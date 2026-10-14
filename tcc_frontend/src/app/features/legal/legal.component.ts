import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

@Component({
  selector: 'app-legal',
  imports: [RouterLink],
  templateUrl: './legal.component.html',
  styleUrl: './legal.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LegalComponent {
  protected readonly privacidade = inject(ActivatedRoute).snapshot.data['documento'] === 'privacidade';
}
