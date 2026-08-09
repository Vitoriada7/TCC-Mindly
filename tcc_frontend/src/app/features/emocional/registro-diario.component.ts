import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

type Sentimento = { label: string; value: string; icon: string; description: string };
type Pergunta = { titulo: string; texto: string };

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
  protected readonly sentimentoSelecionado = signal<string | null>(null);
  protected readonly sentimentoDetalhadoSelecionado = signal<string | null>(null);
  protected readonly pensamento = signal('');
  protected readonly exploracoes = signal(['', '', '']);
  protected readonly reflexao = signal('');

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
    if (!this.podeAvancar()) return;
    if (this.passo() < 4) {
      this.passo.update((valor) => valor + 1);
      return;
    }

    this.router.navigate(['../'], {
      relativeTo: this.route,
      state: { registroConcluido: true },
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
