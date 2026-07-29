import { Routes } from '@angular/router';
import { AuthComponent } from './auth.component';

/** O caminho pai define se o componente será exibido em modo login ou cadastro. */
export const rotasAuth: Routes = [
  {
    path: '',
    component: AuthComponent,
  },
];
