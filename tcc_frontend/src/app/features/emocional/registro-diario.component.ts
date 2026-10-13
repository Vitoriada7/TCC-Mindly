import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  ElementRef,
  effect,
  viewChild,
  computed,
  inject,
  signal,
} from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { finalize, map } from 'rxjs';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { EmocionalApiService } from './emocional-api.service';

type Sentimento = { label: string; value: string; icon: string; description: string };
type Pergunta = { titulo: string; texto: string };
type EstadoAudio = 'idle' | 'requesting' | 'recording' | 'transcribing' | 'complete' | 'error';
@Component({
  selector: 'app-registro-diario',
  imports: [RouterLink, ReactiveFormsModule],
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
  protected readonly complementoFinal = new FormControl('', { nonNullable: true });
  private readonly carregamentoDialog =
    viewChild<ElementRef<HTMLDialogElement>>('carregamentoDialog');
  protected readonly processandoIa = computed(
    () => this.gerando() || this.estadoAudio() === 'transcribing',
  );
  protected readonly mensagemCarregamento = computed(() =>
    this.estadoAudio() === 'transcribing'
      ? 'Transformando sua voz em palavras...'
      : this.passo() === 2
        ? 'Preparando perguntas para voc\u00ea...'
        : 'Preparando sua reflex\u00e3o final...',
  );
  protected readonly estadoAudio = signal<EstadoAudio>('idle');
  protected readonly transcricao = signal('');
  protected readonly analisarExpressaoVocal = new FormControl(false, { nonNullable: true });
  protected readonly usarObservacoesVocais = new FormControl(false, { nonNullable: true });
  protected readonly observacoesVocais = signal<string[]>([]);
  protected readonly avisoAnaliseVocal = signal('');
  protected readonly mensagemAudio = signal('');
  protected readonly salvando = signal(false);
  protected readonly erroSalvamento = signal<string | null>(null);
  private mediaRecorder: MediaRecorder | null = null;
  private readonly destroyRef = inject(DestroyRef);
  private limiteGravacao: number | undefined;
  protected readonly gerando = signal(false);
  protected readonly erroIa = signal<string | null>(null);

  constructor() {
    effect((onCleanup) => {
      const dialog = this.carregamentoDialog()?.nativeElement;
      if (!dialog || !this.processandoIa()) return;
      const focoAnterior =
        document.activeElement instanceof HTMLElement ? document.activeElement : null;
      const overflowAnterior = document.body.style.overflow;
      document.body.style.overflow = 'hidden';
      dialog.showModal();
      onCleanup(() => {
        dialog.close();
        document.body.style.overflow = overflowAnterior;
        if (focoAnterior?.isConnected) focoAnterior.focus();
      });
    });
    this.destroyRef.onDestroy(() => {
      window.clearTimeout(this.limiteGravacao);
      if (this.mediaRecorder) {
        this.mediaRecorder.onstop = null;
        if (this.mediaRecorder.state === 'recording') this.mediaRecorder.stop();
        this.mediaRecorder.stream.getTracks().forEach((track) => track.stop());
      }
    });
  }

  protected readonly sentimentos: Sentimento[] = [
    {
      label: 'Radiante',
      value: 'radiante',
      icon: 'sentiment_very_satisfied',
      description: 'Me sinto leve e feliz.',
    },
    {
      label: 'Bem',
      value: 'bem',
      icon: 'sentiment_satisfied',
      description: 'Tudo está equilibrado.',
    },
    {
      label: 'Neutra',
      value: 'neutra',
      icon: 'sentiment_neutral',
      description: 'Um dia tranquilo e sereno.',
    },
    {
      label: 'Cansada',
      value: 'cansada',
      icon: 'sentiment_dissatisfied',
      description: 'Preciso de um momento para mim.',
    },
    {
      label: 'Difícil',
      value: 'dificil',
      icon: 'sentiment_very_dissatisfied',
      description: 'Sinto que hoje foi mais pesado.',
    },
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

  protected readonly perguntas = signal<Pergunta[]>([]);

  protected readonly passoRotulos = [
    'Como se sente?',
    'Escreva seus pensamentos',
    'Explorando sentimentos',
    'Reflexão final',
  ];
  protected readonly passos = [1, 2, 3, 4];
  protected readonly indices = [0, 1, 2];

  protected readonly etapaAtiva = computed(() => this.passo());
  protected readonly progressPercent = computed(() => (this.passo() / 4) * 100);

  protected readonly podeAvancar = computed(() => {
    switch (this.passo()) {
      case 1:
        return Boolean(this.sentimentoSelecionado());
      case 2:
        return this.pensamento().trim().length > 0;
      case 3:
        return true;
      default:
        return true;
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
      this.mediaRecorder?.stop();
      return;
    }
    if (['requesting', 'transcribing'].includes(this.estadoAudio()) || this.gerando()) return;
    this.estadoAudio.set('requesting');
    this.mensagemAudio.set('Aguardando acesso ao microfone...');
    let stream: MediaStream | undefined;
    try {
      stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      if (this.destroyRef.destroyed) {
        stream.getTracks().forEach((track) => track.stop());
        return;
      }
      const mime = ['audio/webm', 'audio/mp4', 'audio/ogg'].find((tipo) =>
        MediaRecorder.isTypeSupported(tipo),
      );
      if (!mime) throw new Error('Formato indisponivel');
      const recorder = new MediaRecorder(stream, { mimeType: mime });
      this.mediaRecorder = recorder;
      const analisarVoz = this.analisarExpressaoVocal.value;
      const partes: Blob[] = [];
      let tamanho = 0;
      recorder.ondataavailable = (event) => {
        partes.push(event.data);
        tamanho += event.data.size;
        if (tamanho > 7 * 1024 * 1024 && recorder.state === 'recording') recorder.stop();
      };
      recorder.onerror = () => {
        window.clearTimeout(this.limiteGravacao);
        recorder.onstop = null;
        recorder.stream.getTracks().forEach((track) => track.stop());
        this.estadoAudio.set('error');
        this.mensagemAudio.set('A gravação falhou. Tente novamente ou escreva seu relato.');
      };
      recorder.onstop = () => {
        window.clearTimeout(this.limiteGravacao);
        recorder.stream.getTracks().forEach((track) => track.stop());
        if (this.destroyRef.destroyed) return;
        this.estadoAudio.set('transcribing');
        this.mensagemAudio.set(
          analisarVoz ? 'Transcrevendo e observando sua voz...' : 'Transcrevendo seu relato...',
        );
        const audio = new Blob(partes, { type: mime });
        const requisicao = analisarVoz
          ? this.emocionalApi.analisarVoz(audio)
          : this.emocionalApi
              .transcrever(audio)
              .pipe(
                map(({ texto }) => ({
                  texto,
                  analiseDisponivel: false,
                  observacoesVocais: [] as string[],
                })),
              );
        requisicao.subscribe({
          next: (resultado) => {
            const { texto } = resultado;
            if (this.destroyRef.destroyed) return;
            if (resultado.analiseDisponivel) {
              this.observacoesVocais.set(resultado.observacoesVocais);
            } else if (analisarVoz && texto) {
              this.avisoAnaliseVocal.set(
                'Sua transcrição está pronta, mas não foi possível observar a expressão vocal com clareza.',
              );
            }
            this.transcricao.set(texto);
            this.estadoAudio.set(texto ? 'complete' : 'error');
            this.mensagemAudio.set(
              texto
                ? 'Transcrição pronta. Revise e edite seu relato se quiser.'
                : 'Não identificamos fala. Tente novamente ou escreva seu relato.',
            );
            if (texto)
              this.pensamento.update((atual) =>
                `${atual}${atual ? '\n\n' : ''}${texto}`.slice(0, 10000),
              );
          },
          error: () => {
            if (this.destroyRef.destroyed) return;
            this.estadoAudio.set('error');
            this.mensagemAudio.set(
              'Não foi possível transcrever. Tente novamente ou escreva seu relato.',
            );
          },
        });
      };
      recorder.start(1000);
      this.transcricao.set('');
      this.observacoesVocais.set([]);
      this.avisoAnaliseVocal.set('');
      this.usarObservacoesVocais.setValue(false);
      this.estadoAudio.set('recording');
      this.mensagemAudio.set('Gravando... toque para finalizar. Limite de 2 minutos.');
      this.limiteGravacao = window.setTimeout(() => {
        if (recorder.state === 'recording') recorder.stop();
      }, 120000);
    } catch {
      stream?.getTracks().forEach((track) => track.stop());
      this.estadoAudio.set('error');
      this.mensagemAudio.set(
        'Não foi possível gravar. Verifique a permissão do microfone ou escreva seu relato.',
      );
    }
  }

  protected atualizarExploracao(index: number, event: Event): void {
    const target = event.target as HTMLTextAreaElement;
    const valores = [...this.exploracoes()];
    valores[index] = target.value;
    this.exploracoes.set(valores);
  }

  private contextoIa() {
    return {
      sentimento: this.sentimentoSelecionado()!,
      sentimentoDetalhado: this.sentimentoDetalhadoSelecionado(),
      pensamento: this.pensamento().trim(),
      perguntas: this.perguntas().map((item) => item.texto),
      respostas: this.exploracoes().map((texto) => texto.trim()),
      observacoesVocais: this.usarObservacoesVocais.value ? this.observacoesVocais() : [],
    };
  }

  protected continuarSemIa(): void {
    if (this.gerando()) return;
    this.erroIa.set(null);
    if (this.passo() === 2) {
      this.perguntas.set([]);
      this.exploracoes.set(['', '', '']);
    }
    this.reflexao.set('');
    this.passo.set(4);
  }

  protected avancar(): void {
    if (
      !this.podeAvancar() ||
      this.salvando() ||
      this.gerando() ||
      ['requesting', 'recording', 'transcribing'].includes(this.estadoAudio())
    )
      return;
    this.erroIa.set(null);
    if (this.passo() === 1) {
      this.passo.set(2);
      return;
    }
    if (this.passo() === 2) {
      this.gerando.set(true);
      this.emocionalApi
        .gerarPerguntas(this.contextoIa())
        .pipe(finalize(() => this.gerando.set(false)))
        .subscribe({
          next: ({ perguntas }) => {
            if (this.destroyRef.destroyed) return;
            this.perguntas.set(
              perguntas.map((texto, index) => ({ titulo: `Pergunta ${index + 1}`, texto })),
            );
            this.exploracoes.set(['', '', '']);
            this.reflexao.set('');
            this.passo.set(3);
          },
          error: () =>
            this.erroIa.set(
              'Não foi possível preparar as perguntas. Tente novamente ou continue sem IA.',
            ),
        });
      return;
    }
    if (this.passo() === 3) {
      this.gerando.set(true);
      this.emocionalApi
        .gerarApoio(this.contextoIa())
        .pipe(finalize(() => this.gerando.set(false)))
        .subscribe({
          next: ({ texto }) => {
            if (this.destroyRef.destroyed) return;
            this.reflexao.set(texto);
            this.passo.set(4);
          },
          error: () =>
            this.erroIa.set(
              'Não foi possível preparar a mensagem. Tente novamente ou finalize sem IA.',
            ),
        });
      return;
    }

    this.salvando.set(true);
    this.erroSalvamento.set(null);
    this.emocionalApi
      .registrar({
        sentimento: this.sentimentoSelecionado()!,
        sentimentoDetalhado: this.sentimentoDetalhadoSelecionado(),
        pensamento: this.pensamento().trim() || null,
        exploracoes: this.perguntas().map(
          (item, index) => `${item.texto}\n${this.exploracoes()[index].trim().slice(0, 3600)}`,
        ),
        reflexao: this.reflexao().trim() || null,
        complementoFinal: this.complementoFinal.value.trim() || null,
      })
      .pipe(finalize(() => this.salvando.set(false)))
      .subscribe({
        next: () =>
          this.router.navigate(['../'], {
            relativeTo: this.route,
            state: { registroConcluido: true },
          }),
        error: () =>
          this.erroSalvamento.set('Não foi possível salvar seu registro. Tente novamente.'),
      });
  }

  protected voltar(): void {
    if (
      this.gerando() ||
      this.salvando() ||
      ['requesting', 'recording', 'transcribing'].includes(this.estadoAudio())
    )
      return;
    this.erroIa.set(null);
    if (this.passo() > 1) {
      this.passo.update((valor) => valor - 1);
    }
  }

  protected get tituloAtual(): string {
    return this.passoRotulos[this.passo() - 1];
  }
}
