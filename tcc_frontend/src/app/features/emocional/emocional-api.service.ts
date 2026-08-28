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

  listar(): Observable<RegistroEmocional[]> {
    return this.http.get<RegistroEmocional[]>(`${apiConfig.urlBase}/registros-emocionais`);
  }
}
