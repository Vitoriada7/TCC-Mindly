import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { apiConfig } from '../../../core/config/api.config';
import { Habito, HabitoRequest, ResumoHabitos } from '../models/habito.models';

@Injectable({ providedIn: 'root' })
export class HabitoApiService {
  private readonly http = inject(HttpClient);
  private readonly url = `${apiConfig.urlBase}/habitos`;

  listar(inicio: string, fim: string): Observable<Habito[]> {
    const params = new HttpParams().set('inicio', inicio).set('fim', fim);
    return this.http.get<Habito[]>(this.url, { params });
  }

  resumo(): Observable<ResumoHabitos> {
    return this.http.get<ResumoHabitos>(`${this.url}/resumo`);
  }

  criar(dados: HabitoRequest): Observable<Habito> {
    return this.http.post<Habito>(this.url, dados);
  }

  atualizar(id: number, dados: HabitoRequest): Observable<Habito> {
    return this.http.put<Habito>(`${this.url}/${id}`, dados);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

  marcar(id: number, data: string): Observable<void> {
    return this.http.put<void>(`${this.url}/${id}/registros/${data}`, {});
  }

  desmarcar(id: number, data: string): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}/registros/${data}`);
  }
}
