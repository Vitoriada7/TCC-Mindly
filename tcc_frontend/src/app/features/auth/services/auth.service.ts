import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { apiConfig } from '../../../core/config/api.config';
import {
  CadastroRequest,
  LoginRequest,
  RespostaAutenticacao,
  RespostaCadastro,
} from '../models/autenticacao.models';

@Injectable({ providedIn: 'root' })
export class AuthApiService {
  private readonly http = inject(HttpClient);
  private readonly urlAutenticacao = `${apiConfig.urlBase}/autenticacao`;

  login(dados: LoginRequest): Observable<RespostaAutenticacao> {
    return this.http.post<RespostaAutenticacao>(`${this.urlAutenticacao}/login`, dados);
  }

  cadastrar(dados: CadastroRequest): Observable<RespostaCadastro> {
    return this.http.post<RespostaCadastro>(`${this.urlAutenticacao}/cadastro`, dados);
  }
}
