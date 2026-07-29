import { Routes } from '@angular/router';
import { TarefasComponent } from './tarefas.component';

export const rotasTarefas: Routes = [
  { path: '', component: TarefasComponent, title: 'Minhas tarefas | Mindly' },
];
