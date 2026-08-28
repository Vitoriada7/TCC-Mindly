export interface RegistroEmocionalRequest {
  sentimento: string;
  sentimentoDetalhado: string | null;
  pensamento: string | null;
  exploracoes: string[];
  reflexao: string | null;
}

export interface RegistroEmocional {
  id: number;
  sentimento: string;
  sentimentoDetalhado: string | null;
  pensamento: string | null;
  reflexao: string | null;
  dataRegistro: string;
}
