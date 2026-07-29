export interface Habito {
  id: number;
  nome: string;
  icone: string;
  cor: string;
  diasConcluidos: string[];
}

export interface HabitoRequest {
  nome: string;
  icone: string;
  cor: string;
}

export interface ResumoHabitos {
  sequenciaAtual: number;
  habitosConcluidosHoje: number;
  totalHabitosAtivos: number;
}
