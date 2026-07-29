import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-journal',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './journal.component.html',
  styleUrl: './journal.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class JournalComponent {
  protected readonly feelings = [
    { name: 'Radiante', icon: 'sentiment_very_satisfied', color: 'yellow' },
    { name: 'Bem', icon: 'sentiment_satisfied', color: 'green' },
    { name: 'Neutra', icon: 'sentiment_neutral', color: 'blue' },
    { name: 'Cansada', icon: 'sentiment_dissatisfied', color: 'orange' },
    { name: 'Dificil', icon: 'sentiment_very_dissatisfied', color: 'pink' },
  ];
  protected readonly feeling = signal(this.feelings[1]);
  protected readonly intensity = signal(3);
  protected readonly note = new FormControl('', { nonNullable: true });
  protected readonly saved = signal(false);
  protected readonly calendarDays = Array.from({ length: 31 }, (_, index) => index + 1);
  protected readonly recordedDays = signal<number[]>([2, 4, 7, 9, 12, 15, 18, 20]);
  protected save(): void {
    if (this.note.value.trim()) {
      this.saved.set(true);
      this.recordedDays.update((days) => (days.includes(22) ? days : [...days, 22]));
      this.note.setValue('');
    }
  }
}
