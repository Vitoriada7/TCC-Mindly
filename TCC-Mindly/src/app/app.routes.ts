import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    title: 'Início',
    loadComponent: () =>
      import('./features/dashboard/dashboard.component').then((c) => c.DashboardComponent),
  },
  {
    path: 'diario',
    title: 'Diário emocional',
    loadComponent: () =>
      import('./features/journal/journal.component').then((c) => c.JournalComponent),
  },
  {
    path: 'ia',
    title: 'Conversar com a IA',
    loadComponent: () => import('./features/chat/chat.component').then((c) => c.ChatComponent),
  },
  {
    path: 'tarefas',
    title: 'Minhas tarefas',
    loadComponent: () => import('./features/tasks/tasks.component').then((c) => c.TasksComponent),
  },
  {
    path: 'habitos',
    title: 'Hábitos',
    loadComponent: () =>
      import('./features/habits/habits.component').then((c) => c.HabitsComponent),
  },
  {
    path: 'progresso',
    title: 'Progresso',
    loadComponent: () =>
      import('./features/progress/progress.component').then((c) => c.ProgressComponent),
  },
  {
    path: 'perfil',
    title: 'Perfil',
    loadComponent: () =>
      import('./features/profile/profile.component').then((c) => c.ProfileComponent),
  },
  {
    path: 'configuracoes',
    title: 'Configurações',
    loadComponent: () =>
      import('./features/profile/profile.component').then((c) => c.ProfileComponent),
  },
  {
    path: 'login',
    title: 'Entrar',
    loadComponent: () => import('./features/auth/auth.component').then((c) => c.AuthComponent),
  },
  {
    path: 'cadastro',
    title: 'Criar conta',
    loadComponent: () => import('./features/auth/auth.component').then((c) => c.AuthComponent),
  },
  { path: '**', redirectTo: '' },
];
