import { Routes } from '@angular/router';
import { EmocionalComponent } from './emocional.component';
import { RegistroDiarioComponent } from './registro-diario.component';

export const rotasEmocional: Routes = [
  { path: '', component: EmocionalComponent, title: 'Diário emocional | Mindly' },
  { path: 'registro', component: RegistroDiarioComponent, title: 'Registro diário | Mindly' },
];
