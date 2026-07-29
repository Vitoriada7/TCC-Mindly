import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { guestGuard } from './core/guards/guest.guard';

/**
 * Rotas de alto nível da aplicação.
 *
 * Cada feature manterá suas próprias rotas para que possa evoluir de forma
 * isolada. Os componentes e guards serão adicionados nas próximas etapas.
 */
export const routes: Routes = [
  {
    path: 'login',
    canActivate: [guestGuard],
    loadChildren: () => import('./features/auth/auth.routes').then((m) => m.rotasAuth),
  },
  {
    path: 'cadastro',
    canActivate: [guestGuard],
    loadChildren: () => import('./features/auth/auth.routes').then((m) => m.rotasAuth),
  },
  {
    path: '',
    pathMatch: 'full',
    canActivate: [authGuard],
    loadChildren: () =>
      import('./features/dashboard/dashboard.routes').then((m) => m.rotasDashboard),
  },
  {
    path: 'tarefas',
    canActivate: [authGuard],
    loadChildren: () =>
      import('./features/tarefas/tarefas.routes').then((m) => m.rotasTarefas),
  },
  {
    path: 'habitos',
    canActivate: [authGuard],
    loadChildren: () =>
      import('./features/habitos/habitos.routes').then((m) => m.rotasHabitos),
  },
  {
    path: 'emocional',
    canActivate: [authGuard],
    loadChildren: () =>
      import('./features/emocional/emocional.routes').then((m) => m.rotasEmocional),
  },
  {
    path: 'perfil',
    canActivate: [authGuard],
    loadChildren: () => import('./features/perfil/perfil.routes').then((m) => m.rotasPerfil),
  },
];
