import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
@Component({
  selector: 'app-auth',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './auth.component.html',
  styleUrl: './auth.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AuthComponent {
  private readonly route = inject(ActivatedRoute);
  protected readonly isSignup = computed(
    () => this.route.snapshot.routeConfig?.path === 'cadastro',
  );
  protected readonly name = new FormControl('', { nonNullable: true });
  protected readonly email = new FormControl('', { nonNullable: true });
  protected readonly password = new FormControl('', { nonNullable: true });
  protected readonly submitted = false;
}
