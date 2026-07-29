export type StatusTarefa = 'PENDENTE' | 'EM_ANDAMENTO' | 'CONCLUIDA';
export type Prioridade = 'BAIXA' | 'MEDIA' | 'ALTA';

export interface Tarefa {
  id: number;
  titulo: string;
  descricao: string | null;
  status: StatusTarefa;
  prioridade: Prioridade;
  dataCriacao: string;
  dataAtualizacao: string;
  dataLimite: string | null;
  dataConclusao: string | null;
  categoriaId: number;
  vencida: boolean;
}

export interface CriarTarefaRequest {
  titulo: string;
  descricao?: string | null;
  prioridade: Prioridade;
  dataLimite?: string | null;
  categoriaId: number;
}

export type AtualizarTarefaRequest = CriarTarefaRequest;

export interface Categoria {
  id: number;
  nome: string;
}

export interface CategoriaRequest {
  nome: string;
}
