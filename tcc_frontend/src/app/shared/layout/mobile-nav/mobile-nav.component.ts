import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { UsuarioService } from '../../../core/user/usuario.service';

type ItemNavegacao = { label: string; icone: string; rota: string };

@Component({ selector: 'app-mobile-nav', imports: [RouterLink, RouterLinkActive], templateUrl: './mobile-nav.component.html', styleUrl: './mobile-nav.component.css', changeDetection: ChangeDetectionStrategy.OnPush })
export class MobileNavComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly usuarioService = inject(UsuarioService);
  protected readonly itens: ItemNavegacao[] = [
    { label: 'Início', icone: 'home', rota: '/' }, { label: 'Diário', icone: 'edit_note', rota: '/emocional' }, { label: 'Tarefas', icone: 'check_circle', rota: '/tarefas' }, { label: 'Hábitos', icone: 'calendar_month', rota: '/habitos' }, { label: 'Mais', icone: 'more_horiz', rota: '' },
  ];
  protected readonly atalhos: ItemNavegacao[] = [
    { label: 'Conquistas', icone: 'emoji_events', rota: '/conquistas' }, { label: 'Centros de apoio', icone: 'support_agent', rota: '/apoio' }, { label: 'Meu perfil', icone: 'person', rota: '/perfil' },
  ];
  protected readonly showMore = signal(false);
  protected readonly usuario = this.usuarioService.usuario;
  protected readonly iniciais = computed(() => this.usuario()?.nome.trim().split(/\s+/).slice(0, 2).map((nome) => nome[0]).join('').toUpperCase() || 'M');
  protected readonly nomeUsuario = computed(() => this.usuario()?.nome || 'Sua conta');
  protected readonly emailUsuario = computed(() => this.usuario()?.email || 'Mindly');

  constructor() { if (!this.usuario()) this.usuarioService.obterPerfil().subscribe({ error: () => undefined }); }
  protected toggleMore(): void { this.showMore.update((valor) => !valor); }
  protected closeMore(): void { this.showMore.set(false); }
  protected sair(): void { this.authService.logout(); this.closeMore(); this.router.navigate(['/login']); }
}
