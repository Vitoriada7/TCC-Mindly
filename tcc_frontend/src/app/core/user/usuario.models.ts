export interface UsuarioAutenticado {
  id: number;
  nome: string;
  apelido: string;
  email: string;
  dataNascimento: string;
  dataCriacao: string;
}

export interface AtualizarPerfilRequest {
  apelido: string;
}
