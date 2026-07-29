import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
@Component({
  selector: 'app-profile',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProfileComponent {
  protected readonly name = new FormControl('Ana Clara', { nonNullable: true });
  protected readonly notifications = signal(true);
  protected readonly saved = signal(false);
  protected save(): void {
    this.saved.set(true);
  }
}
