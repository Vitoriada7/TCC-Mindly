import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { apiConfig } from '../config/api.config';
import { TokenService } from '../auth/token.service';

/** Inclui o JWT nas requisições autenticadas quando houver uma sessão ativa. */
export const jwtInterceptor: HttpInterceptorFn = (requisicao, proximo) => {
  const tokenService = inject(TokenService);
  const token = tokenService.recuperar();
  const urlApi = `${apiConfig.urlBase}/`;
  const urlAuth = `${urlApi}autenticacao/`;

  if (
    !requisicao.url.startsWith(urlApi) ||
    requisicao.url.startsWith(urlAuth) ||
    !token ||
    requisicao.headers.has('Authorization')
  ) {
    return proximo(requisicao);
  }

  const authService = inject(AuthService);
  const router = inject(Router);
  const encerrarSessao = () => {
    if (tokenService.recuperar() !== token) return;
    authService.logout();
    void router.navigate(['/login'], {
      queryParams: { retorno: router.url },
      replaceUrl: true,
    });
  };

  if (!tokenService.possuiToken()) {
    encerrarSessao();
    return throwError(() => new HttpErrorResponse({ status: 401, url: requisicao.url }));
  }

  return proximo(
    requisicao.clone({
      setHeaders: { Authorization: `Bearer ${token}` },
    }),
  ).pipe(
    catchError((erro: unknown) => {
      if (erro instanceof HttpErrorResponse && erro.status === 401) {
        encerrarSessao();
      }
      return throwError(() => erro);
    }),
  );
};
