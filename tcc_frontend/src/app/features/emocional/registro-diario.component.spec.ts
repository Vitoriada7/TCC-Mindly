import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { vi } from 'vitest';
import { RegistroDiarioComponent } from './registro-diario.component';
import { EmocionalApiService } from './emocional-api.service';

describe('Registro diário com múltiplas emoções', () => {
  it('envia todas as escolhas no campo existente ao continuar o fluxo', async () => {
    HTMLDialogElement.prototype.showModal = function() {
      this.open = true;
    };
    HTMLDialogElement.prototype.close = function() {
      this.open = false;
    };
    const gerarPerguntas = vi.fn().mockReturnValue(of({ perguntas: ['Como foi seu dia?'] }));
    await TestBed.configureTestingModule({
      imports: [RegistroDiarioComponent],
      providers: [provideRouter([]), { provide: EmocionalApiService, useValue: { gerarPerguntas } }],
    }).compileComponents();
    const fixture = TestBed.createComponent(RegistroDiarioComponent);
    await fixture.whenStable();
    const element = fixture.nativeElement as HTMLElement;
    const chips = element.querySelectorAll<HTMLButtonElement>('.chip');
    chips[0].click();
    await fixture.whenStable();
    chips[1].click();
    await fixture.whenStable();
    element.querySelector<HTMLButtonElement>('.actions-row .primary')!.click();
    await fixture.whenStable();
    const relato = element.querySelector('textarea')!;
    relato.value = 'Hoje consegui terminar uma tarefa.';
    relato.dispatchEvent(new Event('input'));
    await fixture.whenStable();
    element.querySelector<HTMLButtonElement>('.actions-row .primary')!.click();
    await fixture.whenStable();
    expect(gerarPerguntas).toHaveBeenCalledWith(expect.objectContaining({
      sentimento: 'radiante',
      sentimentoDetalhado: 'Felicidade, Entusiasmo',
    }));
  });
});
