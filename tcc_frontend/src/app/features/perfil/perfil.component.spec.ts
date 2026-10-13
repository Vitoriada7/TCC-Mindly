import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, tap, throwError } from 'rxjs';
import { TemaService } from '../../core/theme/tema.service';
import { AuthService } from '../../core/auth/auth.service';
import { UsuarioService } from '../../core/user/usuario.service';
import { UsuarioAutenticado } from '../../core/user/usuario.models';
import { EmocionalApiService } from '../emocional/emocional-api.service';
import { ResumoGamificacao } from '../gamificacao/models/gamificacao.models';
import { GamificacaoApiService } from '../gamificacao/services/gamificacao-api.service';
import { BibliotecaAvataresComponent } from './biblioteca-avatares.component';
import { PerfilComponent } from './perfil.component';

const resumo: ResumoGamificacao = {
  habitos: { sequenciaAtual: 0, melhorSequencia: 0, concluidosHoje: 0, metaHoje: 0 },
  tarefas: { sequenciaSemanal: 0, concluidasSemana: 1, metaSemanal: 5, concluidasNoPrazoSemana: 0 },
  emocional: {
    sequenciaAtual: 7,
    melhorSequencia: 7,
    registrouHoje: true,
    sentimentoHoje: 'bem',
    totalRegistros: 12,
  },
  conquistas: [
    {
      codigo: 'PRIMEIRA_TAREFA',
      titulo: 'Tirando do papel',
      descricao: 'Conclua sua primeira tarefa.',
      trilha: 'TAREFAS',
      status: 'CONCLUIDA',
      progresso: 1,
      meta: 1,
    },
    {
      codigo: 'ROTINA_SUAVE',
      titulo: 'Rotina suave',
      descricao: 'Mantenha 7 dias de hábitos em sequência.',
      trilha: 'HABITOS',
      status: 'EM_PROGRESSO',
      progresso: 2,
      meta: 7,
    },
  ],
};

describe('Perfil e biblioteca de avatares', () => {
  afterEach(() => {
    TestBed.inject(TemaService).aplicar(false);
    localStorage.removeItem('mindly.tema');
  });
  async function configurar() {
    const usuario = signal<UsuarioAutenticado | null>({
      id: 7,
      nome: 'Ana Silva',
      apelido: 'Ana',
      email: 'ana@example.test',
      dataNascimento: '2000-01-01',
      dataCriacao: '2026-01-01',
      avatar: null,
      avataresLiberados: [],
    });
    const atualizarAvatar = vi.fn((avatar: string | null) =>
      of({ ...usuario()!, avatar }).pipe(tap((dados) => usuario.set(dados))),
    );
    const atualizarTema = vi.fn((temaEscuro: boolean) => of({ ...usuario()!, temaEscuro }));
    await TestBed.configureTestingModule({
      imports: [PerfilComponent, BibliotecaAvataresComponent],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: { logout: vi.fn() } },
        { provide: EmocionalApiService, useValue: { listar: vi.fn(() => of([])) } },
        { provide: GamificacaoApiService, useValue: { resumo: () => of(resumo) } },
        {
          provide: UsuarioService,
          useValue: { usuario, obterPerfil: () => of(usuario()), atualizarAvatar, atualizarTema },
        },
      ],
    }).compileComponents();
    return { usuario, atualizarAvatar, atualizarTema };
  }

  it('ativa e desativa o tema pelo switch acessível', async () => {
    const { atualizarTema } = await configurar();
    const fixture = TestBed.createComponent(PerfilComponent);
    await fixture.whenStable();
    const toggle = (fixture.nativeElement as HTMLElement).querySelector<HTMLButtonElement>(
      '[role="switch"]',
    )!;
    toggle.click();
    await fixture.whenStable();
    expect(atualizarTema).toHaveBeenCalledWith(true);
    expect(toggle.getAttribute('aria-checked')).toBe('true');
    toggle.click();
    await fixture.whenStable();
    expect(atualizarTema).toHaveBeenCalledWith(false);
    expect(toggle.getAttribute('aria-checked')).toBe('false');
  });

  it('restaura o tema anterior e informa quando a preferência não puder ser salva', async () => {
    const { atualizarTema } = await configurar();
    atualizarTema.mockImplementation(() => throwError(() => new Error('Falha de rede')));
    const fixture = TestBed.createComponent(PerfilComponent);
    await fixture.whenStable();
    const element = fixture.nativeElement as HTMLElement;
    element.querySelector<HTMLButtonElement>('[role="switch"]')!.click();
    await fixture.whenStable();
    expect(TestBed.inject(TemaService).escuro()).toBe(false);
    expect(element.textContent).toContain('Não foi possível salvar o tema');
  });

  it('mostra os três contadores com o total de registros no centro e leva à página própria da biblioteca', async () => {
    await configurar();
    const fixture = TestBed.createComponent(PerfilComponent);
    await fixture.whenStable();
    const element = fixture.nativeElement as HTMLElement;
    const contadores = element.querySelectorAll('.profile-stats > div');
    expect(contadores).toHaveLength(3);
    expect(contadores[0].textContent).toContain('1');
    expect(contadores[1].textContent).toContain('Total de registros');
    expect(contadores[1].textContent).toContain('12');
    expect(contadores[1].classList.contains('featured')).toBe(true);
    expect(contadores[2].textContent).toContain('7');
    expect(element.querySelector('.avatar-grid')).toBeNull();
    expect(element.querySelector('a[href="/perfil/avatares"]')).not.toBeNull();
  });

  it('libera a recompensa concluída mesmo com a lista do perfil desatualizada e salva a seleção', async () => {
    const { atualizarAvatar, usuario } = await configurar();
    const fixture = TestBed.createComponent(BibliotecaAvataresComponent);
    await fixture.whenStable();
    const element = fixture.nativeElement as HTMLElement;
    const estrela = element.querySelector<HTMLButtonElement>(
      'button[aria-label="Estrela skatista: usar como avatar"]',
    )!;
    const flor = element.querySelector<HTMLButtonElement>(
      'button[aria-label="Flor artista: bloqueado"]',
    )!;
    expect(estrela.disabled).toBe(false);
    expect(flor).toBeNull();
    expect(element.textContent).toContain('1 de 13 liberados');
    estrela.click();
    await fixture.whenStable();
    expect(atualizarAvatar).toHaveBeenCalledWith('ESTRELA');
    expect(usuario()?.avatar).toBe('ESTRELA');
    expect(element.textContent).toContain('Seu avatar foi atualizado!');
    expect(element.querySelector('button[aria-label="Estrela skatista: em uso"]')).not.toBeNull();
  });
});
