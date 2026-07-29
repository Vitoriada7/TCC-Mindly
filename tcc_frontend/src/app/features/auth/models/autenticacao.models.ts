export interface LoginRequest {
  email: string;
  senha: string;
}

export interface CadastroRequest {
  nome: string;
  apelido: string;
  email: string;
  senha: string;
  dataNascimento: string;
}

export interface RespostaAutenticacao {
  token: string;
}

export interface RespostaCadastro {
  id: number;
  nome: string;
  apelido: string;
  email: string;
  dataNascimento: string;
  dataCriacao: string;
}
