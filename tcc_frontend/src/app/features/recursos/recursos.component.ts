import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

type Recurso = { categoria: string; titulo: string; descricao: string; duracao: string; icone: string; cor: string; passos: string[]; habito: string; corHabito: string };

@Component({
  selector: 'app-recursos',
  imports: [RouterLink],
  templateUrl: './recursos.component.html',
  styleUrl: './recursos.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecursosComponent {
  protected readonly categorias = ['MEDITAÇÃO', 'ESTUDO & ORGANIZAÇÃO', 'ANTI-ESTRESSE', 'MOVIMENTO'];
  protected readonly recursos: Recurso[] = [
    { categoria: 'MEDITAÇÃO', titulo: 'Respiração em caixa', descricao: 'Uma técnica simples para desacelerar os pensamentos e regular a respiração antes de uma prova ou depois de um dia intenso.', duracao: '2 min', icone: 'air', cor: 'lavender', passos: ['Inspire contando até quatro.', 'Segure o ar por quatro segundos.', 'Expire em quatro e espere mais quatro.'], habito: 'Praticar respiração em caixa', corHabito: 'purple' },
    { categoria: 'MEDITAÇÃO', titulo: 'Aterramento 5-4-3-2-1', descricao: 'Quando a mente estiver acelerada, use os sentidos para retornar ao presente com mais segurança e calma.', duracao: '3 min', icone: 'visibility', cor: 'lilac', passos: ['Observe 5 coisas que você vê.', 'Toque em 4 coisas ao seu alcance.', 'Perceba 3 sons, 2 cheiros e 1 sabor.'], habito: 'Fazer aterramento 5-4-3-2-1', corHabito: 'purple' },
    { categoria: 'ESTUDO & ORGANIZAÇÃO', titulo: 'Ciclo de foco gentil', descricao: 'Uma versão leve da técnica Pomodoro: estrutura para começar sem se cobrar produtividade perfeita.', duracao: '25 min', icone: 'menu_book', cor: 'blue', passos: ['Defina uma única tarefa possível.', 'Faça 20 minutos de foco sem notificações.', 'Descanse 5 minutos antes de decidir o próximo passo.'], habito: 'Fazer um ciclo de foco', corHabito: 'blue' },
    { categoria: 'ESTUDO & ORGANIZAÇÃO', titulo: 'Descarrego mental', descricao: 'Tire as pendências da cabeça e transforme a sensação de sobrecarga em uma lista organizada e possível.', duracao: '5 min', icone: 'edit_note', cor: 'mint', passos: ['Anote tudo o que está ocupando sua mente.', 'Marque o que precisa acontecer hoje.', 'Escolha apenas o primeiro passo para começar.'], habito: 'Fazer descarrego mental', corHabito: 'green' },
    { categoria: 'ANTI-ESTRESSE', titulo: 'Pausa dos ombros', descricao: 'Um pequeno intervalo para soltar a tensão que se acumula no pescoço, na mandíbula e nos ombros.', duracao: '2 min', icone: 'self_improvement', cor: 'peach', passos: ['Eleve os ombros ao inspirar.', 'Solte-os devagar ao expirar.', 'Repita três vezes e relaxe a mandíbula.'], habito: 'Fazer pausa dos ombros', corHabito: 'orange' },
    { categoria: 'MOVIMENTO', titulo: 'Caminhada consciente', descricao: 'Movimente o corpo com atenção ao ritmo, à respiração e ao ambiente — sem precisar de treino intenso.', duracao: '10 min', icone: 'directions_walk', cor: 'rose', passos: ['Caminhe em um ritmo confortável.', 'Sinta o contato dos pés com o chão.', 'Observe a respiração sem tentar mudá-la.'], habito: 'Fazer caminhada consciente', corHabito: 'pink' },
  ];

  protected recursosPorCategoria(categoria: string): Recurso[] {
    return this.recursos.filter((recurso) => recurso.categoria === categoria);
  }
}
