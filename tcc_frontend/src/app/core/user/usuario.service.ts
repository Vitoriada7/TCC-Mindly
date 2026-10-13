import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { apiConfig } from '../config/api.config';
import { AtualizarPerfilRequest, UsuarioAutenticado } from './usuario.models';
import { TemaService } from '../theme/tema.service';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private readonly http = inject(HttpClient);
  private readonly tema = inject(TemaService);
  private readonly urlPerfil = `${apiConfig.urlBase}/usuarios/me`;
  readonly usuario = signal<UsuarioAutenticado | null>(null);

  obterPerfil(): Observable<UsuarioAutenticado> {
    return this.http
      .get<UsuarioAutenticado>(this.urlPerfil)
      .pipe(tap((usuario) => this.sincronizar(usuario)));
  }

  atualizarAvatar(avatar: string | null): Observable<UsuarioAutenticado> {
    return this.http
      .put<UsuarioAutenticado>(`${this.urlPerfil}/avatar`, { avatar })
      .pipe(tap((usuario) => this.sincronizar(usuario)));
  }

  atualizarPerfil(dados: AtualizarPerfilRequest): Observable<UsuarioAutenticado> {
    return this.http
      .put<UsuarioAutenticado>(this.urlPerfil, dados)
      .pipe(tap((usuario) => this.sincronizar(usuario)));
  }

  atualizarTema(temaEscuro: boolean): Observable<UsuarioAutenticado> {
    return this.http
      .put<UsuarioAutenticado>(`${this.urlPerfil}/tema`, { temaEscuro })
      .pipe(tap((usuario) => this.sincronizar(usuario)));
  }

  limpar(): void {
    this.usuario.set(null);
    this.tema.aplicar(false);
  }

  private sincronizar(usuario: UsuarioAutenticado): void {
    this.usuario.set(usuario);
    this.tema.sincronizar(usuario.id, usuario.temaEscuro === true);
  }
}
