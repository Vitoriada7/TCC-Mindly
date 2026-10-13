import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  effect,
  inject,
  input,
  viewChild,
} from '@angular/core';
import { DOCUMENT } from '@angular/common';

@Component({
  selector: 'app-carregamento',
  templateUrl: './carregamento.component.html',
  styleUrl: './carregamento.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CarregamentoComponent {
  readonly ativo = input.required<boolean>();
  private readonly document = inject(DOCUMENT);
  private readonly dialog = viewChild<ElementRef<HTMLDialogElement>>('dialog');

  constructor() {
    effect((onCleanup) => {
      const dialog = this.dialog()?.nativeElement;
      if (!dialog || !this.ativo()) return;
      const overflowAnterior = this.document.body.style.overflow;
      this.document.body.style.overflow = 'hidden';
      dialog.showModal();
      onCleanup(() => {
        dialog.close();
        this.document.body.style.overflow = overflowAnterior;
      });
    });
  }
}
