import { NgOptimizedImage } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { imagemAvatar } from '../../core/user/avatares';

@Component({
  selector: 'app-avatar',
  imports: [NgOptimizedImage],
  template: `@if (imagem(); as imagem) { <img [ngSrc]="imagem" width="76" height="76" alt="Avatar do perfil" /> } @else { {{ iniciais() }} }`,
  styles: `:host { display:grid; place-items:center; width:100%; height:100%; } img { width:100%; height:100%; object-fit:contain; }`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AvatarComponent {
  readonly avatar = input<string | null>();
  readonly nome = input('Mindly');
  protected readonly imagem = computed(() => imagemAvatar(this.avatar()));
  protected readonly iniciais = computed(() => this.nome().trim().split(/\s+/).slice(0, 2).map((nome) => nome[0]).join('').toUpperCase());
}
