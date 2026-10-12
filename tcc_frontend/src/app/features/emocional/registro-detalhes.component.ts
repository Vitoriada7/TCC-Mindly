import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, ElementRef, effect, input, output, viewChild } from '@angular/core';
import { RegistroEmocional } from './emocional.models';

@Component({
  selector: 'app-registro-detalhes',
  imports: [DatePipe],
  templateUrl: './registro-detalhes.component.html',
  styleUrl: './registro-detalhes.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegistroDetalhesComponent {
  readonly registro = input<RegistroEmocional | null>(null);
  readonly sentimento = input('');
  readonly fechar = output<void>();
  private readonly dialog = viewChild<ElementRef<HTMLDialogElement>>('dialog');

  constructor() {
    effect(() => {
      const registro = this.registro();
      const dialog = this.dialog()?.nativeElement;
      if (!dialog) return;
      if (registro) {
        if (!dialog.open) dialog.showModal();
      } else if (dialog.open) {
        dialog.close();
      }
    });
  }

  protected fecharDialog(): void {
    this.dialog()?.nativeElement.close();
  }
}
