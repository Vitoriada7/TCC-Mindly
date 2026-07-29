import { Injectable, inject, signal } from '@angular/core';
import { map, Observable, tap } from 'rxjs';
import { AuthApiService } from '../../features/auth/services/auth.service';
import {
  CadastroRequest,
  LoginRequest,
  RespostaCadastro,
} from '../../features/auth/models/autenticacao.models';
import { TokenService } from './token.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiAuth = inject(AuthApiService);
  private readonly tokenService = inject(TokenService);
  readonly estaAutenticado = signal(this.tokenService.possuiToken());

  login(dados: LoginRequest): Observable<void> {
    return this.apiAuth.login(dados).pipe(
      tap((resposta) => {
        this.tokenService.salvar(resposta.token);
        this.estaAutenticado.set(true);
      }),
      map(() => undefined),
    );
  }

  cadastrar(dados: CadastroRequest): Observable<RespostaCadastro> {
    return this.apiAuth.cadastrar(dados);
  }

  logout(): void {
    this.tokenService.remover();
    this.estaAutenticado.set(false);
  }
}
