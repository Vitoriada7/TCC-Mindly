import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { TokenService } from '../auth/token.service';

/** Inclui o JWT nas requisições autenticadas quando houver uma sessão ativa. */
export const jwtInterceptor: HttpInterceptorFn = (requisicao, proximo) => {
  const token = inject(TokenService).recuperar();

  if (!token || requisicao.headers.has('Authorization')) {
    return proximo(requisicao);
  }

  return proximo(
    requisicao.clone({
      setHeaders: { Authorization: `Bearer ${token}` },
    }),
  );
};
