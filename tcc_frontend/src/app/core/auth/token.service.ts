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
    return Boolean(this.recuperar());
  }
}
