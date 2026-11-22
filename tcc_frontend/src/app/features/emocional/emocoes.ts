export const EMOCOES = [
  {
    value: 'radiante',
    expressao: 'M25 40 Q31 28 37 40 M63 40 Q69 28 75 40 M27 57 Q50 85 73 57 Z',
    label: 'Radiante',
    icon: 'sentiment_very_satisfied',
    cor: 'var(--yellow)',
    detalhes: [
      'Felicidade',
      'Entusiasmo',
      'Gratidão',
      'Orgulho',
      'Empolgação',
      'Inspiração',
      'Euforia',
      'Alegria',
      'Esperança',
      'Encantamento',
      'Amor',
      'Confiança'
    ]
  },
  {
    value: 'bem',
    expressao: 'M25 40 Q31 30 37 40 M63 40 Q69 30 75 40 M29 59 Q50 77 71 59',
    label: 'Bem',
    icon: 'sentiment_satisfied',
    cor: 'var(--green)',
    detalhes: [
      'Calma',
      'Alívio',
      'Aconchego',
      'Segurança',
      'Satisfação',
      'Afeto',
      'Equilíbrio',
      'Serenidade',
      'Acolhimento',
      'Disposição',
      'Otimismo',
      'Com foco'
    ]
  },
  {
    value: 'neutra',
    expressao: 'M26 36 Q31 45 36 36 M64 36 Q69 45 74 36 M34 63 Q50 66 66 63',
    label: 'Ok',
    icon: 'sentiment_neutral',
    cor: 'var(--primary-light)',
    detalhes: [
      'Indiferença',
      'Curiosidade',
      'Reflexão',
      'Tédio',
      'Distração',
      'Paciência',
      'Aceitação',
      'Incerteza',
      'Nostalgia',
      'Interesse',
      'Quietude',
      'Surpresa'
    ]
  },
  {
    value: 'cansada',
    expressao: 'M25 37 Q31 47 37 37 M63 37 Q69 47 75 37 M32 68 Q50 51 68 68',
    label: 'Cansada',
    icon: 'sentiment_dissatisfied',
    cor: 'var(--orange)',
    detalhes: [
      'Cansaço',
      'Desânimo',
      'Sobrecarga',
      'Preguiça',
      'Exaustão',
      'Inquietação',
      'Desconforto',
      'Frustração',
      'Impaciência',
      'Solidão',
      'Sensibilidade'
    ]
  },
  {
    value: 'dificil',
    expressao: 'M25 35 Q31 45 37 35 M63 35 Q69 45 75 35 M31 69 Q50 43 69 69 Z',
    label: 'Difícil',
    icon: 'sentiment_very_dissatisfied',
    cor: 'var(--pink)',
    detalhes: [
      'Tristeza',
      'Ansiedade',
      'Medo',
      'Raiva',
      'Culpa',
      'Mágoa',
      'Insegurança',
      'Angústia',
      'Decepção',
      'Vergonha',
      'Confusão',
      'Preocupação'
    ]
  },
] as const;

export type Emocao = (typeof EMOCOES)[number];
