import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TokenService } from '../auth/token.service';

/** Bloqueia rotas privadas para visitantes sem token de acesso. */
export const authGuard: CanActivateFn = (_, estado) => {
  const tokenService = inject(TokenService);

  if (tokenService.possuiToken()) {
    return true;
  }

  return inject(Router).createUrlTree(['/boas-vindas'], {
    queryParams: { retorno: estado.url },
  });
};
