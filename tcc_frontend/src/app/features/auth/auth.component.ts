import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { NgOptimizedImage } from '@angular/common';
import { AbstractControl, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-auth',
  imports: [NgOptimizedImage, ReactiveFormsModule, RouterLink],
  templateUrl: './auth.component.html',
  styleUrl: './auth.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AuthComponent {
  private readonly formulario = inject(FormBuilder).nonNullable;
  private readonly rota = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);

  protected readonly modoCadastro = computed(
    () => this.rota.parent?.snapshot.routeConfig?.path === 'cadastro',
  );
  protected readonly enviando = signal(false);
  protected readonly mensagemErro = signal<string | null>(null);

  protected readonly formLogin = this.formulario.group({
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required]],
  });

  protected readonly formCadastro = this.formulario.group({
    nome: ['', [Validators.required, Validators.maxLength(100)]],
    apelido: ['', [Validators.required, Validators.maxLength(30)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(150)]],
    senha: ['', [Validators.required, Validators.minLength(6)]],
    dataNascimento: ['', Validators.required],
  });

  protected enviar(): void {
    const formularioAtual = this.modoCadastro() ? this.formCadastro : this.formLogin;
    this.mensagemErro.set(null);

    if (formularioAtual.invalid) {
      formularioAtual.markAllAsTouched();
      return;
    }

    this.enviando.set(true);
    if (this.modoCadastro()) {
      this.authService
        .cadastrar(this.formCadastro.getRawValue())
        .pipe(finalize(() => this.enviando.set(false)))
        .subscribe({
          next: () => this.router.navigate(['/login'], { queryParams: { cadastrado: 'true' } }),
          error: (erro) => this.mensagemErro.set(this.obterMensagemErro(erro)),
        });
      return;
    }

    this.authService
      .login(this.formLogin.getRawValue())
      .pipe(finalize(() => this.enviando.set(false)))
      .subscribe({
        next: () => this.router.navigateByUrl(this.rota.snapshot.queryParamMap.get('retorno') || '/'),
        error: (erro) => this.mensagemErro.set(this.obterMensagemErro(erro)),
      });
  }

  protected invalido(campo: AbstractControl<string>): boolean {
    return campo.invalid && campo.touched;
  }

  private obterMensagemErro(erro: { error?: { message?: string } }): string {
    return erro.error?.message || 'Não foi possível concluir a solicitação. Tente novamente.';
  }
}
