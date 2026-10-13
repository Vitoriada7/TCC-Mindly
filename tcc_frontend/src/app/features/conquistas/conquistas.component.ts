import { CarregamentoComponent } from '../../shared/components/carregamento/carregamento.component';
import { ChangeDetectionStrategy, Component, ElementRef, computed, inject, signal, viewChild } from '@angular/core';
import { NgOptimizedImage } from '@angular/common';
import { AVATARES } from '../../core/user/avatares';
import { UsuarioService } from '../../core/user/usuario.service';
import { finalize } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { Conquista, TrilhaGamificacao } from '../gamificacao/models/gamificacao.models';
import { GamificacaoApiService } from '../gamificacao/services/gamificacao-api.service';

@Component({
  selector: 'app-conquistas',
  imports: [CarregamentoComponent, NgOptimizedImage],
  templateUrl: './conquistas.component.html',
  styleUrl: './conquistas.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConquistasComponent {
  private readonly usuarioService = inject(UsuarioService);
  private readonly dialog = viewChild.required<ElementRef<HTMLDialogElement>>('dialog');
  protected readonly selecionada = signal<Conquista | null>(null);
  protected readonly salvandoAvatar = signal(false);
  protected readonly erroAvatar = signal<string | null>(null);
  protected readonly avatarSalvo = signal(false);

  protected abrir(conquista: Conquista): void {
    this.selecionada.set(conquista);
    this.erroAvatar.set(null);
    this.avatarSalvo.set(false);
    this.dialog().nativeElement.showModal();
  }

  protected fechar(): void { this.dialog().nativeElement.close(); }

  protected fecharPeloFundo(event: MouseEvent): void {
    const dialog = this.dialog().nativeElement;
    const bounds = dialog.getBoundingClientRect();
    if (event.target === dialog && (event.clientX < bounds.left || event.clientX > bounds.right || event.clientY < bounds.top || event.clientY > bounds.bottom)) this.fechar();
  }

  protected usarAvatar(): void {
    const conquista = this.selecionada();
    const avatar = conquista && this.recompensa(conquista.codigo);
    if (!avatar || conquista?.status !== 'CONCLUIDA' || this.salvandoAvatar()) return;
    this.salvandoAvatar.set(true);
    this.erroAvatar.set(null);
    this.usuarioService.atualizarAvatar(avatar.id).pipe(finalize(() => this.salvandoAvatar.set(false))).subscribe({
      next: () => { if (this.selecionada()?.codigo === conquista.codigo) this.avatarSalvo.set(true); },
      error: () => { if (this.selecionada()?.codigo === conquista.codigo) this.erroAvatar.set('Não foi possível trocar seu avatar. Tente novamente.'); },
    });
  }
  protected recompensa(codigo: string) { return AVATARES.find((avatar) => avatar.conquista === codigo); }
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
