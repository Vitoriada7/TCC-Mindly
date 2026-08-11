import { ChangeDetectionStrategy, Component, signal } from '@angular/core';

type CentroApoio = { nome: string; telefone: string; endereco: string; distancia: string; tipo: string };

@Component({
  selector: 'app-apoio',
  templateUrl: './apoio.component.html',
  styleUrl: './apoio.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApoioComponent {
  protected readonly centros: CentroApoio[] = [
    { nome: 'Centro de Apoio Sereno', telefone: '0800 123 456', endereco: 'Rua do Aconchego, 120', distancia: '1,2 km', tipo: 'Apoio emocional' },
    { nome: 'Espaço Mindful', telefone: '0800 987 654', endereco: 'Av. Tranquila, 88', distancia: '2,0 km', tipo: 'Aconselhamento' },
    { nome: 'Linha Amiga 24h', telefone: '188', endereco: 'Atendimento telefônico', distancia: '', tipo: 'Emergência' },
  ];

  protected readonly contatosImportantes = [
    { nome: 'Amigo ou familiar', telefone: '0800 555 000' },
    { nome: 'Psicólogo', telefone: '0800 555 111' },
  ];
}
