import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
type Habit = { name: string; icon: string; color: string; completed: boolean[] };
@Component({
  selector: 'app-habits',
  templateUrl: './habits.component.html',
  styleUrl: './habits.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HabitsComponent {
  protected readonly days = ['S', 'T', 'Q', 'Q', 'S', 'S', 'D'];
  protected readonly habits = signal<Habit[]>([
    {
      name: 'Beber água',
      icon: 'water_drop',
      color: 'blue',
      completed: [true, true, true, true, true, false, false],
    },
    {
      name: 'Pausa consciente',
      icon: 'self_improvement',
      color: 'green',
      completed: [true, true, false, true, false, false, false],
    },
    {
      name: 'Ler por prazer',
      icon: 'auto_stories',
      color: 'pink',
      completed: [true, false, true, false, false, false, false],
    },
  ]);
  protected countCompleted(habit: Habit): number {
    return habit.completed.filter((day) => day).length;
  }
  protected toggle(habitName: string, index: number): void {
    this.habits.update((items) =>
      items.map((habit) =>
        habit.name === habitName
          ? { ...habit, completed: habit.completed.map((day, i) => (i === index ? !day : day)) }
          : habit,
      ),
    );
  }
}
