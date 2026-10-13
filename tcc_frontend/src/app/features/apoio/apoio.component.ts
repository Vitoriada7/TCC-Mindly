import { CarregamentoComponent } from '../../shared/components/carregamento/carregamento.component';
import { isPlatformBrowser } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  OnInit,
  PLATFORM_ID,
  inject,
  signal,
} from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ApoioApiService } from './apoio-api.service';
import { ContatoEmergencia, ContatoEmergenciaRequest } from './apoio.models';

@Component({
  selector: 'app-apoio',
  imports: [CarregamentoComponent, ReactiveFormsModule],
  templateUrl: './apoio.component.html',
  styleUrl: './apoio.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApoioComponent implements OnInit {
  private readonly apoioApi = inject(ApoioApiService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly platformId = inject(PLATFORM_ID);

  protected readonly contatos = signal<ContatoEmergencia[]>([]);
  protected readonly carregandoContatos = signal(true);
  protected readonly salvandoContato = signal(false);
  protected readonly formularioVisivel = signal(false);
  protected readonly erroContato = signal('');
  protected readonly contatoEmEdicao = signal<number | null>(null);

  protected readonly formularioContato = new FormGroup({
    nome: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.maxLength(100)] }),
    telefone: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.minLength(8), Validators.maxLength(20)] }),
    relacionamento: new FormControl('', { nonNullable: true, validators: [Validators.maxLength(50)] }),
    principal: new FormControl(false, { nonNullable: true }),
  });

  ngOnInit(): void {
    this.carregarContatos();
  }

  protected abrirFormulario(): void {
    this.contatoEmEdicao.set(null);
    this.formularioContato.reset({ nome: '', telefone: '', relacionamento: '', principal: false });
    this.erroContato.set('');
    this.formularioVisivel.set(true);
  }

  protected editarContato(contato: ContatoEmergencia): void {
    this.contatoEmEdicao.set(contato.id);
    this.formularioContato.setValue({
      nome: contato.nome,
      telefone: contato.telefone,
      relacionamento: contato.relacionamento ?? '',
      principal: contato.principal,
    });
    this.erroContato.set('');
    this.formularioVisivel.set(true);
  }

  protected cancelarFormulario(): void {
    this.formularioVisivel.set(false);
    this.contatoEmEdicao.set(null);
    this.erroContato.set('');
  }

  protected salvarContato(): void {
    if (this.formularioContato.invalid) {
      this.formularioContato.markAllAsTouched();
      return;
    }

    const dados: ContatoEmergenciaRequest = this.formularioContato.getRawValue();
    const id = this.contatoEmEdicao();
    const requisicao = id === null
      ? this.apoioApi.criarContato(dados)
      : this.apoioApi.atualizarContato(id, dados);

    this.salvandoContato.set(true);
    this.erroContato.set('');
    requisicao.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: () => {
        this.salvandoContato.set(false);
        this.cancelarFormulario();
        this.carregarContatos();
      },
      error: () => {
        this.salvandoContato.set(false);
        this.erroContato.set('Não foi possível salvar o contato. Tente novamente.');
      },
    });
  }

  protected excluirContato(contato: ContatoEmergencia): void {
    if (!isPlatformBrowser(this.platformId) || !window.confirm(`Excluir o contato ${contato.nome}?`)) {
      return;
    }
    this.apoioApi.excluirContato(contato.id).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: () => this.carregarContatos(),
      error: () => this.erroContato.set('Não foi possível excluir o contato.'),
    });
  }

  protected telefoneParaLink(telefone: string): string {
    return telefone.replace(/[^+\d]/g, '');
  }

  private carregarContatos(): void {
    this.carregandoContatos.set(true);
    this.apoioApi.listarContatos().pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (contatos) => {
        this.contatos.set(contatos);
        this.carregandoContatos.set(false);
      },
      error: () => {
        this.carregandoContatos.set(false);
        this.erroContato.set('Não foi possível carregar seus contatos.');
      },
    });
  }
}
