import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { EmocionalComponent } from './emocional.component';
import { EmocionalApiService } from './emocional-api.service';
import { GamificacaoApiService } from '../gamificacao/services/gamificacao-api.service';

describe('Detalhes do registro emocional', () => {
  it('abre o relato ao clicar no dia registrado e permite abrir novamente após fechar', async () => {
    // jsdom não implementa a abertura nativa de dialogs.
    HTMLDialogElement.prototype.showModal = function () { this.open = true; };
    HTMLDialogElement.prototype.close = function () {
      this.open = false;
      this.dispatchEvent(new Event('close'));
    };
    const hoje = new Date();
    const dataRegistro = `${hoje.getFullYear()}-${String(hoje.getMonth() + 1).padStart(2, '0')}-${String(hoje.getDate()).padStart(2, '0')}T12:00:00`;
    await TestBed.configureTestingModule({
      imports: [EmocionalComponent],
      providers: [
        provideRouter([]),
        { provide: EmocionalApiService, useValue: { listar: () => of([{
          id: 1, sentimento: 'bem', sentimentoDetalhado: null,
          pensamento: 'Meu relato salvo', reflexao: null, dataRegistro,
        }]) } },
        { provide: GamificacaoApiService, useValue: { resumo: () => of(null) } },
      ],
    }).compileComponents();
    const fixture = TestBed.createComponent(EmocionalComponent);
    await fixture.whenStable();
    const element = fixture.nativeElement as HTMLElement;
    const dia = element.querySelector<HTMLButtonElement>('button.day.registered')!;
    const dialog = element.querySelector('dialog')!;
    expect(dialog.open).toBe(false);
    dia.click();
    await fixture.whenStable();
    expect(dialog.open).toBe(true);
    expect(dialog.textContent).toContain('Meu relato salvo');
    dialog.querySelector('button')!.click();
    await fixture.whenStable();
    expect(dialog.open).toBe(false);
    dia.click();
    await fixture.whenStable();
    expect(dialog.open).toBe(true);
  });
});
