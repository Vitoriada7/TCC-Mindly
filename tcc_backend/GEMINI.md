# IA no registro emocional

Execute o backend a partir de `tcc_backend`. A configuração lê `GEMINI_API_KEY` e,
opcionalmente, `GEMINI_MODEL` (padrão: `gemini-3.5-flash-lite`). No ambiente local,
`config/gemini.local.properties` contém a chave e é ignorado pelo Git. Esse arquivo
não é empacotado no JAR; em outros ambientes, configure a variável de ambiente.

Endpoints autenticados, todos POST:

- `/registros-emocionais/ia/transcricao`: multipart com campo `audio`, até 8 MB.
- `/registros-emocionais/ia/analise-voz`: mesmo upload; retorna `texto`,
  `analiseDisponivel` e até três `observacoesVocais` de até 200 caracteres.
  O Gemini recebe o áudio original para observar ritmo, pausas e expressão vocal.
- `/registros-emocionais/ia/perguntas`: JSON com `sentimento`,
  `sentimentoDetalhado` opcional e `pensamento`. Retorna `perguntas` com três textos.
- `/registros-emocionais/ia/apoio`: mesmo JSON, acrescido de `perguntas` e
  `respostas` opcionais. Retorna `texto` com até 1800 caracteres.

O navegador grava até dois minutos e envia o áudio somente ao encerrar a gravação.
Antes de gravar, a análise vocal pode ser ativada em uma opção desmarcada por padrão.
As observações são estimativas experimentais, sem scores clínicos, e só entram nas
perguntas e no apoio quando o usuário marca uma segunda confirmação. Os endpoints
de perguntas e apoio aceitam `observacoesVocais` opcionais (até três textos de 200
caracteres). O autorrelato tem prioridade. As observações não são persistidas
separadamente e não alteram o sentimento escolhido. Uma nova gravação limpa as
observações e sua confirmação. Se a análise falhar, o frontend tenta a transcrição
convencional; isso pode gerar uma segunda chamada à API. Se as observações forem
inválidas, a transcrição válida é preservada.
A transcrição pode ser revisada antes de avançar. O áudio não é persistido pelo
Mindly. O relato, as perguntas e respostas e o apoio final são salvos pelo endpoint
existente de registro emocional, com o campo opcional `complementoFinal` salvo separadamente em uma coluna TEXT.
Com `ddl-auto: update`, essa coluna é criada ao iniciar o backend. Perguntas e respostas são
armazenadas juntas em `exploracoes`; o apoio fica em `reflexao`.

As instruções orientam o modelo a evitar diagnósticos, perguntas invasivas e
prescrições, incentivar autocuidado e recomendar ajuda humana proporcional ao
contexto. Essas instruções não garantem avaliação clínica ou detecção de risco.
Antes de disponibilizar a jovens, revise exemplos de respostas com um profissional
qualificado, inclusive relatos de sofrimento intenso e risco imediato.

Timeout, recusa do modelo, erros externos e respostas inválidas retornam HTTP 503,
sem expor credenciais ou registrar o relato. O usuário pode tentar novamente ou
continuar sem IA. Erros de formato de áudio retornam HTTP 400.

Validação: `npm run build` no frontend e `mvn test` no backend. Para verificar
manualmente: grave e revise um relato, avance, responda nenhuma ou algumas
perguntas e confira o apoio final. Repita com IA indisponível para verificar que
o relato ainda pode ser salvo. O microfone exige localhost ou HTTPS e permissão.

Referências: [áudio](https://ai.google.dev/gemini-api/docs/generate-content/audio),
[GenerateContent](https://ai.google.dev/api/generate-content),
[modelos](https://ai.google.dev/gemini-api/docs/models).
