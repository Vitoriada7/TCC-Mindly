import { Routes } from '@angular/router';
import { PerfilComponent } from './perfil.component';

export const rotasPerfil: Routes = [
  { path: 'avatares', loadComponent: () => import('./biblioteca-avatares.component').then((m) => m.BibliotecaAvataresComponent), title: 'Biblioteca de avatares | Mindly' },
  { path: '', component: PerfilComponent, title: 'Perfil | Mindly' },
];
