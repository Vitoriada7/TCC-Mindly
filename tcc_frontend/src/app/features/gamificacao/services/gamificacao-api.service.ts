import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { apiConfig } from '../../../core/config/api.config';
import { ResumoGamificacao } from '../models/gamificacao.models';

@Injectable({ providedIn: 'root' })
export class GamificacaoApiService {
  private readonly http = inject(HttpClient);

  resumo(): Observable<ResumoGamificacao> {
    return this.http.get<ResumoGamificacao>(`${apiConfig.urlBase}/gamificacao/resumo`);
  }
}
