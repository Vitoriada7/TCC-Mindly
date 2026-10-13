import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { vi } from 'vitest';
import { AuthService } from '../auth/auth.service';
import { TokenService } from '../auth/token.service';
import { apiConfig } from '../config/api.config';
import { jwtInterceptor } from './jwt.interceptor';

describe('jwtInterceptor', () => {
  const url = `${apiConfig.urlBase}/tarefas`;
  const navigate = vi.fn().mockResolvedValue(true);
  let http: HttpClient;
  let controller: HttpTestingController;
  let tokens: TokenService;

  const criarToken = (exp: number) => `header.${btoa(JSON.stringify({ exp }))}.signature`;

  beforeEach(() => {
    localStorage.clear();
    navigate.mockClear();
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([jwtInterceptor])),
        provideHttpClientTesting(),
        { provide: Router, useValue: { url: '/tarefas', navigate } },
      ],
    });
    http = TestBed.inject(HttpClient);
    controller = TestBed.inject(HttpTestingController);
    tokens = TestBed.inject(TokenService);
  });

  afterEach(() => {
    controller.verify();
    localStorage.clear();
  });

  it('encerra a sessão e redireciona quando a API rejeita o token', () => {
    tokens.salvar(criarToken(Date.now() / 1000 + 60));
    const auth = TestBed.inject(AuthService);
    http.get(url).subscribe({ error: () => undefined });
    const request = controller.expectOne(url);
    expect(request.request.headers.get('Authorization')).toBe(`Bearer ${tokens.recuperar()}`);
    request.flush(null, { status: 401, statusText: 'Unauthorized' });
    expect(tokens.recuperar()).toBeNull();
    expect(auth.estaAutenticado()).toBe(false);
    expect(navigate).toHaveBeenCalledWith(['/login'], {
      queryParams: { retorno: '/tarefas' },
      replaceUrl: true,
    });
  });

  it('redireciona antes de enviar uma requisição com token vencido', () => {
    tokens.salvar(criarToken(Date.now() / 1000 - 1));
    http.get(url).subscribe({ error: () => undefined });
    controller.expectNone(url);
    expect(tokens.recuperar()).toBeNull();
    expect(navigate).toHaveBeenCalledOnce();
  });

  it('não encerra a sessão por falta de permissão', () => {
    const token = criarToken(Date.now() / 1000 + 60);
    tokens.salvar(token);
    http.get(url).subscribe({ error: () => undefined });
    controller.expectOne(url).flush(null, { status: 403, statusText: 'Forbidden' });
    expect(tokens.recuperar()).toBe(token);
    expect(navigate).not.toHaveBeenCalled();
  });

  it('não intercepta credenciais incorretas no login', () => {
    const login = `${apiConfig.urlBase}/autenticacao/login`;
    http.post(login, {}).subscribe({ error: () => undefined });
    const request = controller.expectOne(login);
    expect(request.request.headers.has('Authorization')).toBe(false);
    request.flush(null, { status: 401, statusText: 'Unauthorized' });
    expect(navigate).not.toHaveBeenCalled();
  });

  it('rejeita tokens malformados e considera o instante exato de expiração', () => {
    tokens.salvar('invalido');
    expect(tokens.possuiToken()).toBe(false);
    tokens.salvar(criarToken(Math.floor(Date.now() / 1000)));
    expect(tokens.possuiToken()).toBe(false);
    tokens.salvar(criarToken(Date.now() / 1000 + 60));
    expect(tokens.possuiToken()).toBe(true);
  });
});
