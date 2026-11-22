import { ChangeDetectionStrategy, Component, ElementRef, afterNextRender, computed, input, output, viewChild } from '@angular/core';

import { EMOCOES } from './emocoes';
import { RostoEmocaoComponent } from './rosto-emocao.component';

@Component({
  selector: 'app-seletor-emocoes',
  imports: [RostoEmocaoComponent],
  templateUrl: './seletor-emocoes.component.html',
  styleUrl: './seletor-emocoes.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SeletorEmocoesComponent {
  readonly principal = input<string | null>(null);
  readonly detalhes = input<readonly string[]>([]);
  readonly principalChange = output<string>();
  readonly detalhesChange = output<string[]>();
  protected readonly emocoes = EMOCOES;
  protected readonly indice = computed(() => Math.max(0, EMOCOES.findIndex(e => e.value === this.principal())));
  protected readonly atual = computed(() => EMOCOES[this.indice()]);
  protected readonly limiteAtingido = computed(() => this.atual().detalhes.some(detalhe => !this.detalhes().includes(detalhe) && !this.cabe(detalhe)));
  private readonly trilho = viewChild<ElementRef<HTMLDivElement>>('trilho');

  constructor() {
    afterNextRender(() => {
      const trilho = this.trilho()?.nativeElement;
      const botao = trilho?.children.item(this.indice()) as HTMLElement | null;
      if (trilho && botao) trilho.scrollLeft = botao.offsetLeft - (trilho.clientWidth - botao.clientWidth) / 2;
    });
  }

  protected selecionar(indice: number): void {
    const destino = Math.max(0, Math.min(EMOCOES.length - 1, indice));
    this.principalChange.emit(EMOCOES[destino].value);
    const trilho = this.trilho()?.nativeElement;
    const botao = trilho?.children.item(destino) as HTMLElement | null;
    if (trilho && botao) {
      trilho.scrollTo({
        left: botao.offsetLeft - (trilho.clientWidth - botao.clientWidth) / 2,
        behavior: window.matchMedia('(prefers-reduced-motion: reduce)').matches ? 'instant' : 'smooth'
      });
    }
  }

  protected aoDeslizar(): void {
    const trilho = this.trilho()?.nativeElement;
    if (!trilho) return;
    const centro = trilho.scrollLeft + trilho.clientWidth / 2;
    const botoes = Array.from(trilho.children) as HTMLElement[];
    const indice = botoes.reduce((melhor, botao, i) =>
      Math.abs(botao.offsetLeft + botao.clientWidth / 2 - centro) <
        Math.abs(botoes[melhor].offsetLeft + botoes[melhor].clientWidth / 2 - centro) ? i : melhor, 0);
    this.principalChange.emit(EMOCOES[indice].value);
  }

  protected cabe(detalhe: string): boolean {
    return [...this.detalhes(), detalhe].join(', ').length <= 200;
  }

  protected alternar(detalhe: string): void {
    if (this.detalhes().includes(detalhe)) {
      this.detalhesChange.emit(this.detalhes().filter(item => item !== detalhe));
    } else if (this.cabe(detalhe)) {
      this.detalhesChange.emit([...this.detalhes(), detalhe]);
    }
    if (!this.principal()) this.principalChange.emit(this.atual().value);
  }
}
