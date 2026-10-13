import { Component, signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of } from 'rxjs';
import { App } from './app';
import { AuthService } from './core/auth/auth.service';
import { UsuarioService } from './core/user/usuario.service';

@Component({ template: '<h1>Entrar no Mindly</h1>' })
class LoginTeste {}

describe('App', () => {
  const autenticado = signal(false);
  const obterPerfil = vi.fn(() => of(null));
  const limpar = vi.fn();

  beforeEach(async () => {
    autenticado.set(false);
    obterPerfil.mockClear();
    limpar.mockClear();
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        provideRouter([{ path: 'login', component: LoginTeste }]),
        { provide: AuthService, useValue: { estaAutenticado: autenticado } },
        { provide: UsuarioService, useValue: { obterPerfil, limpar } },
      ],
    }).compileComponents();
    await TestBed.inject(Router).navigateByUrl('/login');
  });

  it('exibe o login sem a navegação privada', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    const element = fixture.nativeElement as HTMLElement;
    expect(element.querySelector('h1')?.textContent).toContain('Entrar no Mindly');
    expect(element.querySelector('app-sidebar')).toBeNull();
    expect(limpar).toHaveBeenCalledOnce();
    expect(obterPerfil).not.toHaveBeenCalled();
  });

  it('carrega a preferência do perfil ao entrar e limpa os dados ao sair', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    autenticado.set(true);
    await fixture.whenStable();
    expect(obterPerfil).toHaveBeenCalledOnce();
    autenticado.set(false);
    await fixture.whenStable();
    expect(limpar).toHaveBeenCalledTimes(2);
  });
});
