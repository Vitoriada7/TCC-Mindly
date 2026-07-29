import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TokenService } from '../auth/token.service';

/** Impede que usuários autenticados retornem às telas públicas de acesso. */
export const guestGuard: CanActivateFn = () => {
  if (!inject(TokenService).possuiToken()) {
    return true;
  }

  return inject(Router).createUrlTree(['/']);
};
