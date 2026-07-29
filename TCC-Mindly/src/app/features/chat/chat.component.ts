import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
type Message = { content: string; mine: boolean };
@Component({
  selector: 'app-chat',
  imports: [ReactiveFormsModule],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChatComponent {
  protected readonly message = new FormControl('', { nonNullable: true });
  protected readonly messages = signal<Message[]>([
    {
      content: 'Oi, Ana! Como você está se sentindo agora? Estou aqui para conversar sem pressa.',
      mine: false,
    },
  ]);
  protected send(): void {
    const content = this.message.value.trim();
    if (!content) return;
    this.messages.update((items) => [
      ...items,
      { content, mine: true },
      {
        content:
          'Obrigada por compartilhar. Que tal respirar fundo e pensar em uma pequena coisa que pode deixar este momento mais leve?',
        mine: false,
      },
    ]);
    this.message.setValue('');
  }
}
