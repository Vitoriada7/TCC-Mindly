import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize, map, of, switchMap } from 'rxjs';
import { AvatarComponent } from '../../shared/avatar/avatar.component';
import { ResumoGamificacao } from '../gamificacao/models/gamificacao.models';
import { GamificacaoApiService } from '../gamificacao/services/gamificacao-api.service';
import { EmocionalApiService } from '../emocional/emocional-api.service';
import { AuthService } from '../../core/auth/auth.service';
import { UsuarioAutenticado } from '../../core/user/usuario.models';
import { UsuarioService } from '../../core/user/usuario.service';

@Component({
  selector: 'app-perfil',
  imports: [ReactiveFormsModule, RouterLink, AvatarComponent],
  templateUrl: './perfil.component.html',
  styleUrl: './perfil.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PerfilComponent {
  private readonly gamificacaoApi = inject(GamificacaoApiService);
  private readonly emocionalApi = inject(EmocionalApiService);
  protected readonly resumo = signal<ResumoGamificacao | null>(null);
  protected readonly totalRegistros = signal(0);
  protected readonly totalConquistas = computed(() => this.resumo()?.conquistas.filter((conquista) => conquista.status === 'CONCLUIDA').length ?? 0);
  protected readonly sequenciaRegistros = computed(() => this.resumo()?.emocional.sequenciaAtual ?? 0);
  protected readonly editando = signal(false);
  private readonly usuarioService = inject(UsuarioService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly formBuilder = inject(FormBuilder).nonNullable;

  protected readonly carregando = signal(true);
  protected readonly salvando = signal(false);
  protected readonly erro = signal<string | null>(null);
  protected readonly sucesso = signal(false);
  protected readonly usuario = signal<UsuarioAutenticado | null>(null);
  protected readonly formulario = this.formBuilder.group({ apelido: ['', [Validators.required, Validators.maxLength(30)]] });

  constructor() { this.carregar(); }

  protected editarApelido(): void {
    this.formulario.reset({ apelido: this.usuario()?.apelido ?? '' });
    this.sucesso.set(false);
    this.erro.set(null);
    this.editando.set(true);
  }

  protected cancelarEdicao(): void {
    this.formulario.reset({ apelido: this.usuario()?.apelido ?? '' });
    this.editando.set(false);
  }

  protected carregar(): void {
    this.carregando.set(true); this.erro.set(null);
    this.gamificacaoApi.resumo().pipe(
      switchMap((resumo) => {
        this.resumo.set(resumo);
        return resumo.emocional.totalRegistros === undefined
          ? this.emocionalApi.listar().pipe(map((registros) => registros.length))
          : of(resumo.emocional.totalRegistros);
      }),
      switchMap((total) => { this.totalRegistros.set(total); return this.usuarioService.obterPerfil(); }),
      finalize(() => this.carregando.set(false))).subscribe({
      next: (usuario) => { this.usuario.set(usuario); this.formulario.setValue({ apelido: usuario.apelido }); },
      error: () => this.erro.set('Não foi possível carregar seus dados. Tente novamente.'),
    });
  }

  protected salvar(): void {
    if (this.salvando()) return;
    this.formulario.controls.apelido.setValue(this.formulario.controls.apelido.value.trim());
    if (this.formulario.invalid) { this.formulario.markAllAsTouched(); return; }
    this.salvando.set(true); this.erro.set(null); this.sucesso.set(false);
    this.usuarioService.atualizarPerfil({ apelido: this.formulario.getRawValue().apelido.trim() }).pipe(finalize(() => this.salvando.set(false))).subscribe({
      next: (usuario) => { this.usuario.set(usuario); this.formulario.setValue({ apelido: usuario.apelido }); this.sucesso.set(true); this.editando.set(false); },
      error: () => this.erro.set('Não foi possível salvar suas alterações. Tente novamente.'),
    });
  }

  protected sair(): void { this.authService.logout(); this.router.navigate(['/login']); }
}
