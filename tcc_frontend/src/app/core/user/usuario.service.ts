import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { apiConfig } from '../config/api.config';
import { AtualizarPerfilRequest, UsuarioAutenticado } from './usuario.models';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private readonly http = inject(HttpClient);
  private readonly urlPerfil = `${apiConfig.urlBase}/usuarios/me`;
  readonly usuario = signal<UsuarioAutenticado | null>(null);

  obterPerfil(): Observable<UsuarioAutenticado> {
    return this.http.get<UsuarioAutenticado>(this.urlPerfil).pipe(tap((usuario) => this.usuario.set(usuario)));
  }

  atualizarPerfil(dados: AtualizarPerfilRequest): Observable<UsuarioAutenticado> {
    return this.http.put<UsuarioAutenticado>(this.urlPerfil, dados).pipe(tap((usuario) => this.usuario.set(usuario)));
  }
}
