export type StatusConquista = 'BLOQUEADA' | 'EM_PROGRESSO' | 'CONCLUIDA';
export type TrilhaGamificacao = 'HABITOS' | 'TAREFAS' | 'EMOCIONAL';

export interface Conquista {
  codigo: string;
  titulo: string;
  descricao: string;
  trilha: TrilhaGamificacao;
  status: StatusConquista;
  progresso: number;
  meta: number;
}

export interface ResumoGamificacao {
  habitos: { sequenciaAtual: number; melhorSequencia: number; concluidosHoje: number; metaHoje: number };
  tarefas: { sequenciaSemanal: number; concluidasSemana: number; metaSemanal: number; concluidasNoPrazoSemana: number };
  emocional: { sequenciaAtual: number; melhorSequencia: number; registrouHoje: boolean; sentimentoHoje: string | null };
  conquistas: Conquista[];
}
