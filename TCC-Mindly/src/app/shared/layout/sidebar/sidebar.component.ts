import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

type NavigationItem = { label: string; icon: string; route: string };

@Component({
  selector: 'app-sidebar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SidebarComponent {
  readonly isOpen = input(false);
  readonly closed = output<void>();
  protected readonly navigation: NavigationItem[] = [
    { label: 'Início', icon: 'home', route: '/' },
    { label: 'Diário emocional', icon: 'edit_note', route: '/diario' },
    { label: 'Conversar com a IA', icon: 'chat_bubble', route: '/ia' },
    { label: 'Minhas tarefas', icon: 'check_circle', route: '/tarefas' },
    { label: 'Hábitos', icon: 'calendar_month', route: '/habitos' },
    { label: 'Progresso', icon: 'bar_chart', route: '/progresso' },
  ];
}
