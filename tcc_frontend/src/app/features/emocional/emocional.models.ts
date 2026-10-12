export interface RegistroEmocionalRequest {
  sentimento: string;
  sentimentoDetalhado: string | null;
  pensamento: string | null;
  exploracoes: string[];
  reflexao: string | null;
  complementoFinal?: string | null;
}

export interface RegistroEmocional {
  exploracoes?: string[];
  id: number;
  sentimento: string;
  sentimentoDetalhado: string | null;
  pensamento: string | null;
  reflexao: string | null;
  complementoFinal?: string | null;
  dataRegistro: string;
}
