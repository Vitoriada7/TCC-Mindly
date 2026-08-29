import { ChangeDetectionStrategy, Component, DestroyRef, inject } from '@angular/core';
import { NgOptimizedImage } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-boas-vindas',
  imports: [NgOptimizedImage],
  templateUrl: './boas-vindas.component.html',
  styleUrl: './boas-vindas.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BoasVindasComponent {
  private readonly router = inject(Router);
  private readonly rota = inject(ActivatedRoute);
  private readonly destroyRef = inject(DestroyRef);
  private navegando = false;
  private readonly temporizador = window.setTimeout(() => this.irParaLogin(), 4200);

  constructor() {
    this.destroyRef.onDestroy(() => window.clearTimeout(this.temporizador));
  }

  protected irParaLogin(): void {
    if (this.navegando) return;

    this.navegando = true;
    window.clearTimeout(this.temporizador);
    const retorno = this.rota.snapshot.queryParamMap.get('retorno');
    void this.router.navigate(['/login'], {
      replaceUrl: true,
      queryParams: retorno && retorno !== '/' ? { retorno } : undefined,
    });
  }
}
