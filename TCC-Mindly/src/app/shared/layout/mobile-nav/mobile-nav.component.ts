import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-mobile-nav',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './mobile-nav.component.html',
  styleUrl: './mobile-nav.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MobileNavComponent {
  protected readonly items = [
    { label: 'Início', icon: 'home', route: '/' },
    { label: 'Diário', icon: 'edit_note', route: '/diario' },
    { label: 'Tarefas', icon: 'check_circle', route: '/tarefas' },
    { label: 'Hábitos', icon: 'calendar_month', route: '/habitos' },
    { label: 'Perfil', icon: 'person', route: '/perfil' },
  ];
}
