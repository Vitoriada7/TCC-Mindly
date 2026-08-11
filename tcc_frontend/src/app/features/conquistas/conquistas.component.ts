import { ChangeDetectionStrategy, Component, signal } from '@angular/core';

type ConquistaStatus = 'CONCLUIDA' | 'DESBLOQUEADA' | 'BLOQUEADA';
type Conquista = { titulo: string; descricao: string; status: ConquistaStatus; icone: string };

@Component({
  selector: 'app-conquistas',
  templateUrl: './conquistas.component.html',
  styleUrl: './conquistas.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConquistasComponent {
  protected readonly conquistas = signal<Conquista[]>([
    { titulo: 'Primeiro registro', descricao: 'Compartilhe seu primeiro dia no diário emocional.', status: 'CONCLUIDA', icone: 'edit_note' },
    { titulo: 'Rotina suave', descricao: 'Complete 7 dias de hábito seguido.', status: 'DESBLOQUEADA', icone: 'calendar_today' },
    { titulo: 'Foco gentil', descricao: 'Finalize 3 tarefas com calma.', status: 'BLOQUEADA', icone: 'check_circle' },
    { titulo: 'Coração atento', descricao: 'Faça 5 registros emocionais esta semana.', status: 'BLOQUEADA', icone: 'favorite' },
  ]);

  protected readonly ordenarConquistas = signal<Conquista[]>(this.conquistas().slice().sort((a, b) => {
    const ordem = { CONCLUIDA: 0, DESBLOQUEADA: 1, BLOQUEADA: 2 } as const;
    return ordem[a.status] - ordem[b.status];
  }));
}
