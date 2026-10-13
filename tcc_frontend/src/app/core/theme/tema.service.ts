import { DOCUMENT } from '@angular/common';
import { Injectable, inject, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class TemaService {
  private readonly document = inject(DOCUMENT);
  readonly escuro = signal(false);

  constructor() {
    try {
      const token = localStorage.getItem('mindly.token');
      const cache: unknown = JSON.parse(localStorage.getItem('mindly.tema') || 'null');
      if (token && cache && typeof cache === 'object' && 'id' in cache && 'escuro' in cache) {
        const payload: unknown = JSON.parse(
          atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')),
        );
        if (
          payload &&
          typeof payload === 'object' &&
          'sub' in payload &&
          'exp' in payload &&
          typeof payload.exp === 'number' &&
          String(cache.id) === String(payload.sub) &&
          payload.exp * 1000 > Date.now()
        ) {
          this.aplicar(cache.escuro === true);
        }
      }
    } catch {
      this.aplicar(false);
    }
  }

  aplicar(escuro: boolean): void {
    this.escuro.set(escuro);
    this.document.documentElement.dataset['theme'] = escuro ? 'dark' : 'light';
  }

  sincronizar(id: number, escuro: boolean): void {
    this.aplicar(escuro);
    try {
      localStorage.setItem('mindly.tema', JSON.stringify({ id, escuro }));
    } catch {
      // A preferência continua salva na conta quando o armazenamento local está indisponível.
    }
  }
}
