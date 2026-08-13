export interface ContatoEmergencia {
  id: number;
  nome: string;
  telefone: string;
  relacionamento: string | null;
  principal: boolean;
}

export interface ContatoEmergenciaRequest {
  nome: string;
  telefone: string;
  relacionamento: string;
  principal: boolean;
}
