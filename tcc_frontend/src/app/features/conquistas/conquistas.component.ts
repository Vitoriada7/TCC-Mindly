import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { finalize } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { Conquista, TrilhaGamificacao } from '../gamificacao/models/gamificacao.models';
import { GamificacaoApiService } from '../gamificacao/services/gamificacao-api.service';

@Component({
  selector: 'app-conquistas',
  templateUrl: './conquistas.component.html',
  styleUrl: './conquistas.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConquistasComponent {
  private readonly gamificacaoApi = inject(GamificacaoApiService);
  private readonly route = inject(ActivatedRoute);
  protected readonly conquistas = signal<Conquista[]>([]);
  protected readonly carregando = signal(true);
  protected readonly erro = signal<string | null>(null);
  protected readonly trilhaSelecionada = signal<TrilhaGamificacao | null>(null);

  protected readonly categorias = computed(() => {
    const definicoes: ReadonlyArray<{ trilha: TrilhaGamificacao; titulo: string; icone: string }> = [
      { trilha: 'HABITOS', titulo: 'Hábitos', icone: 'local_fire_department' },
      { trilha: 'TAREFAS', titulo: 'Tarefas', icone: 'task_alt' },
      { trilha: 'EMOCIONAL', titulo: 'Autocuidado', icone: 'favorite' },
    ];
    const ordem = { CONCLUIDA: 0, EM_PROGRESSO: 1, BLOQUEADA: 2 } as const;
    return definicoes.map((categoria) => ({
      ...categoria,
      conquistas: this.conquistas()
        .filter((conquista) => conquista.trilha === categoria.trilha)
        .sort((a, b) => ordem[a.status] - ordem[b.status]),
    })).sort((a, b) => Number(b.trilha === this.trilhaSelecionada()) - Number(a.trilha === this.trilhaSelecionada()));
  });

  constructor() {
    this.carregar();
    this.route.queryParamMap.subscribe((params) => {
      const trilha = params.get('trilha');
      this.trilhaSelecionada.set(trilha === 'HABITOS' || trilha === 'TAREFAS' || trilha === 'EMOCIONAL' ? trilha : null);
    });
  }

  protected carregar(): void {
    this.carregando.set(true);
    this.erro.set(null);
    this.gamificacaoApi.resumo().pipe(finalize(() => this.carregando.set(false))).subscribe({
      next: (resumo) => this.conquistas.set(resumo.conquistas),
      error: () => this.erro.set('Não foi possível carregar suas conquistas agora.'),
    });
  }

}
