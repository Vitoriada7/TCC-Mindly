import { TestBed } from '@angular/core/testing';
import { TemaService } from './tema.service';

describe('Cache do tema', () => {
  beforeEach(() => {
    localStorage.clear();
    document.documentElement.dataset['theme'] = 'light';
  });

  afterEach(() => {
    localStorage.clear();
    document.documentElement.dataset['theme'] = 'light';
  });

  function salvarSessao(sub: string, exp = Date.now() / 1000 + 60): void {
    localStorage.setItem('mindly.token', `header.${btoa(JSON.stringify({ sub, exp }))}.signature`);
    localStorage.setItem('mindly.tema', JSON.stringify({ id: 7, escuro: true }));
  }

  it('restaura o tema ao recarregar com a mesma sessão válida', () => {
    salvarSessao('7');
    expect(TestBed.inject(TemaService).escuro()).toBe(true);
    expect(document.documentElement.dataset['theme']).toBe('dark');
  });

  it('não reutiliza o tema de outra conta', () => {
    salvarSessao('8');
    expect(TestBed.inject(TemaService).escuro()).toBe(false);
  });

  it('não restaura o tema de uma sessão expirada', () => {
    salvarSessao('7', Date.now() / 1000 - 1);
    expect(TestBed.inject(TemaService).escuro()).toBe(false);
  });

  it('permite usar a aplicação quando o cache está corrompido', () => {
    localStorage.setItem('mindly.tema', 'invalido');
    expect(TestBed.inject(TemaService).escuro()).toBe(false);
  });
});
