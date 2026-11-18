import { TestBed } from '@angular/core/testing';
import { SeletorEmocoesComponent } from './seletor-emocoes.component';

describe('Seleção de emoções', () => {
  async function preparar(detalhes: string[] = []) {
    await TestBed.configureTestingModule({ imports: [SeletorEmocoesComponent] }).compileComponents();
    const fixture = TestBed.createComponent(SeletorEmocoesComponent);
    fixture.componentRef.setInput('principal', 'bem');
    fixture.componentRef.setInput('detalhes', detalhes);
    fixture.componentInstance.detalhesChange.subscribe(valores => fixture.componentRef.setInput('detalhes', valores));
    fixture.componentInstance.principalChange.subscribe(valor => fixture.componentRef.setInput('principal', valor));
    await fixture.whenStable();
    const element = fixture.nativeElement as HTMLElement;
    return { fixture, element };
  }

  it('seleciona várias opções e permite remover uma sem apagar as demais', async () => {
    const { fixture, element } = await preparar();
    const chips = element.querySelectorAll<HTMLButtonElement>('.chip');
    chips[0].click();
    await fixture.whenStable();
    chips[1].click();
    await fixture.whenStable();
    expect(fixture.componentInstance.detalhes()).toEqual(['Calma', 'Alívio']);
    chips[0].click();
    await fixture.whenStable();
    expect(fixture.componentInstance.detalhes()).toEqual(['Alívio']);
  });

  it('troca as opções por emoção principal e mantém as escolhas anteriores', async () => {
    const { fixture, element } = await preparar(['Calma']);
    fixture.componentRef.setInput('principal', 'dificil');
    await fixture.whenStable();
    expect(element.querySelector('.chips')?.textContent).toContain('Tristeza');
    expect(element.querySelector('.chips')?.textContent).not.toContain('Calma');
    expect(element.querySelector('.selection-summary')).toBeNull();
    expect(fixture.componentInstance.detalhes()).toEqual(['Calma']);
    fixture.componentRef.setInput('principal', 'bem');
    await fixture.whenStable();
    expect(element.querySelector('.chip')?.getAttribute('aria-pressed')).toBe('true');
    element.querySelector<HTMLButtonElement>('.chip')!.click();
    await fixture.whenStable();
    expect(fixture.componentInstance.detalhes()).toEqual([]);
  });

  it('impede ultrapassar 200 caracteres e libera espaço ao remover uma escolha', async () => {
    const { fixture, element } = await preparar(['x'.repeat(195)]);
    const calma = element.querySelector<HTMLButtonElement>('.chip')!;
    expect(calma.disabled).toBe(true);
    fixture.componentRef.setInput('detalhes', []);
    await fixture.whenStable();
    expect(calma.disabled).toBe(false);
    calma.click();
    await fixture.whenStable();
    expect(fixture.componentInstance.detalhes()).toEqual(['Calma']);
  });

  it('identifica a emoção central após deslizar o carrossel', async () => {
    const { fixture, element } = await preparar(['Calma']);
    const rail = element.querySelector<HTMLElement>('.rail')!;
    Object.defineProperty(rail, 'clientWidth', { value: 300 });
    Array.from(rail.children).forEach((botao, i) => {
      Object.defineProperty(botao, 'offsetLeft', { value: 60 + i * 180 });
      Object.defineProperty(botao, 'clientWidth', { value: 180 });
    });
    rail.scrollLeft = 540;
    rail.dispatchEvent(new Event('scroll'));
    await fixture.whenStable();
    expect(fixture.componentInstance.principal()).toBe('cansada');
    expect(element.querySelector('.chips')?.textContent).toContain('Cansaço');
    expect(fixture.componentInstance.detalhes()).toEqual(['Calma']);
  });
});
