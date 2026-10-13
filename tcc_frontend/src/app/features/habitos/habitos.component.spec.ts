import { TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { of, Subject } from 'rxjs';
import { CarregamentoComponent } from '../../shared/components/carregamento/carregamento.component';
import { GamificacaoApiService } from '../gamificacao/services/gamificacao-api.service';
import { HabitosComponent } from './habitos.component';
import { Habito, ResumoHabitos } from './models/habito.models';
import { HabitoApiService } from './services/habito-api.service';

describe('Loading da página de hábitos', () => {
  it('aparece imediatamente e só termina depois de carregar os dados principais', () => {
    const habitos = new Subject<Habito[]>();
    const resumo = new Subject<ResumoHabitos>();
    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        { provide: HabitoApiService, useValue: { listar: () => habitos, resumo: () => resumo } },
        { provide: GamificacaoApiService, useValue: { resumo: () => of(null) } },
      ],
    });
    // A abertura do modal é validada no teste do componente de carregamento.
    TestBed.overrideComponent(CarregamentoComponent, { set: { template: '' } });
    const fixture = TestBed.createComponent(HabitosComponent);
    fixture.detectChanges();
    const loading = fixture.debugElement.query(By.directive(CarregamentoComponent))
      .componentInstance as CarregamentoComponent;
    expect(loading.ativo()).toBe(true);
    habitos.next([]);
    habitos.complete();
    fixture.detectChanges();
    expect(loading.ativo()).toBe(true);
    resumo.next({ sequenciaAtual: 0, habitosConcluidosHoje: 0, totalHabitosAtivos: 0 });
    resumo.complete();
    fixture.detectChanges();
    expect(loading.ativo()).toBe(false);
  });
});
