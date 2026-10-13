import { CarregamentoComponent } from '../../shared/components/carregamento/carregamento.component';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { EmocionalApiService } from './emocional-api.service';
import { catchError, finalize, forkJoin, of } from 'rxjs';
import { ResumoGamificacao } from '../gamificacao/models/gamificacao.models';
import { GamificacaoApiService } from '../gamificacao/services/gamificacao-api.service';
import { RegistroEmocional } from './emocional.models';
import { RegistroDetalhesComponent } from './registro-detalhes.component';

type Humor = { label: string; value: string; icon: string; description: string };

@Component({
  selector: 'app-emocional',
  imports: [CarregamentoComponent, RouterLink, RegistroDetalhesComponent],
  templateUrl: './emocional.component.html',
  styleUrl: './emocional.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmocionalComponent {
  private readonly emocionalApi = inject(EmocionalApiService);
  private readonly gamificacaoApi = inject(GamificacaoApiService);
  protected readonly humores: Humor[] = [
    { label: 'Radiante', value: 'radiante', icon: 'sentiment_very_satisfied', description: 'Me sinto leve e feliz.' },
    { label: 'Bem', value: 'bem', icon: 'sentiment_satisfied', description: 'Tudo está equilibrado.' },
    { label: 'Neutra', value: 'neutra', icon: 'sentiment_neutral', description: 'Um dia tranquilo e sereno.' },
    { label: 'Cansada', value: 'cansada', icon: 'sentiment_dissatisfied', description: 'Preciso de um momento para mim.' },
    { label: 'Difícil', value: 'dificil', icon: 'sentiment_very_dissatisfied', description: 'Sinto que hoje foi mais pesado.' },
  ];

  protected readonly humorSelecionado = signal<string | null>(null);
  protected readonly carregando = signal(true);
  private readonly datasRegistradas = signal<Date[]>([]);
  private readonly registros = signal<RegistroEmocional[]>([]);
  protected readonly registroSelecionado = signal<RegistroEmocional | null>(null);
  protected readonly sentimentoRegistro = computed(() => {
    const sentimento = this.registroSelecionado()?.sentimento;
    return this.humores.find((humor) => humor.value === sentimento)?.label ?? sentimento ?? '';
  });
  protected readonly mesSelecionado = signal(new Date(new Date().getFullYear(), new Date().getMonth(), 1));
  protected readonly diasRegistrados = computed(() => {
    const mes = this.mesSelecionado();
    return new Set(this.datasRegistradas()
      .filter((data) => data.getFullYear() === mes.getFullYear() && data.getMonth() === mes.getMonth())
      .map((data) => data.getDate()));
  });
  protected readonly resumoGamificacao = signal<ResumoGamificacao | null>(null);
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
    new Intl.DateTimeFormat('pt-BR', { month: 'long', year: 'numeric' }).format(this.mesSelecionado()),
  );

  protected readonly primeirosDias = computed(() => {
    const primeiroDia = this.mesSelecionado().getDay();
    return Array.from({ length: primeiroDia }, () => '');
  });

  protected readonly diasDoMes = computed(() => {
    const mes = this.mesSelecionado();
    const total = new Date(mes.getFullYear(), mes.getMonth() + 1, 0).getDate();
    return Array.from({ length: total }, (_, index) => index + 1);
  });

  protected readonly diasSemana = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sab'];

  constructor() {
    const state = window.history.state as { registroConcluido?: boolean } | null;
    if (state?.registroConcluido) {
      this.mostrarToast();
      window.history.replaceState({}, '', window.location.href);
    }
    this.carregarRegistros();
  }

  private carregarRegistros(): void {
    forkJoin({
      registros: this.emocionalApi.listar(),
      gamificacao: this.gamificacaoApi.resumo().pipe(catchError(() => of(null))),
    }).pipe(finalize(() => this.carregando.set(false))).subscribe({
      next: ({ registros, gamificacao }) => {
        this.registros.set(registros);
        this.resumoGamificacao.set(gamificacao);
        const ano = this.dataAtual.getFullYear();
        const mes = this.dataAtual.getMonth();
        const datas = registros.map((registro) => new Date(registro.dataRegistro));
        this.datasRegistradas.set(datas);
        const registroHoje = registros.find((_registro, index) => {
          const data = datas[index];
          return data.getFullYear() === ano && data.getMonth() === mes && data.getDate() === this.dataAtual.getDate();
        });
        this.humorSelecionado.set(registroHoje?.sentimento ?? null);
      },
      error: () => undefined,
    });
  }

  protected navegarMes(deslocamento: number): void {
    this.mesSelecionado.update((mes) => new Date(mes.getFullYear(), mes.getMonth() + deslocamento, 1));
  }

  protected abrirRegistro(dia: number): void {
    const mes = this.mesSelecionado();
    const registro = this.registros().find((item) => {
      const data = new Date(item.dataRegistro);
      return data.getFullYear() === mes.getFullYear() &&
        data.getMonth() === mes.getMonth() && data.getDate() === dia;
    });
    this.registroSelecionado.set(registro ?? null);
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
