import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { apiConfig } from '../../../core/config/api.config';

export interface EstadoGoogleTasks {
  disponivel: boolean;
  conectado: boolean;
  ultimaSincronizacao: string | null;
}

@Injectable({ providedIn: 'root' })
export class GoogleTasksApiService {
  private readonly http = inject(HttpClient);
  private readonly url = `${apiConfig.urlBase}/integracoes/google-tasks`;
  estado() { return this.http.get<EstadoGoogleTasks>(this.url); }
  autorizar() { return this.http.post<{ url: string }>(`${this.url}/autorizar`, {}); }
  conectar(code: string, state: string) { return this.http.post<void>(`${this.url}/conectar`, { code, state }); }
  sincronizar() { return this.http.post<{ tarefas: number }>(`${this.url}/sincronizar`, {}); }
  desconectar() { return this.http.delete<void>(this.url); }
}
