import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin, finalize } from 'rxjs';
import { Categoria, Tarefa } from '../tarefas/models/tarefa.models';
import { TarefaApiService } from '../tarefas/services/tarefa-api.service';
import { UsuarioService } from '../../core/user/usuario.service';
import { HabitoApiService } from '../habitos/services/habito-api.service';
import { ResumoHabitos } from '../habitos/models/habito.models';
import { ResumoGamificacao } from '../gamificacao/models/gamificacao.models';
import { GamificacaoApiService } from '../gamificacao/services/gamificacao-api.service';

@Component({
  selector: 'app-dashboard',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardComponent {
  private readonly tarefaApi = inject(TarefaApiService);
  private readonly usuarioService = inject(UsuarioService);
  private readonly habitoApi = inject(HabitoApiService);
  private readonly gamificacaoApi = inject(GamificacaoApiService);
  private readonly formBuilder = inject(FormBuilder).nonNullable;

  protected readonly carregando = signal(true);
  protected readonly erro = signal<string | null>(null);
  protected readonly tarefas = signal<Tarefa[]>([]);
  protected readonly categorias = signal<Categoria[]>([]);
  protected readonly resumoHabitos = signal<ResumoHabitos | null>(null);
  protected readonly resumoGamificacao = signal<ResumoGamificacao | null>(null);
  protected readonly concluindoIds = signal<ReadonlySet<number>>(new Set());
  protected readonly modalAberto = signal(false);
  protected readonly criando = signal(false);
  protected readonly erroFormulario = signal<string | null>(null);
  protected readonly agora = new Date();

  protected readonly tarefasHoje = computed(() =>
    this.tarefas()
      .filter((tarefa) => tarefa.vencida || this.ehHoje(tarefa.dataLimite))
      .sort((a, b) => Number(b.vencida) - Number(a.vencida)
        || Number(a.status === 'CONCLUIDA') - Number(b.status === 'CONCLUIDA')),
  );
  protected readonly prioridadesHoje = computed(
    () => this.tarefasHoje().filter((tarefa) => tarefa.status !== 'CONCLUIDA').length,
  );
  protected readonly categoriasPorId = computed(
    () => new Map(this.categorias().map((categoria) => [categoria.id, categoria.nome])),
  );
  protected readonly saudacao = computed(() => {
    const hora = this.agora.getHours();
    return hora < 12 ? 'Bom dia' : hora < 18 ? 'Boa tarde' : 'Boa noite';
  });
  protected readonly apelido = computed(() => this.usuarioService.usuario()?.apelido ?? '');
  protected readonly dataCabecalho = new Intl.DateTimeFormat('pt-BR', {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
  }).format(this.agora);

  protected readonly formulario = this.formBuilder.group({
    titulo: ['', [Validators.required, Validators.maxLength(150)]],
    descricao: [''],
    prioridade: ['MEDIA' as const, Validators.required],
    dataLimite: [''],
    categoriaId: [0, [Validators.required, Validators.min(1)]],
  });

  constructor() {
    this.carregar();
    this.usuarioService.obterPerfil().subscribe({ error: () => undefined });
  }

  protected carregar(): void {
    this.carregando.set(true);
    this.erro.set(null);
    forkJoin({ tarefas: this.tarefaApi.listar(), categorias: this.tarefaApi.listarCategorias(), resumoHabitos: this.habitoApi.resumo(), resumoGamificacao: this.gamificacaoApi.resumo() })
      .pipe(finalize(() => this.carregando.set(false)))
      .subscribe({
        next: ({ tarefas, categorias, resumoHabitos, resumoGamificacao }) => {
          this.tarefas.set(tarefas);
          this.categorias.set(categorias);
          this.resumoHabitos.set(resumoHabitos);
          this.resumoGamificacao.set(resumoGamificacao);
          if (!this.formulario.controls.categoriaId.value && categorias[0]) {
            this.formulario.controls.categoriaId.setValue(categorias[0].id);
          }
        },
        error: () => this.erro.set('Não foi possível carregar seu resumo agora. Tente novamente.'),
      });
  }

  protected concluir(tarefa: Tarefa): void {
    if (tarefa.status === 'CONCLUIDA' || this.concluindoIds().has(tarefa.id)) return;

    this.concluindoIds.update((ids) => new Set(ids).add(tarefa.id));
    this.tarefaApi.concluir(tarefa.id).pipe(finalize(() => this.concluindoIds.update((ids) => {
      const proximo = new Set(ids);
      proximo.delete(tarefa.id);
      return proximo;
    }))).subscribe({
      next: (atualizada) => {
        this.tarefas.update((tarefas) => tarefas.map((item) => (item.id === atualizada.id ? atualizada : item)));
        this.atualizarGamificacao();
      },
      error: () => this.erro.set('Não foi possível concluir esta tarefa. Tente novamente.'),
    });
  }

  protected abrirFormulario(): void {
    this.erroFormulario.set(null);
    this.modalAberto.set(true);
  }

  protected fecharFormulario(): void {
    if (!this.criando()) this.modalAberto.set(false);
  }

  protected criarTarefa(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    const dados = this.formulario.getRawValue();
    this.criando.set(true);
    this.erroFormulario.set(null);
    this.tarefaApi.criar({
      titulo: dados.titulo.trim(),
      descricao: dados.descricao.trim() || null,
      prioridade: dados.prioridade,
      dataLimite: dados.dataLimite || null,
      categoriaId: dados.categoriaId,
    }).pipe(finalize(() => this.criando.set(false))).subscribe({
      next: (tarefa) => {
        this.tarefas.update((tarefas) => [tarefa, ...tarefas]);
        this.formulario.reset({ titulo: '', descricao: '', prioridade: 'MEDIA', dataLimite: '', categoriaId: dados.categoriaId });
        this.modalAberto.set(false);
      },
      error: () => this.erroFormulario.set('Não foi possível criar a tarefa. Verifique os dados e tente novamente.'),
    });
  }

  protected prioridadeRotulo(prioridade: Tarefa['prioridade']): string {
    return { ALTA: 'Alta', MEDIA: 'Média', BAIXA: 'Baixa' }[prioridade];
  }

  protected categoriaDa(tarefa: Tarefa): string | undefined {
    return this.categoriasPorId().get(tarefa.categoriaId);
  }

  protected prazoDa(tarefa: Tarefa): string {
    if (!tarefa.dataLimite) return 'Sem horário definido';
    return new Intl.DateTimeFormat('pt-BR', { hour: '2-digit', minute: '2-digit' }).format(new Date(tarefa.dataLimite));
  }

  private ehHoje(valor: string | null): boolean {
    if (!valor) return false;
    const data = new Date(valor);
    return data.getFullYear() === this.agora.getFullYear()
      && data.getMonth() === this.agora.getMonth()
      && data.getDate() === this.agora.getDate();
  }

  private atualizarGamificacao(): void {
    this.gamificacaoApi.resumo().subscribe({
      next: (resumo) => this.resumoGamificacao.set(resumo),
      error: () => undefined,
    });
  }
}
