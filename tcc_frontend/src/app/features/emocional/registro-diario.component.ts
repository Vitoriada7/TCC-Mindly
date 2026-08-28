import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { EmocionalApiService } from './emocional-api.service';

type Sentimento = { label: string; value: string; icon: string; description: string };
type Pergunta = { titulo: string; texto: string };
type EstadoAudio = 'idle' | 'recording' | 'transcribing' | 'complete' | 'error';
type SpeechRecognitionResultEventLike = Event & { results: { length: number; [index: number]: { [index: number]: { transcript: string } } }; resultIndex: number };
type SpeechRecognitionLike = { lang: string; interimResults: boolean; continuous: boolean; start(): void; stop(): void; onresult: ((event: SpeechRecognitionResultEventLike) => void) | null; onend: (() => void) | null; onerror: (() => void) | null };
type SpeechRecognitionConstructor = new () => SpeechRecognitionLike;

@Component({
  selector: 'app-registro-diario',
  imports: [RouterLink],
  templateUrl: './registro-diario.component.html',
  styleUrl: './registro-diario.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegistroDiarioComponent {
  protected readonly passo = signal(1);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly emocionalApi = inject(EmocionalApiService);
  protected readonly sentimentoSelecionado = signal<string | null>(null);
  protected readonly sentimentoDetalhadoSelecionado = signal<string | null>(null);
  protected readonly pensamento = signal('');
  protected readonly exploracoes = signal(['', '', '']);
  protected readonly reflexao = signal('');
  protected readonly estadoAudio = signal<EstadoAudio>('idle');
  protected readonly transcricao = signal('');
  protected readonly mensagemAudio = signal('');
  protected readonly salvando = signal(false);
  protected readonly erroSalvamento = signal<string | null>(null);
  private mediaRecorder: MediaRecorder | null = null;
  private reconhecimento: SpeechRecognitionLike | null = null;
  private trechosTranscritos = '';

  protected readonly sentimentos: Sentimento[] = [
    { label: 'Radiante', value: 'radiante', icon: 'sentiment_very_satisfied', description: 'Me sinto leve e feliz.' },
    { label: 'Bem', value: 'bem', icon: 'sentiment_satisfied', description: 'Tudo está equilibrado.' },
    { label: 'Neutra', value: 'neutra', icon: 'sentiment_neutral', description: 'Um dia tranquilo e sereno.' },
    { label: 'Cansada', value: 'cansada', icon: 'sentiment_dissatisfied', description: 'Preciso de um momento para mim.' },
    { label: 'Difícil', value: 'dificil', icon: 'sentiment_very_dissatisfied', description: 'Sinto que hoje foi mais pesado.' },
  ];

  protected readonly sentimentosEspecificos = [
    'Acolhido',
    'Sobrecarregado',
    'Confiante',
    'Inquieto',
    'Com foco',
    'Sensível',
    'Aliviado',
    'Inseguro',
  ];

  protected readonly perguntas: Pergunta[] = [
    { titulo: 'Como foi o seu dia?', texto: 'Quais emoções apareceram e o que você sentiu mais forte?' },
    { titulo: 'O que chamou mais atenção?', texto: 'Há algo que você gostaria de lembrar de si mesmo daqui para frente?' },
    { titulo: 'Qual aprendizado você leva?', texto: 'O que pode ajudar você a se sentir mais acolhido amanhã?' },
  ];

  protected readonly passoRotulos = ['Como se sente?', 'Escreva seus pensamentos', 'Explorando sentimentos', 'Reflexão final'];
  protected readonly passos = [1, 2, 3, 4];
  protected readonly indices = [0, 1, 2];

  protected readonly etapaAtiva = computed(() => this.passo());
  protected readonly progressPercent = computed(() => (this.passo() / 4) * 100);

  protected readonly podeAvancar = computed(() => {
    switch (this.passo()) {
      case 1: return Boolean(this.sentimentoSelecionado());
      case 2: return this.pensamento().trim().length > 0;
      case 3: return true;
      default: return this.reflexao().trim().length > 0;
    }
  });

  protected selecionarSentimento(valor: string): void {
    this.sentimentoSelecionado.set(valor);
  }

  protected selecionarDetalhe(valor: string): void {
    this.sentimentoDetalhadoSelecionado.set(valor);
  }

  protected atualizarPensamento(event: Event): void {
    const target = event.target as HTMLTextAreaElement;
    this.pensamento.set(target.value);
  }

  protected async alternarGravacao(): Promise<void> {
    if (this.estadoAudio() === 'recording') {
      this.pararGravacao();
      return;
    }

    if (!navigator.mediaDevices?.getUserMedia) {
      this.estadoAudio.set('error');
      this.mensagemAudio.set('A gravação de áudio não é compatível com este navegador.');
      return;
    }

    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      this.mediaRecorder = new MediaRecorder(stream);
      this.mediaRecorder.onstop = () => stream.getTracks().forEach((track) => track.stop());
      this.mediaRecorder.start();
      this.trechosTranscritos = '';
      this.transcricao.set('');
      this.mensagemAudio.set('Gravando seu relato… toque novamente para finalizar.');
      this.estadoAudio.set('recording');
      this.iniciarTranscricao();
    } catch {
      this.estadoAudio.set('error');
      this.mensagemAudio.set('Não foi possível acessar o microfone. Verifique a permissão e tente novamente.');
    }
  }

  private iniciarTranscricao(): void {
    const reconhecimentoGlobal = window as Window & { SpeechRecognition?: SpeechRecognitionConstructor; webkitSpeechRecognition?: SpeechRecognitionConstructor };
    const ConstrutorReconhecimento = reconhecimentoGlobal.SpeechRecognition ?? reconhecimentoGlobal.webkitSpeechRecognition;
    if (!ConstrutorReconhecimento) return;

    this.reconhecimento = new ConstrutorReconhecimento();
    this.reconhecimento.lang = 'pt-BR';
    this.reconhecimento.interimResults = true;
    this.reconhecimento.continuous = true;
    this.reconhecimento.onresult = (event) => {
      let texto = '';
      for (let index = event.resultIndex; index < event.results.length; index += 1) texto += event.results[index][0].transcript;
      this.transcricao.set(`${this.trechosTranscritos} ${texto}`.trim());
    };
    this.reconhecimento.onerror = () => this.mensagemAudio.set('O áudio foi gravado, mas a transcrição não está disponível neste navegador.');
    this.reconhecimento.onend = () => {
      if (this.estadoAudio() === 'transcribing') this.finalizarTranscricao();
    };
    this.reconhecimento.start();
  }

  private pararGravacao(): void {
    this.mediaRecorder?.stop();
    this.reconhecimento?.stop();
    this.estadoAudio.set('transcribing');
    this.mensagemAudio.set('Transcrevendo seu relato…');
    if (!this.reconhecimento) window.setTimeout(() => this.finalizarTranscricao(), 900);
  }

  private finalizarTranscricao(): void {
    const texto = this.transcricao().trim();
    this.estadoAudio.set('complete');
    this.mensagemAudio.set(texto ? 'Transcrição pronta. Você pode continuar editando o seu relato.' : 'Áudio gravado. A transcrição não foi identificada neste navegador.');
    if (texto && !this.pensamento().includes(texto)) this.pensamento.update((atual) => `${atual}${atual ? '\n\n' : ''}${texto}`);
  }

  protected atualizarExploracao(index: number, event: Event): void {
    const target = event.target as HTMLTextAreaElement;
    const valores = [...this.exploracoes()];
    valores[index] = target.value;
    this.exploracoes.set(valores);
  }

  protected trackByPergunta(_index: number, pergunta: Pergunta): string {
    return pergunta.titulo;
  }

  protected atualizarReflexao(event: Event): void {
    const target = event.target as HTMLTextAreaElement;
    this.reflexao.set(target.value);
  }

  protected avancar(): void {
    if (!this.podeAvancar() || this.salvando()) return;
    if (this.passo() < 4) {
      this.passo.update((valor) => valor + 1);
      return;
    }

    this.salvando.set(true);
    this.erroSalvamento.set(null);
    this.emocionalApi.registrar({
      sentimento: this.sentimentoSelecionado()!,
      sentimentoDetalhado: this.sentimentoDetalhadoSelecionado(),
      pensamento: this.pensamento().trim() || null,
      exploracoes: this.exploracoes().map((valor) => valor.trim()),
      reflexao: this.reflexao().trim() || null,
    }).pipe(finalize(() => this.salvando.set(false))).subscribe({
      next: () => this.router.navigate(['../'], {
        relativeTo: this.route,
        state: { registroConcluido: true },
      }),
      error: () => this.erroSalvamento.set('Não foi possível salvar seu registro. Tente novamente.'),
    });
  }

  protected voltar(): void {
    if (this.passo() > 1) {
      this.passo.update((valor) => valor - 1);
    }
  }

  protected get tituloAtual(): string {
    return this.passoRotulos[this.passo() - 1];
  }
}
