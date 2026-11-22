import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

type Recurso = {
  categoria: string;
  titulo: string;
  descricao: string;
  duracao: string;
  icone: string;
  cor: string;
  passos: string[];
  habito: string;
  corHabito: string;
  fonte: string;
  fonteUrl: string;
};

@Component({
  selector: 'app-recursos',
  imports: [RouterLink],
  templateUrl: './recursos.component.html',
  styleUrl: './recursos.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecursosComponent {
  protected readonly categorias = [
    'MEDITAÇÃO',
    'ESTUDO & ORGANIZAÇÃO',
    'ANTI-ESTRESSE',
    'MOVIMENTO'
  ];
  protected readonly recursos: Recurso[] = [
    {
      categoria: "MEDITAÇÃO",
      titulo: "Respiração tranquila",
      descricao: "Uma técnica simples para desacelerar os pensamentos e regular a respiração antes de uma prova ou depois de um dia intenso.",
      duracao: "5 min",
      icone: "air",
      cor: "lavender",
      passos: [
        "Encontre uma posição confortável e perceba o corpo.",
        "Use o ritmo confortável da respiração como foco de atenção.",
        "Ao se distrair, retome a atenção sem julgar os pensamentos."
      ],
      habito: "Praticar respiração tranquila",
      corHabito: "purple",
      fonte: "Einstein Hospital Israelita",
      fonteUrl: "https://www.einstein.br/n/vida-saudavel/5-vantagens-do-mindfulness-e-um-passo-a-passo-para-pratica-lo"
    },
    {
      categoria: "MEDITAÇÃO",
      titulo: "Aterramento 5-4-3-2-1",
      descricao: "Quando a mente estiver acelerada, use os sentidos para retornar ao presente com mais segurança e calma.",
      duracao: "3 min",
      icone: "visibility",
      cor: "lilac",
      passos: [
        "Observe 5 coisas que você vê.",
        "Toque em 4 coisas ao seu alcance.",
        "Perceba 3 sons, 2 cheiros e 1 sabor."
      ],
      habito: "Fazer aterramento 5-4-3-2-1",
      corHabito: "purple",
      fonte: "TJSC — Cartilha de manejo do estresse",
      fonteUrl: "https://www.tjsc.jus.br/documents/37870/7446977/Cartilha%20manejo%20do%20estresse.pdf/36bcb02a-1960-2211-39c2-5b0ca59edc4e?t=1629737356874"
    },
    {
      categoria: "ESTUDO & ORGANIZAÇÃO",
      titulo: "Ciclo de foco gentil",
      descricao: "Reserve um bloco curto para uma tarefa e uma pausa para recuperar o ritmo, respeitando o tempo disponível.",
      duracao: "25 min",
      icone: "menu_book",
      cor: "blue",
      passos: [
        "Defina uma única tarefa possível.",
        "Faça 20 minutos de foco sem notificações.",
        "Descanse 5 minutos antes de decidir o próximo passo."
      ],
      habito: "Fazer um ciclo de foco",
      corHabito: "blue",
      fonte: "UFAL — Saúde no Trabalho",
      fonteUrl: "https://ufal.br/servidor/saude-no-trabalho/projetos/qvt-em-rede/cuidado-com-a-saude-e-qualidade-de-vida-no-trabalho"
    },
    {
      categoria: "ESTUDO & ORGANIZAÇÃO",
      titulo: "Descarrego mental",
      descricao: "Use a escrita para reconhecer o que está pesando e lembrar de estratégias que já ajudaram.",
      duracao: "5 min",
      icone: "edit_note",
      cor: "mint",
      passos: [
        "Escreva o que está causando preocupação ou sobrecarga.",
        "Observe como essas situações fazem você se sentir.",
        "Anote algo que já ajudou antes e que pode experimentar agora."
      ],
      habito: "Fazer descarrego mental",
      corHabito: "green",
      fonte: "Fiocruz Brasília — Cuidados psicológicos",
      fonteUrl: "https://www.fiocruzbrasilia.fiocruz.br/wp-content/uploads/2020/06/cartilha_trabalhadores_saude.pdf"
    },
    {
      categoria: "ANTI-ESTRESSE",
      titulo: "Pausa dos ombros",
      descricao: "Uma pausa breve para perceber e movimentar os ombros depois de ficar muito tempo na mesma posição.",
      duracao: "2 min",
      icone: "self_improvement",
      cor: "peach",
      passos: [
        "Sente-se de forma confortável, com os pés apoiados.",
        "Eleve suavemente os ombros em direção às orelhas.",
        "Abaixe e solte os ombros devagar, sem forçar ou provocar dor."
      ],
      habito: "Fazer pausa dos ombros",
      corHabito: "orange",
      fonte: "UFSCar — Terapia Ocupacional",
      fonteUrl: "https://www.dto.ufscar.br/arquivos/producoes-docentes/o-que-vou-fazer-hoje-_-enviado-publicacao.pdf"
    },
    {
      categoria: "MOVIMENTO",
      titulo: "Caminhada consciente",
      descricao: "Movimente o corpo com atenção ao ritmo, à respiração e ao ambiente — sem precisar de treino intenso.",
      duracao: "10 min",
      icone: "directions_walk",
      cor: "rose",
      passos: [
        "Caminhe em um ritmo confortável.",
        "Sinta o contato dos pés com o chão.",
        "Observe a respiração sem tentar mudá-la."
      ],
      habito: "Fazer caminhada consciente",
      corHabito: "pink",
      fonte: "OPAS/OMS — edição brasileira",
      fonteUrl: "https://www.paho.org/pt/documentos/faca-que-importa-em-tempos-estresse-guia-ilustrado-versao-adaptada-para-brasil"
    },
    {
      categoria: "MEDITAÇÃO",
      titulo: "Atenção à respiração",
      descricao: "Reserve um momento para perceber a respiração e voltar a ela quando a atenção se dispersar.",
      duracao: "5 min",
      icone: "spa",
      cor: "lavender",
      passos: [
        "Sente-se de forma confortável.",
        "Perceba o ar entrando e saindo naturalmente.",
        "Ao se distrair, retome a atenção com gentileza."
      ],
      habito: "Atenção à respiração",
      corHabito: "purple",
      fonte: "Mércia Vieira de Faria — IdeiaSUS/Fiocruz",
      fonteUrl: "https://ideiasus.fiocruz.br/praticas/pratica-de-mindfulness-atencao-plena/"
    },
    {
      categoria: "MEDITAÇÃO",
      titulo: "Observação do presente",
      descricao: "Use uma pausa para perceber detalhes do ambiente, sem precisar avaliar o que encontra.",
      duracao: "3 min",
      icone: "nature",
      cor: "mint",
      passos: [
        "Escolha um lugar tranquilo para observar.",
        "Note cores, formas e sons ao seu redor.",
        "Quando surgirem pensamentos, volte aos detalhes."
      ],
      habito: "Observação do presente",
      corHabito: "green",
      fonte: "Einstein Hospital Israelita",
      fonteUrl: "https://www.einstein.br/n/vida-saudavel/5-vantagens-do-mindfulness-e-um-passo-a-passo-para-pratica-lo"
    },
    {
      categoria: "ESTUDO & ORGANIZAÇÃO",
      titulo: "Uma etapa de cada vez",
      descricao: "Escolha uma prioridade e reserve espaço na agenda para começar sem tentar resolver tudo ao mesmo tempo.",
      duracao: "5 min",
      icone: "checklist",
      cor: "blue",
      passos: [
        "Liste as tarefas que precisam da sua atenção.",
        "Identifique o que é importante e o que também é urgente.",
        "Escolha uma prioridade e reserve um horário com margem para imprevistos."
      ],
      habito: "Uma etapa de cada vez",
      corHabito: "blue",
      fonte: "UFAL — Saúde no Trabalho",
      fonteUrl: "https://ufal.br/servidor/saude-no-trabalho/projetos/qvt-em-rede/cuidado-com-a-saude-e-qualidade-de-vida-no-trabalho"
    },
    {
      categoria: "ESTUDO & ORGANIZAÇÃO",
      titulo: "Planejamento com pausas",
      descricao: "Organize compromissos e momentos de recuperação para construir uma rotina mais equilibrada.",
      duracao: "5 min",
      icone: "edit_note",
      cor: "lilac",
      passos: [
        "Liste os compromissos e as tarefas do próximo dia.",
        "Distribua as atividades em blocos de horário realistas.",
        "Inclua pausas e um momento de autocuidado na agenda."
      ],
      habito: "Planejar a rotina com pausas",
      corHabito: "purple",
      fonte: "UFAL — Saúde no Trabalho",
      fonteUrl: "https://ufal.br/servidor/saude-no-trabalho/projetos/qvt-em-rede/cuidado-com-a-saude-e-qualidade-de-vida-no-trabalho"
    },
    {
      categoria: "ANTI-ESTRESSE",
      titulo: "Nomear os pensamentos",
      descricao: "Reconheça um pensamento difícil como uma experiência da mente antes de voltar ao que está fazendo.",
      duracao: "3 min",
      icone: "psychology",
      cor: "lavender",
      passos: [
        "Perceba qual pensamento está prendendo sua atenção.",
        "Diga mentalmente: estou percebendo este pensamento.",
        "Sinta os pés no chão e volte à atividade presente."
      ],
      habito: "Nomear os pensamentos",
      corHabito: "purple",
      fonte: "OPAS/OMS — edição brasileira",
      fonteUrl: "https://www.paho.org/pt/documentos/faca-que-importa-em-tempos-estresse-guia-ilustrado-versao-adaptada-para-brasil"
    },
    {
      categoria: "ANTI-ESTRESSE",
      titulo: "Gentileza consigo",
      descricao: "Experimente responder a um momento difícil com o mesmo cuidado que ofereceria a alguém querido.",
      duracao: "3 min",
      icone: "favorite",
      cor: "rose",
      passos: [
        "Perceba como você está se sentindo, sem se cobrar estar bem.",
        "Responda a si com palavras de compreensão e cuidado.",
        "Escolha um momento de descanso ou uma atividade prazerosa possível."
      ],
      habito: "Gentileza consigo",
      corHabito: "pink",
      fonte: "Ministério da Saúde — Guia de saúde mental",
      fonteUrl: "https://bvsms.saude.gov.br/bvs/publicacoes/guia_saude_mental_adolescente_11_14_anos.pdf"
    },
    {
      categoria: "ANTI-ESTRESSE",
      titulo: "Reconectar com o ambiente",
      descricao: "Faça uma pausa para perceber o corpo e direcionar sua atenção ao lugar onde está.",
      duracao: "2 min",
      icone: "public",
      cor: "mint",
      passos: [
        "Note os pensamentos e as sensações presentes.",
        "Pressione os pés suavemente contra o chão.",
        "Observe o ambiente e retome uma ação com atenção."
      ],
      habito: "Reconectar com o ambiente",
      corHabito: "green",
      fonte: "OPAS/OMS — edição brasileira",
      fonteUrl: "https://www.paho.org/pt/documentos/faca-que-importa-em-tempos-estresse-guia-ilustrado-versao-adaptada-para-brasil"
    },
    {
      categoria: "MOVIMENTO",
      titulo: "Alongamento sentado",
      descricao: "Abra espaço para um movimento suave do tronco durante uma pausa nos estudos.",
      duracao: "2 min",
      icone: "accessibility_new",
      cor: "blue",
      passos: [
        "Sente-se em uma cadeira estável, com os pés apoiados.",
        "Gire o tronco suavemente para um lado, até um limite confortável.",
        "Mantenha por cerca de 20 segundos e repita do outro lado, sem dor."
      ],
      habito: "Alongamento sentado",
      corHabito: "blue",
      fonte: "Einstein Hospital Israelita",
      fonteUrl: "https://www.einstein.br/n/vida-saudavel/exercicios-de-alongamento"
    },
    {
      categoria: "MOVIMENTO",
      titulo: "Marcha na cadeira",
      descricao: "Movimente as pernas com calma em um intervalo da rotina.",
      duracao: "2 min",
      icone: "directions_run",
      cor: "peach",
      passos: [
        "Sente-se em uma cadeira firme, sem rodas.",
        "Eleve um joelho de cada vez, até onde for confortável.",
        "Alterne as pernas devagar, mantendo os pés apoiados entre movimentos."
      ],
      habito: "Marcha na cadeira",
      corHabito: "orange",
      fonte: "Ministério da Saúde — Prevenção de quedas",
      fonteUrl: "https://bvsms.saude.gov.br/bvs/publicacoes/prevencao_quedas_pessoas_idosasimp.pdf"
    },
    {
      categoria: "MOVIMENTO",
      titulo: "Mobilidade dos tornozelos",
      descricao: "Uma pausa breve para movimentar os pés enquanto você está sentado.",
      duracao: "2 min",
      icone: "steps",
      cor: "mint",
      passos: [
        "Sente-se com apoio e estenda uma perna confortavelmente.",
        "Aponte os dedos do pé para longe e depois em sua direção.",
        "Repita suavemente e troque a perna, sem forçar."
      ],
      habito: "Mobilidade dos tornozelos",
      corHabito: "green",
      fonte: "UFSCar — Terapia Ocupacional",
      fonteUrl: "https://www.dto.ufscar.br/arquivos/producoes-docentes/o-que-vou-fazer-hoje-_-enviado-publicacao.pdf"
    },
  ];

  protected recursosPorCategoria(categoria: string): Recurso[] {
    return this.recursos.filter((recurso) => recurso.categoria === categoria);
  }
}
