import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

type Mood = { label: string; icon: string };
type Task = { title: string; detail: string; priority: string; done: boolean };

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardComponent {
  protected readonly moods: Mood[] = [
    { label: 'Muito bem', icon: 'sentiment_very_satisfied' },
    { label: 'Bem', icon: 'sentiment_satisfied' },
    { label: 'Neutro', icon: 'sentiment_neutral' },
    { label: 'Cansada', icon: 'sentiment_dissatisfied' },
    { label: 'Difícil', icon: 'sentiment_very_dissatisfied' },
  ];
  protected readonly selectedMood = signal(this.moods[1]);
  protected readonly habitDone = signal(false);
  protected readonly tasks = signal<Task[]>([
    {
      title: 'Revisar conteúdo de Biologia',
      detail: '09:00 — 30 min',
      priority: 'Alta',
      done: false,
    },
    {
      title: 'Finalizar atividade de Matemática',
      detail: '14:30 — 45 min',
      priority: 'Média',
      done: false,
    },
    {
      title: 'Organizar materiais da semana',
      detail: 'Quando puder',
      priority: 'Baixa',
      done: true,
    },
  ]);

  protected toggleTask(title: string): void {
    this.tasks.update((tasks) =>
      tasks.map((task) => (task.title === title ? { ...task, done: !task.done } : task)),
    );
  }
  protected addTask(): void {
    this.tasks.update((tasks) => [
      ...tasks,
      {
        title: 'Nova tarefa para organizar',
        detail: 'Defina um horário',
        priority: 'Baixa',
        done: false,
      },
    ]);
  }
}
