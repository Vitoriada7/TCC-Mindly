import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { apiConfig } from '../../core/config/api.config';
import { RegistroEmocional, RegistroEmocionalRequest } from './emocional.models';

@Injectable({ providedIn: 'root' })
export class EmocionalApiService {
  private readonly http = inject(HttpClient);

  registrar(request: RegistroEmocionalRequest): Observable<RegistroEmocional> {
    return this.http.post<RegistroEmocional>(`${apiConfig.urlBase}/registros-emocionais`, request);
  }

  gerarPerguntas(request: AprofundamentoRequest): Observable<{ perguntas: string[] }> {
    return this.http.post<{ perguntas: string[] }>(
      `${apiConfig.urlBase}/registros-emocionais/ia/perguntas`,
      request,
    );
  }

  gerarApoio(request: AprofundamentoRequest): Observable<{ texto: string }> {
    return this.http.post<{ texto: string }>(
      `${apiConfig.urlBase}/registros-emocionais/ia/apoio`,
      request,
    );
  }

  transcrever(audio: Blob): Observable<{ texto: string }> {
    const form = new FormData();
    form.append('audio', audio, 'relato');
    return this.http.post<{ texto: string }>(
      `${apiConfig.urlBase}/registros-emocionais/ia/transcricao`,
      form,
    );
  }

  listar(): Observable<RegistroEmocional[]> {
    return this.http.get<RegistroEmocional[]>(`${apiConfig.urlBase}/registros-emocionais`);
  }
}

export interface AprofundamentoRequest {
  sentimento: string;
  sentimentoDetalhado: string | null;
  pensamento: string;
  perguntas?: string[];
  respostas?: string[];
}
