import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { catchError, map, Observable } from 'rxjs';
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

  analisarVoz(audio: Blob): Observable<AnaliseVozResponse> {
    const form = new FormData();
    form.append('audio', audio, 'relato');
    return this.http
      .post<AnaliseVozResponse>(`${apiConfig.urlBase}/registros-emocionais/ia/analise-voz`, form)
      .pipe(
        catchError(() =>
          this.transcrever(audio).pipe(
            map(({ texto }) => ({ texto, analiseDisponivel: false, observacoesVocais: [] })),
          ),
        ),
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
  observacoesVocais?: string[];
}

export interface AnaliseVozResponse {
  texto: string;
  analiseDisponivel: boolean;
  observacoesVocais: string[];
}
