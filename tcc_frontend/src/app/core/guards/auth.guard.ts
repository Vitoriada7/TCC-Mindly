import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TokenService } from '../auth/token.service';
import { AuthService } from '../auth/auth.service';

/** Bloqueia rotas privadas para visitantes sem token de acesso. */
export const authGuard: CanActivateFn = (_, estado) => {
  const tokenService = inject(TokenService);

  if (tokenService.possuiToken()) {
    return true;
  }

  inject(AuthService).logout();
  return inject(Router).createUrlTree(['/login'], {
    queryParams: { retorno: estado.url },
  });
};
