import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin, finalize } from 'rxjs';
import { Categoria, Prioridade, StatusTarefa, Tarefa } from './models/tarefa.models';
import { TarefaApiService } from './services/tarefa-api.service';

@Component({
  selector: 'app-tarefas',
  imports: [ReactiveFormsModule],
  templateUrl: './tarefas.component.html',
  styleUrl: './tarefas.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TarefasComponent {
  private readonly tarefaApi = inject(TarefaApiService);
  private readonly formBuilder = inject(FormBuilder).nonNullable;

  protected readonly carregando = signal(true);
  protected readonly erro = signal<string | null>(null);
  protected readonly tarefas = signal<Tarefa[]>([]);
  protected readonly categorias = signal<Categoria[]>([]);
  protected readonly statusSelecionado = signal<StatusTarefa | 'VENCIDA' | undefined>(undefined);
  protected readonly prioridadeSelecionada = signal<Prioridade | undefined>(undefined);
  protected readonly categoriaSelecionada = signal<number | undefined>(undefined);
  protected readonly concluindoIds = signal<ReadonlySet<number>>(new Set());
  protected readonly processando = signal(false);
  protected readonly modalTarefaAberto = signal(false);
  protected readonly diaSelecionado = signal<Date | null>(null);
  protected readonly arrastandoCalendario = signal(false);
  private inicioArrasteCalendario = 0;
  private scrollInicialCalendario = 0;
  protected readonly tarefasFiltradas = computed(() => {
    const selecionado = this.diaSelecionado();
    if (!selecionado) return this.tarefas();

    return this.tarefas().filter((tarefa) => {
      if (!tarefa.dataLimite) return false;
      const limite = new Date(tarefa.dataLimite);
      return limite.toDateString() === selecionado.toDateString();
    });
  });
  protected readonly contextoDaLista = computed(() => {
    const selecionado = this.diaSelecionado();
    if (!selecionado) return 'Todas as tarefas';

    const data = new Intl.DateTimeFormat('pt-BR', {
      day: '2-digit',
      month: 'long',
    }).format(selecionado);

    return `Tarefas com prazo em ${data}`;
  });
  protected readonly diasCalendario = computed(() => {
    const dataCentral = this.diaSelecionado() ?? new Date();
    const inicio = new Date(dataCentral);
    inicio.setDate(dataCentral.getDate() - 3);
    return Array.from({ length: 7 }, (_, index) => {
      const data = new Date(inicio);
      data.setDate(inicio.getDate() + index);
      return {
        iso: data.toISOString(),
        weekday: new Intl.DateTimeFormat('pt-BR', { weekday: 'short' }).format(data),
        day: data.getDate(),
        month: new Intl.DateTimeFormat('pt-BR', { month: 'short' }).format(data).replace('.', ''),
        date: data,
      };
    });
  });
  protected readonly modalCategoriasAberto = signal(false);
  protected readonly tarefaEmEdicao = signal<Tarefa | null>(null);
  protected readonly erroFormulario = signal<string | null>(null);
  protected readonly categoriaEditadaId = signal<number | null>(null);
  protected readonly concluidas = computed(() => this.tarefas().filter((tarefa) => tarefa.status === 'CONCLUIDA').length);

  protected readonly formularioTarefa = this.formBuilder.group({
    titulo: ['', [Validators.required, Validators.maxLength(150)]],
    descricao: [''],
    prioridade: ['MEDIA' as Prioridade, Validators.required],
    dataLimite: [''],
    categoriaId: [0, [Validators.required, Validators.min(1)]],
  });
  protected readonly formularioCategoria = this.formBuilder.group({ nome: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]] });

  constructor() { this.carregarCategoriasETarefas(); }

  protected selecionarStatus(status: StatusTarefa | 'VENCIDA' | undefined): void { this.statusSelecionado.set(status); this.carregarTarefas(); }
  protected selecionarPrioridade(prioridade: Prioridade | undefined): void { this.prioridadeSelecionada.set(prioridade); this.carregarTarefas(); }
  protected selecionarCategoria(categoria: number | undefined): void { this.categoriaSelecionada.set(categoria); this.carregarTarefas(); }

  protected abrirCriacao(): void {
    this.tarefaEmEdicao.set(null); this.erroFormulario.set(null);
    this.formularioTarefa.reset({ titulo: '', descricao: '', prioridade: 'MEDIA', dataLimite: '', categoriaId: this.categorias()[0]?.id ?? 0 });
    this.modalTarefaAberto.set(true);
  }

  protected abrirEdicao(tarefa: Tarefa): void {
    this.tarefaEmEdicao.set(tarefa); this.erroFormulario.set(null);
    this.formularioTarefa.reset({ titulo: tarefa.titulo, descricao: tarefa.descricao ?? '', prioridade: tarefa.prioridade, dataLimite: tarefa.dataLimite?.slice(0, 16) ?? '', categoriaId: tarefa.categoriaId });
    this.modalTarefaAberto.set(true);
  }

  protected fecharModalTarefa(): void { if (!this.processando()) this.modalTarefaAberto.set(false); }
  protected abrirCategorias(): void { this.erroFormulario.set(null); this.categoriaEditadaId.set(null); this.formularioCategoria.reset({ nome: '' }); this.modalCategoriasAberto.set(true); }
  protected fecharCategorias(): void { if (!this.processando()) this.modalCategoriasAberto.set(false); }

  protected salvarTarefa(): void {
    if (this.formularioTarefa.invalid) { this.formularioTarefa.markAllAsTouched(); return; }
    const dados = this.formularioTarefa.getRawValue();
    const request = { titulo: dados.titulo.trim(), descricao: dados.descricao.trim() || null, prioridade: dados.prioridade, dataLimite: dados.dataLimite || null, categoriaId: dados.categoriaId };
    const tarefa = this.tarefaEmEdicao();
    this.processando.set(true); this.erroFormulario.set(null);
    const requisicao = tarefa ? this.tarefaApi.atualizar(tarefa.id, request) : this.tarefaApi.criar(request);
    requisicao.pipe(finalize(() => this.processando.set(false))).subscribe({
      next: (tarefaSalva) => {
        this.tarefas.update((tarefas) => {
          const tarefaJaExiste = tarefas.some((item) => item.id === tarefaSalva.id);

          return tarefaJaExiste
            ? tarefas.map((item) => (item.id === tarefaSalva.id ? tarefaSalva : item))
            : [tarefaSalva, ...tarefas];
        });
        this.modalTarefaAberto.set(false);
      },
      error: () => this.erroFormulario.set('Não foi possível salvar a tarefa. Verifique os dados e tente novamente.'),
    });
  }

  protected concluir(tarefa: Tarefa): void {
    if (tarefa.status === 'CONCLUIDA' || this.concluindoIds().has(tarefa.id)) return;
    this.concluindoIds.update((ids) => new Set(ids).add(tarefa.id));
    this.tarefaApi.concluir(tarefa.id).pipe(finalize(() => this.concluindoIds.update((ids) => { const proximo = new Set(ids); proximo.delete(tarefa.id); return proximo; }))).subscribe({
      next: () => this.carregarTarefas(),
      error: () => this.erro.set('Não foi possível concluir esta tarefa. Tente novamente.'),
    });
  }

  protected excluirTarefa(tarefa: Tarefa): void {
    if (!confirm(`Excluir a tarefa "${tarefa.titulo}"?`)) return;
    this.processando.set(true);
    this.tarefaApi.excluir(tarefa.id).pipe(finalize(() => this.processando.set(false))).subscribe({ next: () => this.carregarTarefas(), error: () => this.erro.set('Não foi possível excluir esta tarefa.') });
  }

  protected editarCategoria(categoria: Categoria): void { this.categoriaEditadaId.set(categoria.id); this.formularioCategoria.setValue({ nome: categoria.nome }); }

  protected selecionarDia(data: Date | null): void {
    this.diaSelecionado.set(data ? new Date(data) : null);
  }

  protected selecionarDiaPorToque(evento: PointerEvent, data: Date): void {
    if (evento.pointerType === 'touch' || evento.pointerType === 'pen') {
      this.selecionarDia(data);
    }
  }

  protected iniciarArrasteCalendario(evento: PointerEvent, calendario: HTMLElement): void {
    if (evento.pointerType !== 'mouse' || evento.button !== 0) return;

    this.arrastandoCalendario.set(true);
    this.inicioArrasteCalendario = evento.clientX;
    this.scrollInicialCalendario = calendario.scrollLeft;
  }

  protected arrastarCalendario(evento: PointerEvent, calendario: HTMLElement): void {
    if (evento.pointerType !== 'mouse' || !this.arrastandoCalendario()) return;

    const deslocamento = evento.clientX - this.inicioArrasteCalendario;
    calendario.scrollLeft = this.scrollInicialCalendario - deslocamento;
  }

  protected finalizarArrasteCalendario(): void {
    if (!this.arrastandoCalendario()) return;

    this.arrastandoCalendario.set(false);
  }

  protected diaAtivo(data: Date): boolean {
    return data.toDateString() === this.diaSelecionado()?.toDateString();
  }
  protected salvarCategoria(): void {
    if (this.formularioCategoria.invalid) { this.formularioCategoria.markAllAsTouched(); return; }
    const id = this.categoriaEditadaId(); const nome = this.formularioCategoria.getRawValue().nome.trim();
    this.processando.set(true); this.erroFormulario.set(null);
    const requisicao = id ? this.tarefaApi.atualizarCategoria(id, { nome }) : this.tarefaApi.criarCategoria({ nome });
    requisicao.pipe(finalize(() => this.processando.set(false))).subscribe({
      next: (categoria) => { this.categorias.update((itens) => id ? itens.map((item) => item.id === categoria.id ? categoria : item) : [...itens, categoria].sort((a, b) => a.nome.localeCompare(b.nome))); this.categoriaEditadaId.set(null); this.formularioCategoria.reset({ nome: '' }); },
      error: () => this.erroFormulario.set('Não foi possível salvar a categoria.'),
    });
  }

  protected excluirCategoria(categoria: Categoria): void {
    if (!confirm(`Excluir a categoria "${categoria.nome}"?`)) return;
    this.processando.set(true);
    this.tarefaApi.excluirCategoria(categoria.id).pipe(finalize(() => this.processando.set(false))).subscribe({
      next: () => this.categorias.update((itens) => itens.filter((item) => item.id !== categoria.id)),
      error: () => this.erroFormulario.set('Não foi possível excluir a categoria. Categorias com tarefas vinculadas não podem ser excluídas.'),
    });
  }

  protected prioridadeRotulo(prioridade: Prioridade): string { return { ALTA: 'Alta', MEDIA: 'Média', BAIXA: 'Baixa' }[prioridade]; }
  protected categoriaDa(tarefa: Tarefa): string | undefined { return this.categorias().find((categoria) => categoria.id === tarefa.categoriaId)?.nome; }
  protected dataDa(tarefa: Tarefa): string { return tarefa.dataLimite ? new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' }).format(new Date(tarefa.dataLimite)) : 'Sem prazo'; }

  private carregarCategoriasETarefas(): void {
    forkJoin({ categorias: this.tarefaApi.listarCategorias(), tarefas: this.tarefaApi.listar() }).pipe(finalize(() => this.carregando.set(false))).subscribe({
      next: ({ categorias, tarefas }) => { this.categorias.set(categorias); this.tarefas.set(tarefas); }, error: () => this.erro.set('Não foi possível carregar suas tarefas.'),
    });
  }
  private carregarTarefas(): void {

    this.carregando.set(true);
    this.erro.set(null);
  
    const status = this.statusSelecionado();
  
    this.tarefaApi
      .listar(
        status === 'VENCIDA' ? undefined : status,
        this.prioridadeSelecionada(),
        this.categoriaSelecionada(),
        status === 'VENCIDA'
      )
      .pipe(finalize(() => this.carregando.set(false)))
      .subscribe({
        next: (tarefas) => this.tarefas.set(tarefas),
        error: () =>
          this.erro.set('Não foi possível filtrar suas tarefas.'),
      });
  }
}
