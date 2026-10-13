import { DOCUMENT } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { apiConfig } from '../config/api.config';
import { TemaService } from '../theme/tema.service';
import { UsuarioService } from './usuario.service';
import { UsuarioAutenticado } from './usuario.models';

describe('Preferência de tema do perfil', () => {
  const perfil: UsuarioAutenticado = {
    id: 7,
    nome: 'Ana',
    apelido: 'Ana',
    email: 'ana@example.test',
    dataNascimento: '2000-01-01',
    dataCriacao: '2026-01-01',
    temaEscuro: true,
  };
  let usuarios: UsuarioService;
  let http: HttpTestingController;
  let tema: TemaService;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    usuarios = TestBed.inject(UsuarioService);
    http = TestBed.inject(HttpTestingController);
    tema = TestBed.inject(TemaService);
  });

  afterEach(() => {
    http.verify();
    tema.aplicar(false);
    localStorage.clear();
  });

  it('aplica o tema salvo na conta ao carregar o perfil', () => {
    usuarios.obterPerfil().subscribe();
    http.expectOne(`${apiConfig.urlBase}/usuarios/me`).flush(perfil);
    expect(tema.escuro()).toBe(true);
    expect(TestBed.inject(DOCUMENT).documentElement.dataset['theme']).toBe('dark');
    expect(JSON.parse(localStorage.getItem('mindly.tema')!)).toEqual({ id: 7, escuro: true });
  });

  it('salva o tema independentemente do apelido e permite desativá-lo', () => {
    usuarios.atualizarTema(false).subscribe();
    const request = http.expectOne(`${apiConfig.urlBase}/usuarios/me/tema`);
    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual({ temaEscuro: false });
    request.flush({ ...perfil, temaEscuro: false });
    expect(tema.escuro()).toBe(false);
  });

  it('limpa os dados e o tema ao sair, sem aplicar a preferência à próxima conta', () => {
    usuarios.obterPerfil().subscribe();
    http.expectOne(`${apiConfig.urlBase}/usuarios/me`).flush(perfil);
    usuarios.limpar();
    expect(usuarios.usuario()).toBeNull();
    expect(tema.escuro()).toBe(false);
    usuarios.obterPerfil().subscribe();
    http
      .expectOne(`${apiConfig.urlBase}/usuarios/me`)
      .flush({ ...perfil, id: 8, temaEscuro: false });
    expect(tema.escuro()).toBe(false);
  });
});
