import { NgOptimizedImage } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { finalize, switchMap } from 'rxjs';
import { AVATARES } from '../../core/user/avatares';
import { UsuarioService } from '../../core/user/usuario.service';
import { Conquista } from '../gamificacao/models/gamificacao.models';
import { GamificacaoApiService } from '../gamificacao/services/gamificacao-api.service';

@Component({
  selector: 'app-biblioteca-avatares',
  imports: [NgOptimizedImage, RouterLink],
  templateUrl: './biblioteca-avatares.component.html',
  styleUrl: './biblioteca-avatares.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BibliotecaAvataresComponent {
  private readonly usuarioService = inject(UsuarioService);
  private readonly gamificacaoApi = inject(GamificacaoApiService);
  protected readonly usuario = this.usuarioService.usuario;
  protected readonly conquistas = signal<Conquista[]>([]);
  protected readonly carregando = signal(true);
  protected readonly salvando = signal(false);
  protected readonly erro = signal<string | null>(null);
  protected readonly sucesso = signal(false);
  protected readonly biblioteca = computed(() => AVATARES.map((avatar) => {
    const conquista = this.conquistas().find((item) => item.codigo === avatar.conquista);
    return { ...avatar, conquistaInfo: conquista, liberado: conquista?.status === 'CONCLUIDA' || (this.usuario()?.avataresLiberados?.includes(avatar.id) ?? false) };
  }));
  protected readonly totalLiberados = computed(() => this.biblioteca().filter((avatar) => avatar.liberado).length);
  protected readonly avataresLiberados = computed(() => this.biblioteca().filter((avatar) => avatar.liberado));

  constructor() { this.carregar(); }

  protected carregar(): void {
    this.carregando.set(true);
    this.erro.set(null);
    this.gamificacaoApi.resumo().pipe(
      switchMap((resumo) => { this.conquistas.set(resumo.conquistas); return this.usuarioService.obterPerfil(); }),
      finalize(() => this.carregando.set(false)),
    ).subscribe({ error: () => this.erro.set('Não foi possível carregar seus avatares. Tente novamente.') });
  }

  protected selecionarAvatar(avatar: string | null): void {
    if (this.salvando() || (avatar !== null && !this.biblioteca().some((item) => item.id === avatar && item.liberado))) return;
    this.salvando.set(true);
    this.erro.set(null);
    this.sucesso.set(false);
    this.usuarioService.atualizarAvatar(avatar).pipe(finalize(() => this.salvando.set(false))).subscribe({
      next: () => this.sucesso.set(true),
      error: () => this.erro.set('Não foi possível trocar seu avatar. Tente novamente.'),
    });
  }
}
