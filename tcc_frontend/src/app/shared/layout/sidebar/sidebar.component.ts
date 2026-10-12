import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../../../core/auth/auth.service';
import { UsuarioService } from '../../../core/user/usuario.service';
import { AvatarComponent } from '../../avatar/avatar.component';

type ItemNavegacao = { rotulo: string; icone: string; rota?: string; emBreve?: boolean };

@Component({
  selector: 'app-sidebar',
  imports: [RouterLink, RouterLinkActive, AvatarComponent],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SidebarComponent {
  private readonly usuarioService = inject(UsuarioService);
  protected readonly usuario = this.usuarioService.usuario;
  constructor() { if (!this.usuario()) this.usuarioService.obterPerfil().subscribe({ error: () => undefined }); }
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  protected readonly itens: ItemNavegacao[] = [
    { rotulo: 'Início', icone: 'home', rota: '/' },
    { rotulo: 'Emoções', icone: 'edit_note', rota: '/emocional' },
    { rotulo: 'Recursos', icone: 'spa', rota: '/recursos' },
    { rotulo: 'Conquistas', icone: 'emoji_events', rota: '/conquistas' },
    { rotulo: 'Centros de apoio', icone: 'support_agent', rota: '/apoio' },
    { rotulo: 'Minhas tarefas', icone: 'check_circle', rota: '/tarefas' },
    { rotulo: 'Hábitos', icone: 'calendar_month', rota: '/habitos' },
    { rotulo: 'Progresso', icone: 'bar_chart', emBreve: true },
    { rotulo: 'Perfil', icone: 'person', rota: '/perfil' },
  ];

  protected sair(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
