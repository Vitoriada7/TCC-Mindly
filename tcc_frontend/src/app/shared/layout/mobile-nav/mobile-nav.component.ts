import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

type ItemNavegacao = { label: string; icone: string; rota?: string; emBreve?: boolean };

@Component({
  selector: 'app-mobile-nav',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './mobile-nav.component.html',
  styleUrl: './mobile-nav.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MobileNavComponent {
  protected readonly itens: ItemNavegacao[] = [
    { label: 'Início', icone: 'home', rota: '/' },
    { label: 'Tarefas', icone: 'check_circle', rota: '/tarefas' },
    { label: 'Hábitos', icone: 'calendar_month', rota: '/habitos' },
    { label: 'Progresso', icone: 'bar_chart', emBreve: true },
    { label: 'Perfil', icone: 'person', rota: '/perfil' },
  ];
}
