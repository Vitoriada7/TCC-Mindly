import { isPlatformBrowser } from '@angular/common';
import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  ElementRef,
  OnInit,
  OnDestroy,
  PLATFORM_ID,
  ViewChild,
  inject,
  signal,
} from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import * as L from 'leaflet';
import { ApoioApiService } from './apoio-api.service';
import { ContatoEmergencia, ContatoEmergenciaRequest } from './apoio.models';

@Component({
  selector: 'app-apoio',
  imports: [ReactiveFormsModule],
  templateUrl: './apoio.component.html',
  styleUrl: './apoio.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApoioComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('mapa') private mapaElemento?: ElementRef<HTMLElement>;

  private readonly apoioApi = inject(ApoioApiService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly platformId = inject(PLATFORM_ID);
  private mapa?: L.Map;
  private marcadorUsuario?: L.Marker;

  protected readonly contatos = signal<ContatoEmergencia[]>([]);
  protected readonly buscandoLocalizacao = signal(false);
  protected readonly mensagemLocalizacao = signal(
    'Use sua localização para visualizar sua posição aproximada no mapa.',
  );
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

  ngAfterViewInit(): void {
    if (!isPlatformBrowser(this.platformId) || !this.mapaElemento) {
      return;
    }
    this.mapa = L.map(this.mapaElemento.nativeElement, { scrollWheelZoom: false }).setView(
      [-30.0346, -51.2177],
      7,
    );
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '&copy; OpenStreetMap contributors',
    }).addTo(this.mapa);
  }

  ngOnDestroy(): void {
    this.mapa?.remove();
  }

  protected usarMinhaLocalizacao(): void {
    if (!isPlatformBrowser(this.platformId) || !navigator.geolocation) {
      this.mensagemLocalizacao.set('Seu navegador não oferece suporte à localização.');
      return;
    }

    this.buscandoLocalizacao.set(true);
    this.mensagemLocalizacao.set('Obtendo sua localização...');
    navigator.geolocation.getCurrentPosition(
      ({ coords }) => {
        this.buscandoLocalizacao.set(false);
        this.mensagemLocalizacao.set('Sua localização aproximada está indicada no mapa.');
        this.mostrarLocalizacao(coords.latitude, coords.longitude);
      },
      () => {
        this.buscandoLocalizacao.set(false);
        this.mensagemLocalizacao.set(
          'Não foi possível acessar sua localização. Verifique a permissão do navegador.',
        );
      },
      { enableHighAccuracy: false, timeout: 10_000, maximumAge: 300_000 },
    );
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

  private mostrarLocalizacao(latitude: number, longitude: number): void {
    if (!this.mapa) {
      return;
    }
    const posicao: L.LatLngExpression = [latitude, longitude];
    const iconeUsuario = L.divIcon({
      className: 'marcador-mapa marcador-usuario',
      html: '<span></span>',
    });
    this.marcadorUsuario?.remove();
    this.marcadorUsuario = L.marker(posicao, {
      icon: iconeUsuario,
      title: 'Sua localização',
    })
      .bindPopup('Sua localização aproximada')
      .addTo(this.mapa);
    this.mapa.setView(posicao, 14);
    this.marcadorUsuario.openPopup();
  }

}
