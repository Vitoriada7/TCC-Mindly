import { NgOptimizedImage } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { imagemAvatar } from '../../core/user/avatares';

@Component({
  selector: 'app-avatar',
  imports: [NgOptimizedImage],
  templateUrl: './avatar.component.html',
  styleUrl: './avatar.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AvatarComponent {
  readonly avatar = input<string | null>();
  readonly nome = input('Mindly');
  protected readonly imagem = computed(() => imagemAvatar(this.avatar()));
  protected readonly iniciais = computed(() =>
    this.nome()
      .trim()
      .split(/\s+/)
      .slice(0, 2)
      .map((nome) => nome[0])
      .join('')
      .toUpperCase(),
  );
}
