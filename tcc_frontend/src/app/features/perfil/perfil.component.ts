import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/auth/auth.service';
import { UsuarioAutenticado } from '../../core/user/usuario.models';
import { UsuarioService } from '../../core/user/usuario.service';

@Component({
  selector: 'app-perfil',
  imports: [ReactiveFormsModule],
  templateUrl: './perfil.component.html',
  styleUrl: './perfil.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PerfilComponent {
  private readonly usuarioService = inject(UsuarioService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly formBuilder = inject(FormBuilder).nonNullable;

  protected readonly carregando = signal(true);
  protected readonly salvando = signal(false);
  protected readonly erro = signal<string | null>(null);
  protected readonly sucesso = signal(false);
  protected readonly usuario = signal<UsuarioAutenticado | null>(null);
  protected readonly iniciais = computed(() => this.usuario()?.nome.trim().split(/\s+/).slice(0, 2).map((nome) => nome[0]).join('').toUpperCase() || '');
  protected readonly formulario = this.formBuilder.group({ nome: ['', [Validators.required, Validators.maxLength(100)]] });

  constructor() { this.carregar(); }

  protected carregar(): void {
    this.carregando.set(true); this.erro.set(null);
    this.usuarioService.obterPerfil().pipe(finalize(() => this.carregando.set(false))).subscribe({
      next: (usuario) => { this.usuario.set(usuario); this.formulario.setValue({ nome: usuario.nome }); },
      error: () => this.erro.set('Não foi possível carregar seus dados. Tente novamente.'),
    });
  }

  protected salvar(): void {
    if (this.formulario.invalid) { this.formulario.markAllAsTouched(); return; }
    this.salvando.set(true); this.erro.set(null); this.sucesso.set(false);
    this.usuarioService.atualizarPerfil({ nome: this.formulario.getRawValue().nome.trim() }).pipe(finalize(() => this.salvando.set(false))).subscribe({
      next: (usuario) => { this.usuario.set(usuario); this.formulario.setValue({ nome: usuario.nome }); this.sucesso.set(true); },
      error: () => this.erro.set('Não foi possível salvar suas alterações. Tente novamente.'),
    });
  }

  protected sair(): void { this.authService.logout(); this.router.navigate(['/login']); }
}
