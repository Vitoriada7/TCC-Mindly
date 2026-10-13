import { ChangeDetectionStrategy, Component, ElementRef, effect, inject, signal, viewChild } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize } from 'rxjs';
import { GoogleTasksApiService, EstadoGoogleTasks } from './services/google-tasks-api.service';

@Component({
  selector: 'app-google-tasks',
  imports: [DatePipe],
  templateUrl: './google-tasks.component.html',
  styleUrl: './google-tasks.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GoogleTasksComponent {
  private readonly api = inject(GoogleTasksApiService);
  protected readonly estado = signal<EstadoGoogleTasks | null>(null);
  protected readonly processando = signal(false);
  protected readonly mensagem = signal('');
  protected readonly erro = signal('');
  protected readonly modalAberto = signal(false);
  private readonly dialog = viewChild<ElementRef<HTMLDialogElement>>('dialog');
  private readonly trigger = viewChild<ElementRef<HTMLButtonElement>>('trigger');

  constructor() {
    effect((onCleanup) => {
      const dialog = this.dialog()?.nativeElement;
      if (!dialog || !this.modalAberto()) return;
      const overflowAnterior = document.body.style.overflow;
      document.body.style.overflow = 'hidden';
      dialog.showModal();
      onCleanup(() => {
        dialog.close();
        document.body.style.overflow = overflowAnterior;
        this.trigger()?.nativeElement.focus();
      });
    });
    const url = new URL(window.location.href);
    const code = url.searchParams.get('code');
    const state = url.searchParams.get('state');
    const negado = url.searchParams.get('error');
    if (code || state || negado) {
      this.modalAberto.set(true);
      for (const parametro of ['code', 'state', 'error', 'error_description', 'scope', 'authuser', 'prompt']) {
        url.searchParams.delete(parametro);
      }
      window.history.replaceState(window.history.state, '', url.pathname + url.search + url.hash);
    }
    if (code && state) {
      this.processando.set(true);
      this.api.conectar(code, state).pipe(finalize(() => this.processando.set(false))).subscribe({
        next: () => { this.mensagem.set('Conta conectada. Clique em Sincronizar tarefas para enviá-las ao Google.'); this.carregar(); },
        error: (erro: unknown) => { this.falha(erro); this.carregar(); },
      });
    } else {
      if (negado) this.erro.set('Conexão cancelada. Você pode continuar usando o Mindly normalmente.');
      this.carregar();
    }
  }
  protected abrirModal(): void {
    this.modalAberto.set(true);
    this.carregar();
  }
  protected fecharModal(): void {
    this.modalAberto.set(false);
  }
  protected fecharPeloFundo(event: MouseEvent): void {
    if (event.target === this.dialog()?.nativeElement) this.fecharModal();
  }
  protected carregar(): void {
    this.api.estado().subscribe({ next: (estado) => this.estado.set(estado), error: (erro: unknown) => this.falha(erro) });
  }
  protected conectar(): void {
    if (this.processando()) return;
    this.erro.set(''); this.mensagem.set(''); this.processando.set(true);
    this.api.autorizar().pipe(finalize(() => this.processando.set(false))).subscribe({
      next: ({ url }) => window.location.assign(url), error: (erro: unknown) => this.falha(erro),
    });
  }
  protected sincronizar(): void {
    if (this.processando()) return;
    this.erro.set(''); this.mensagem.set(''); this.processando.set(true);
    this.api.sincronizar().pipe(finalize(() => this.processando.set(false))).subscribe({
      next: ({ tarefas }) => { this.mensagem.set(`${tarefas} tarefa(s) sincronizada(s) com o Google.`); this.carregar(); },
      error: (erro: unknown) => this.falha(erro),
    });
  }
  protected desconectar(): void {
    if (this.processando()) return;
    this.erro.set(''); this.mensagem.set(''); this.processando.set(true);
    this.api.desconectar().pipe(finalize(() => this.processando.set(false))).subscribe({
      next: () => { this.mensagem.set('Conta desconectada. As tarefas já enviadas continuam no Google.'); this.carregar(); },
      error: (erro: unknown) => this.falha(erro),
    });
  }
  private falha(erro: unknown): void {
    this.erro.set(erro instanceof HttpErrorResponse && erro.status === 400
      ? 'Não foi possível conectar. Inicie novamente pelo botão Conectar Google e autorize o acesso às tarefas.'
      : 'Não foi possível concluir a conexão ou sincronização. Tente novamente; se persistir, reconecte sua conta.');
  }
}
