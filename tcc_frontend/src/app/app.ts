import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  effect,
  inject,
  signal,
} from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MobileNavComponent } from './shared/layout/mobile-nav/mobile-nav.component';
import { SidebarComponent } from './shared/layout/sidebar/sidebar.component';
import { AuthService } from './core/auth/auth.service';
import { UsuarioService } from './core/user/usuario.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MobileNavComponent, SidebarComponent],
  templateUrl: './app.html',
  styleUrl: './app.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class App {
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  private readonly auth = inject(AuthService);
  private readonly usuarios = inject(UsuarioService);
  protected readonly rotaPublica = signal(this.ehRotaPublica(this.router.url));

  constructor() {
    effect((onCleanup) => {
      if (!this.auth.estaAutenticado()) {
        this.usuarios.limpar();
        return;
      }
      const subscription = this.usuarios.obterPerfil().subscribe({ error: () => undefined });
      onCleanup(() => subscription.unsubscribe());
    });
    this.router.events
      .pipe(
        filter((evento): evento is NavigationEnd => evento instanceof NavigationEnd),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((evento) => this.rotaPublica.set(this.ehRotaPublica(evento.urlAfterRedirects)));
  }

  private ehRotaPublica(url: string): boolean {
    return (
      url.startsWith('/boas-vindas') || url.startsWith('/login') || url.startsWith('/cadastro') ||
      url.startsWith('/privacidade') || url.startsWith('/termos')
    );
  }
}
