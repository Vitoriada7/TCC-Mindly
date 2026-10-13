export const AVATARES = [
  { id: 'ARVORE', nome: 'Árvore aventureira', conquista: 'PRIMEIRO_HABITO', imagem: '/avatares/arvore.png' },
  { id: 'FLOR', nome: 'Flor artista', conquista: 'ROTINA_SUAVE', imagem: '/avatares/flor.png' },
  { id: 'ESTRELA', nome: 'Estrela skatista', conquista: 'PRIMEIRA_TAREFA', imagem: '/avatares/estrela.png' },
  { id: 'CORUJA', nome: 'Coruja leitora', conquista: 'SEMANA_ORGANIZADA', imagem: '/avatares/coruja.png' },
  { id: 'GATO', nome: 'Gato gamer', conquista: 'NO_TEMPO_CERTO', imagem: '/avatares/gato.png' },
  { id: 'CORACAO', nome: 'Coração sereno', conquista: 'PRIMEIRO_REGISTRO', imagem: '/avatares/coracao.png' },
  { id: 'CEREBRO', nome: 'Cérebro meditante', conquista: 'SEMANA_CONSCIENTE', imagem: '/avatares/cerebro.png' },
  { id: 'SOL', nome: 'Sol esportivo', conquista: 'HABITOS_30_DIAS', imagem: '/avatares/sol.png' },
  { id: 'BORBOLETA', nome: 'Borboleta fashion', conquista: 'HABITOS_30_SEGUIDOS', imagem: '/avatares/borboleta.png' },
  { id: 'COGUMELO', nome: 'Cogumelo fotógrafo', conquista: 'TAREFAS_25', imagem: '/avatares/cogumelo.png' },
  { id: 'NUVEM', nome: 'Nuvem indie', conquista: 'TAREFAS_50', imagem: '/avatares/nuvem.png' },
  { id: 'LUA', nome: 'Lua leitora', conquista: 'REGISTROS_30', imagem: '/avatares/lua.png' },
  { id: 'XICARA', nome: 'Xícara de crochê', conquista: 'EMOCOES_30_SEGUIDOS', imagem: '/avatares/xicara.png' }
] as const;

export function imagemAvatar(id: string | null | undefined): string | null {
  return AVATARES.find((avatar) => avatar.id === id)?.imagem ?? null;
}
