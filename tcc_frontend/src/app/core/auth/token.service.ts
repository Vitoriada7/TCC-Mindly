import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class TokenService {
  private readonly chaveToken = 'mindly.token';

  salvar(token: string): void {
    localStorage.setItem(this.chaveToken, token);
  }

  recuperar(): string | null {
    return localStorage.getItem(this.chaveToken);
  }

  remover(): void {
    localStorage.removeItem(this.chaveToken);
  }

  possuiToken(): boolean {
    const token = this.recuperar();
    if (!token) return false;

    try {
      const partes = token.split('.');
      if (partes.length !== 3) return false;
      const payload = partes[1].replace(/-/g, '+').replace(/_/g, '/');
      const dados: unknown = JSON.parse(atob(payload));
      return (
        typeof dados === 'object' &&
        dados !== null &&
        'exp' in dados &&
        typeof dados.exp === 'number' &&
        Number.isFinite(dados.exp) &&
        dados.exp * 1000 > Date.now()
      );
    } catch {
      return false;
    }
  }
}
