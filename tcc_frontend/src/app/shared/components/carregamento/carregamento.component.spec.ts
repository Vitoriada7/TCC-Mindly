import { TestBed } from '@angular/core/testing';
import { CarregamentoComponent } from './carregamento.component';

describe('CarregamentoComponent', () => {
  it('abre o modal durante a consulta e restaura a rolagem ao terminar', () => {
    TestBed.configureTestingModule({});
    const fixture = TestBed.createComponent(CarregamentoComponent);
    fixture.componentRef.setInput('ativo', false);
    fixture.detectChanges();
    const dialog = (fixture.nativeElement as HTMLElement).querySelector('dialog')!;
    dialog.showModal = vi.fn();
    dialog.close = vi.fn();
    document.body.style.overflow = 'auto';
    fixture.detectChanges();
    expect(dialog.showModal).not.toHaveBeenCalled();
    fixture.componentRef.setInput('ativo', true);
    fixture.detectChanges();
    expect(dialog.showModal).toHaveBeenCalledOnce();
    expect(document.body.style.overflow).toBe('hidden');
    expect(dialog.querySelector('[role="status"]')?.textContent).toContain('Carregando seus dados');
    expect(dialog.querySelector('.loading-icon span')?.textContent).toBe('progress_activity');
    fixture.componentRef.setInput('ativo', false);
    fixture.detectChanges();
    expect(dialog.close).toHaveBeenCalledOnce();
    expect(document.body.style.overflow).toBe('auto');
    document.body.style.overflow = '';
  });
});
