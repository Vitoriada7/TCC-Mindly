import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

@Component({
  selector: 'app-feature-page',
  imports: [RouterLink],
  templateUrl: './feature-page.component.html',
  styleUrl: './feature-page.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FeaturePageComponent {
  private readonly route = inject(ActivatedRoute);
  protected readonly title = computed(() => this.route.snapshot.data['title'] as string);
  protected readonly icon = computed(() => this.route.snapshot.data['icon'] as string);
}
