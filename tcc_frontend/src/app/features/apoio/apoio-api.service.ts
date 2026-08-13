import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { apiConfig } from '../../core/config/api.config';
import {
  ContatoEmergencia,
  ContatoEmergenciaRequest,
} from './apoio.models';

@Injectable({ providedIn: 'root' })
export class ApoioApiService {
  private readonly http = inject(HttpClient);

  listarContatos(): Observable<ContatoEmergencia[]> {
    return this.http.get<ContatoEmergencia[]>(`${apiConfig.urlBase}/contatos-emergencia`);
  }

  criarContato(dados: ContatoEmergenciaRequest): Observable<ContatoEmergencia> {
    return this.http.post<ContatoEmergencia>(`${apiConfig.urlBase}/contatos-emergencia`, dados);
  }

  atualizarContato(
    id: number,
    dados: ContatoEmergenciaRequest,
  ): Observable<ContatoEmergencia> {
    return this.http.put<ContatoEmergencia>(
      `${apiConfig.urlBase}/contatos-emergencia/${id}`,
      dados,
    );
  }

  excluirContato(id: number): Observable<void> {
    return this.http.delete<void>(`${apiConfig.urlBase}/contatos-emergencia/${id}`);
  }
}
