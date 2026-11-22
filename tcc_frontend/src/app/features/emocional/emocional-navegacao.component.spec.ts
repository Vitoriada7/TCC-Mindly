import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';
import { vi } from 'vitest';
import { EmocionalComponent } from './emocional.component';
import { RegistroDiarioComponent } from './registro-diario.component';
import { EmocionalApiService } from './emocional-api.service';
import { GamificacaoApiService } from '../gamificacao/services/gamificacao-api.service';

describe('Entrada no registro pela emoção', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter([
          { path: 'emocional', component: EmocionalComponent },
          { path: 'emocional/registro', component: RegistroDiarioComponent },
        ]),
        { provide: EmocionalApiService, useValue: { listar: () => of([]) } },
        { provide: GamificacaoApiService, useValue: { resumo: () => of(null) } },
      ],
    });
  });

  it('abre o fluxo com a emoção clicada e permite trocar a escolha', async () => {
    const harness = await RouterTestingHarness.create('/emocional');
    const link = harness.routeNativeElement!.querySelector<HTMLAnchorElement>(
      'a.mood-button[href*="cansada"]',
    )!;
    link.click();
    await harness.fixture.whenStable();
    harness.detectChanges();

    expect(TestBed.inject(Router).url).toBe('/emocional/registro?sentimento=cansada');
    const element = harness.routeNativeElement!;
    expect(element.querySelector('.emotion.current strong')?.textContent).toBe('Cansada');
    expect(element.querySelector('.emotion.current')?.getAttribute('aria-pressed')).toBe('true');
    expect(element.querySelector<HTMLButtonElement>('.actions-row .primary')!.disabled).toBe(false);

    // O jsdom não implementa a rolagem e a preferência de animações do navegador.
    element.querySelector<HTMLElement>('.rail')!.scrollTo = vi.fn();
    vi.stubGlobal('matchMedia', vi.fn().mockReturnValue({ matches: true }));
    element.querySelectorAll<HTMLButtonElement>('.rail .emotion')[1].click();
    await harness.fixture.whenStable();
    expect(element.querySelector('.emotion.current strong')?.textContent).toBe('Bem');
  });

  afterEach(() => vi.unstubAllGlobals());

  it.each(['/emocional/registro', '/emocional/registro?sentimento=invalida'])(
    'mantém a escolha em aberto ao entrar por %s',
    async (url) => {
      const harness = await RouterTestingHarness.create(url);
      const element = harness.routeNativeElement!;
      expect(element.querySelector('.emotion[aria-pressed="true"]')).toBeNull();
      expect(element.querySelector<HTMLButtonElement>('.actions-row .primary')!.disabled).toBe(true);
    },
  );
});
