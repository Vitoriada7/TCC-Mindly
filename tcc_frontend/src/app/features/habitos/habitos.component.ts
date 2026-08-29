import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { finalize, forkJoin } from 'rxjs';
import { Habito } from './models/habito.models';
import { HabitoApiService } from './services/habito-api.service';
import { ResumoGamificacao } from '../gamificacao/models/gamificacao.models';
import { GamificacaoApiService } from '../gamificacao/services/gamificacao-api.service';

type DiaSemana = { data: string; rotulo: string; dia: number };

@Component({
  selector: 'app-habitos',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './habitos.component.html',
  styleUrl: './habitos.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HabitosComponent {
  private readonly habitoApi = inject(HabitoApiService);
  private readonly gamificacaoApi = inject(GamificacaoApiService);
  private readonly formBuilder = inject(FormBuilder).nonNullable;
  private readonly route = inject(ActivatedRoute);
  private readonly hoje = new Date();

  protected readonly carregando = signal(true);
  protected readonly erro = signal<string | null>(null);
  protected readonly erroFormulario = signal<string | null>(null);
  protected readonly processando = signal(false);
  protected readonly habitos = signal<Habito[]>([]);
  protected readonly sequenciaAtual = signal(0);
  protected readonly resumoGamificacao = signal<ResumoGamificacao | null>(null);
  protected readonly habitoEmEdicao = signal<Habito | null>(null);
  protected readonly modalAberto = signal(false);
  protected readonly dias = this.criarDiasSemana();
  protected readonly hojeIso = this.formatarData(this.hoje);
  protected readonly totalConcluidosSemana = computed(() =>
    this.habitos().reduce((total, habito) => total + habito.diasConcluidos.length, 0),
  );
  protected readonly formulario = this.formBuilder.group({
    nome: ['', [Validators.required, Validators.maxLength(100)]],
    icone: ['self_improvement', [Validators.required, Validators.maxLength(50)]],
    cor: ['green', [Validators.required, Validators.maxLength(30)]],
  });

  constructor() {
    this.carregar();
    this.route.queryParamMap.subscribe((params) => {
      if (params.get('novo') !== 'true') return;
      this.habitoEmEdicao.set(null);
      this.erroFormulario.set(null);
      this.formulario.reset({ nome: params.get('nome') ?? '', icone: params.get('icone') ?? 'self_improvement', cor: params.get('cor') ?? 'green' });
      this.modalAberto.set(true);
    });
  }

  protected abrirCriacao(): void {
    this.habitoEmEdicao.set(null);
    this.erroFormulario.set(null);
    this.formulario.reset({ nome: '', icone: 'self_improvement', cor: 'green' });
    this.modalAberto.set(true);
  }

  protected abrirEdicao(habito: Habito): void {
    this.habitoEmEdicao.set(habito);
    this.erroFormulario.set(null);
    this.formulario.reset({ nome: habito.nome, icone: habito.icone, cor: habito.cor });
    this.modalAberto.set(true);
  }

  protected fecharModal(): void { if (!this.processando()) this.modalAberto.set(false); }

  protected salvar(): void {
    if (this.formulario.invalid) { this.formulario.markAllAsTouched(); return; }
    const dados = this.formulario.getRawValue();
    const request = { nome: dados.nome.trim(), icone: dados.icone.trim(), cor: dados.cor.trim() };
    const emEdicao = this.habitoEmEdicao();
    this.processando.set(true);
    this.erroFormulario.set(null);
    (emEdicao ? this.habitoApi.atualizar(emEdicao.id, request) : this.habitoApi.criar(request))
      .pipe(finalize(() => this.processando.set(false)))
      .subscribe({
        next: () => { this.modalAberto.set(false); this.carregar(); },
        error: () => this.erroFormulario.set('Não foi possível salvar o hábito. Verifique os dados e tente novamente.'),
      });
  }

  protected alternarRegistro(habito: Habito, data: string): void {
    if (this.processando()) return;
    this.processando.set(true);
    const concluido = this.estaConcluido(habito, data);
    (concluido ? this.habitoApi.desmarcar(habito.id, data) : this.habitoApi.marcar(habito.id, data))
      .pipe(finalize(() => this.processando.set(false)))
      .subscribe({ next: () => this.carregar(), error: () => this.erro.set('Não foi possível atualizar a marcação do hábito.') });
  }

  protected excluir(habito: Habito): void {
    if (!confirm(`Excluir o hábito "${habito.nome}"?`)) return;
    this.processando.set(true);
    this.habitoApi.excluir(habito.id).pipe(finalize(() => this.processando.set(false))).subscribe({
      next: () => this.carregar(),
      error: () => this.erro.set('Não foi possível excluir o hábito.'),
    });
  }

  protected estaConcluido(habito: Habito, data: string): boolean { return habito.diasConcluidos.includes(data); }
  protected quantidadeSemana(habito: Habito): number { return habito.diasConcluidos.length; }

  protected carregar(): void {
    this.carregando.set(true);
    this.erro.set(null);
    forkJoin({ habitos: this.habitoApi.listar(this.dias[0].data, this.dias[6].data), resumo: this.habitoApi.resumo(), gamificacao: this.gamificacaoApi.resumo() })
      .pipe(finalize(() => this.carregando.set(false)))
      .subscribe({
        next: ({ habitos, resumo, gamificacao }) => { this.habitos.set(habitos); this.sequenciaAtual.set(resumo.sequenciaAtual); this.resumoGamificacao.set(gamificacao); },
        error: () => this.erro.set('Não foi possível carregar seus hábitos. Tente novamente.'),
      });
  }

  private criarDiasSemana(): DiaSemana[] {
    const referencia = new Date(this.hoje);
    const deslocamento = (referencia.getDay() + 6) % 7;
    referencia.setDate(referencia.getDate() - deslocamento);
    return ['S', 'T', 'Q', 'Q', 'S', 'S', 'D'].map((rotulo, indice) => {
      const data = new Date(referencia);
      data.setDate(referencia.getDate() + indice);
      return { data: this.formatarData(data), rotulo, dia: data.getDate() };
    });
  }

  private formatarData(data: Date): string {
    const ano = data.getFullYear();
    const mes = String(data.getMonth() + 1).padStart(2, '0');
    const dia = String(data.getDate()).padStart(2, '0');
    return `${ano}-${mes}-${dia}`;
  }
}
