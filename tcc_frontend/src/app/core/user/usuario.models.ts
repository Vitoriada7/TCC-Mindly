export interface UsuarioAutenticado {
  id: number;
  temaEscuro?: boolean;
  avatar?: string | null;
  avataresLiberados?: string[];
  nome: string;
  apelido: string;
  email: string;
  dataNascimento: string;
  dataCriacao: string;
}

export interface AtualizarPerfilRequest {
  apelido: string;
}
