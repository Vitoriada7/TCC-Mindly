import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
@Component({
  selector: 'app-progress',
  templateUrl: './progress.component.html',
  styleUrl: './progress.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProgressComponent {
  protected readonly period = signal('Semana');
  protected readonly moods = [
    { day: 'Seg', icon: 'sentiment_satisfied', color: 'yellow' },
    { day: 'Ter', icon: 'sentiment_satisfied', color: 'yellow' },
    { day: 'Qua', icon: 'sentiment_neutral', color: 'blue' },
    { day: 'Qui', icon: 'sentiment_very_satisfied', color: 'green' },
    { day: 'Sex', icon: 'sentiment_satisfied', color: 'yellow' },
  ];
}
