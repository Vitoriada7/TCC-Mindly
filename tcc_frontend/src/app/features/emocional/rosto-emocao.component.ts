import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { Emocao } from './emocoes';

@Component({
  selector: 'app-rosto-emocao',
  templateUrl: './rosto-emocao.component.html',
  styleUrl: './rosto-emocao.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: {
    'aria-hidden': 'true',
    '[style.--face-color]': 'emocao().cor',
  },
})
export class RostoEmocaoComponent {
  readonly emocao = input.required<Emocao>();
}
