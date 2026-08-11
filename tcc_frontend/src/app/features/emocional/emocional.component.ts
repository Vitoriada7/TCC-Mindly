import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

type Humor = { label: string; value: string; icon: string; description: string };

@Component({
  selector: 'app-emocional',
  imports: [RouterLink],
  templateUrl: './emocional.component.html',
  styleUrl: './emocional.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmocionalComponent {
  protected readonly humores: Humor[] = [
    { label: 'Radiante', value: 'radiante', icon: 'sentiment_very_satisfied', description: 'Me sinto leve e feliz.' },
    { label: 'Bem', value: 'bem', icon: 'sentiment_satisfied', description: 'Tudo está equilibrado.' },
    { label: 'Neutra', value: 'neutra', icon: 'sentiment_neutral', description: 'Um dia tranquilo e sereno.' },
    { label: 'Cansada', value: 'cansada', icon: 'sentiment_dissatisfied', description: 'Preciso de um momento para mim.' },
    { label: 'Difícil', value: 'dificil', icon: 'sentiment_very_dissatisfied', description: 'Sinto que hoje foi mais pesado.' },
  ];

  protected readonly humorSelecionado = signal<string | null>(null);
  protected readonly diasRegistrados = signal(new Set<number>([2, 4, 7, 9, 12, 15, 18, 20, 22]));
  protected readonly toastVisivel = signal(false);
  protected readonly toastMensagem = signal('Seu registro diário foi concluído com sucesso!');
  protected readonly dataAtual = new Date();

  protected readonly resumo = computed(() => {
    const valor = this.humorSelecionado();
    if (!valor) {
      return 'Selecione um estado emocional para marcar como seu hoje.';
    }

    const humor = this.humores.find((item) => item.value === valor);
    return `Hoje você se sente ${humor?.label.toLowerCase() ?? valor}.`;
  });

  protected readonly mesAtual = computed(() =>
    new Intl.DateTimeFormat('pt-BR', { month: 'long', year: 'numeric' }).format(this.dataAtual),
  );

  protected readonly primeirosDias = computed(() => {
    const primeiroDia = new Date(this.dataAtual.getFullYear(), this.dataAtual.getMonth(), 1).getDay();
    return Array.from({ length: primeiroDia }, () => '');
  });

  protected readonly diasDoMes = computed(() => {
    const total = new Date(this.dataAtual.getFullYear(), this.dataAtual.getMonth() + 1, 0).getDate();
    return Array.from({ length: total }, (_, index) => index + 1);
  });

  protected readonly diasSemana = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sab'];

  constructor() {
    const state = window.history.state as { registroConcluido?: boolean } | null;
    if (state?.registroConcluido) {
      this.mostrarToast();
      window.history.replaceState({}, '', window.location.href);
    }
  }

  protected selecionarHumor(valor: string): void {
    this.humorSelecionado.set(valor);
  }

  protected mostrarToast(): void {
    this.toastVisivel.set(true);
    window.setTimeout(() => this.toastVisivel.set(false), 4200);
  }

  protected fecharToast(): void {
    this.toastVisivel.set(false);
  }
}
