import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TrilhaGamificacao } from '../../../features/gamificacao/models/gamificacao.models';

@Component({
  selector: 'app-trilha-gamificacao',
  imports: [RouterLink],
  templateUrl: './trilha-gamificacao.component.html',
  styleUrl: './trilha-gamificacao.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TrilhaGamificacaoComponent {
  readonly trilha = input.required<TrilhaGamificacao>();
  readonly icone = input.required<string>();
  readonly titulo = input.required<string>();
  readonly valor = input.required<string>();
  readonly descricao = input.required<string>();
  readonly progresso = input<number | null>(null);
  readonly meta = input<number | null>(null);
  readonly compacto = input(false);
}
