import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
type Task = { title: string; priority: string; done: boolean };
@Component({
  selector: 'app-tasks',
  imports: [ReactiveFormsModule],
  templateUrl: './tasks.component.html',
  styleUrl: './tasks.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TasksComponent {
  protected readonly title = new FormControl('', { nonNullable: true });
  protected readonly tasks = signal<Task[]>([
    { title: 'Revisar Biologia', priority: 'Alta', done: false },
    { title: 'Atividade de Matemática', priority: 'Média', done: false },
    { title: 'Organizar materiais', priority: 'Baixa', done: true },
  ]);
  protected readonly completedCount = computed(
    () => this.tasks().filter((task) => task.done).length,
  );
  protected add(): void {
    if (this.title.value.trim()) {
      this.tasks.update((x) => [...x, { title: this.title.value, priority: 'Média', done: false }]);
      this.title.setValue('');
    }
  }
  protected toggle(title: string): void {
    this.tasks.update((x) => x.map((t) => (t.title === title ? { ...t, done: !t.done } : t)));
  }
}
