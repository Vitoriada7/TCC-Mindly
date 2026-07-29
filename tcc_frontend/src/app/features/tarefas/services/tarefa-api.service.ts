import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { apiConfig } from '../../../core/config/api.config';
import { AtualizarTarefaRequest, Categoria, CategoriaRequest, CriarTarefaRequest, Prioridade, StatusTarefa, Tarefa } from '../models/tarefa.models';

@Injectable({ providedIn: 'root' })
export class TarefaApiService {
  private readonly http = inject(HttpClient);
  private readonly urlTarefas = `${apiConfig.urlBase}/tarefas`;
  private readonly urlCategorias = `${apiConfig.urlBase}/categorias`;

  listar(status?: StatusTarefa | 'VENCIDA', prioridade?: Prioridade, categoriaId?: number, vencida?: boolean): Observable<Tarefa[]> {
    let params = new HttpParams();
    const filtrarVencidas = vencida || status === 'VENCIDA';
    if (status && status !== 'VENCIDA') params = params.set('status', status);
    if (prioridade) params = params.set('prioridade', prioridade);
    if (categoriaId) params = params.set('categoria', categoriaId);
    if (filtrarVencidas) params = params.set('vencida', true);

    return this.http.get<Tarefa[]>(this.urlTarefas, { params });
  }

  listarCategorias(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(this.urlCategorias);
  }

  criar(dados: CriarTarefaRequest): Observable<Tarefa> {
    return this.http.post<Tarefa>(this.urlTarefas, dados);
  }

  concluir(id: number): Observable<Tarefa> {
    return this.http.patch<Tarefa>(`${this.urlTarefas}/${id}/concluir`, {});
  }

  atualizar(id: number, dados: AtualizarTarefaRequest): Observable<Tarefa> {
    return this.http.put<Tarefa>(`${this.urlTarefas}/${id}`, dados);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.urlTarefas}/${id}`);
  }

  criarCategoria(dados: CategoriaRequest): Observable<Categoria> {
    return this.http.post<Categoria>(this.urlCategorias, dados);
  }

  atualizarCategoria(id: number, dados: CategoriaRequest): Observable<Categoria> {
    return this.http.put<Categoria>(`${this.urlCategorias}/${id}`, dados);
  }

  excluirCategoria(id: number): Observable<void> {
    return this.http.delete<void>(`${this.urlCategorias}/${id}`);
  }
}
