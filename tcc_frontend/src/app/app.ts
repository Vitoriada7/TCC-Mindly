import { ChangeDetectionStrategy, Component, DestroyRef, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MobileNavComponent } from './shared/layout/mobile-nav/mobile-nav.component';
import { SidebarComponent } from './shared/layout/sidebar/sidebar.component';

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
  protected readonly rotaPublica = signal(this.ehRotaPublica(this.router.url));

  constructor() {
    this.router.events
      .pipe(filter((evento): evento is NavigationEnd => evento instanceof NavigationEnd), takeUntilDestroyed(this.destroyRef))
      .subscribe((evento) => this.rotaPublica.set(this.ehRotaPublica(evento.urlAfterRedirects)));
  }

  private ehRotaPublica(url: string): boolean {
    return url.startsWith('/login') || url.startsWith('/cadastro');
  }
}
