# Mindly — Relatório técnico de resultados e base para conclusão

## 1. Identificação e resumo executivo

**Data:** 16 de novembro de 2026, fuso America/Sao_Paulo. **Branch:** `mindly-v1.2`. **Commit de referência:** `e1f810d1d3a3c211b30d5cc92cd6036034117acd`. A análise abrange a árvore de trabalho disponível nesta data, incluindo alterações locais e arquivos ainda não versionados; portanto, não representa exclusivamente o conteúdo do commit. Não foram modificados componentes, regras de negócio, configurações ou dados para produzir este relatório.

**Fonte de escopo:** base documental incorporada em [gerar-relatorio-tcc.md](../../agents/skills/gerar-relatorio-tcc.md), extraída do artigo “Aplicativo para organização de tarefas e acompanhamento emocional”, versão identificada como `ModeloArtigo_TCC_IFSUL_SS__nosso_TCC_ (8).pdf`. O PDF original e uma versão acadêmica mais recente não foram encontrados entre os arquivos disponíveis. As páginas e seções acadêmicas citadas aqui são referências fornecidas pela skill, não conferência direta do PDF.

**Fontes do estado atual:** código Angular e Java, contratos HTTP, entidades, repositórios, migrações SQL, manifests, lockfile, documentação técnica e testes executados. Foram identificados dois módulos de aplicação, `tcc_frontend` e `tcc_backend`, com banco PostgreSQL e integrações externas. Não foi encontrado módulo Python no conjunto pesquisado.

| Conjunto original | Completamente entregue | Parcialmente entregue | Não entregue | Não verificável | Total |
| --- | ---: | ---: | ---: | ---: | ---: |
| Requisitos funcionais | 15 | 3 | 0 | 0 | 18 |
| Requisitos não funcionais | 0 | 4 | 0 | 1 | 5 |

Os RF completos representam **15/18 = 83,3%** da base original, segundo os critérios de implementação explicitados nas matrizes. Esse percentual não é cobertura de testes, aprovação operacional, eficácia em saúde ou percentual ponderado do produto. Requisitos parciais não receberam pontuação fracionária. O resultado histórico de 11 completos, 5 parciais e 2 não implementados, informado na seção 6 do artigo pela skill, permanece apenas como referência documental; não é possível atribuir a evolução a commits específicos sem uma análise histórica adicional.

O sistema apresenta fluxos conectados de cadastro, tarefas, agenda por prazo, hábitos, registro emocional, consulta de registros anteriores, visualização temporal do humor, práticas de autocuidado e apoio por IA. As lacunas funcionais principais são a manutenção restrita do perfil, as metas predominantemente predefinidas e a ausência de síntese de conquistas no dashboard. A busca de atendimento abre o Google Maps externamente. O recurso de voz realiza transcrição, sem evidência de inferência emocional acústica.

**Verificações executadas:** 21 testes Angular aprovados em 8 arquivos; build Angular de desenvolvimento aprovado; teste Node de configuração de API aprovado; Maven com 60 testes, dos quais 59 aprovados e 1 com erro de inicialização relacionado à conexão PostgreSQL. Não houve validação manual em navegador, teste ponta a ponta ou chamada deliberada às APIs de Gemini e Google Tasks.

## 2. Método e limites da avaliação

Foram lidas as instruções dos módulos, as rotas Angular, os componentes centrais e seus consumidores HTTP, controllers Java, serviços de domínio e integração, repositórios pertinentes, DTOs, mappers, entidades, configurações de segurança e migrações V1/V2. As buscas incluíram metas, mapas, integrações de voz e IA, referências a Python/HumeAI/SenseVoice e indícios de mocks, placeholders e TODOs. Mocks em testes foram tratados como instrumentos de verificação, não como entrega funcional de integrações reais.

Os critérios foram derivados dos RF/RNF originais e dos detalhamentos incorporados pela skill. “Completamente entregue” descreve a implementação conectada no escopo examinado e pode estar apoiado apenas em inspeção estática. “Parcialmente entregue” indica subcritérios essenciais ausentes ou incompletos. “Não entregue” exige evidência suficiente de ausência funcional. “Não verificável” indica insuficiência de evidência para decidir. A classificação não pressupõe disponibilidade de credenciais ou funcionamento dos provedores externos.

Os níveis de verificação usados são **E**: inspeção estática de implementação e conexão entre camadas; **T**: teste automatizado executado sobre o recorte indicado; **B**: build Angular de desenvolvimento executado. Não se atribui T a um fluxo inteiro porque outro serviço possui teste. Os testes unitários usam substitutos para dependências e não comprovam persistência real nem autorização HTTP completa.

Não foram criadas contas, publicados serviços ou realizadas migrações deliberadas. O teste Spring existente tentou inicializar o contexto com a configuração de banco disponível e falhou na conexão; não comprovou aplicação de migrações. Não foram lidos ou reproduzidos conteúdos de arquivos privados de credenciais, chaves ou backups. Este relatório omite identificadores de conta presentes na mensagem de erro do banco.

Não foram verificadas operação em produção, backup/restauração, acessibilidade por ferramenta, desempenho sob carga, compatibilidade real do microfone, usabilidade com estudantes ou efeitos sobre saúde e engajamento. A ausência de estudo com usuários impede inferir redução de ansiedade, estresse ou sobrecarga. A existência de prompts cautelosos não comprova segurança clínica das respostas geradas.

## 3. Inventário tecnológico

As versões resolvidas do frontend são as de `package-lock.json`; não foram confundidas com os intervalos declarados no manifesto. As versões Java/Spring são as declaradas e observadas na execução. Componentes gerenciados pelo parent Maven aparecem sem versão individual quando não extraída nesta análise.

| Tecnologia/ferramenta | Camada e finalidade | Versão declarada / resolvida ou observada | Evidência | Situação de uso |
| --- | --- | --- | --- | --- |
| Java | Backend, regras e cliente HTTP | 21 / execução 21.0.2 | `tcc_backend/pom.xml`; execução Maven | Confirmado |
| Spring Boot | Inicialização e configuração | 4.1.0 / 4.1.0 no teste | `pom.xml`; log de inicialização | Confirmado; contexto falhou no banco |
| Spring Web MVC | API REST e multipart | Gerenciada pelo Boot | `spring-boot-starter-webmvc`; controllers | Confirmado |
| Spring Data JPA e Hibernate | Persistência e consultas | Gerenciadas pelo Boot | Repositórios `JpaRepository`, entidades, `application.yaml` | Confirmado no código; banco não validado nesta execução |
| Spring Security e OAuth2 Resource Server | Autenticação Bearer/JWT | Gerenciadas pelo Boot | `SecurityConfig`, `AutenticacaoService` | Confirmado |
| JWT RS256, Nimbus JOSE e BCrypt | Assinatura de tokens e hash de senha | Bibliotecas gerenciadas; protocolos sem versão de pacote própria | `SecurityConfig`, `AutenticacaoService` | Confirmado; Nimbus chega via stack Spring |
| Jakarta Validation e Hibernate Validator | Validação de entrada | Gerenciada / Validator 9.1.0.Final no teste | DTOs, `@Valid`, testes de tema | Confirmado |
| Lombok | Geração de código Java | Gerenciada pelo parent | `pom.xml`, entidades e DTOs | Confirmado |
| PostgreSQL, SQL e driver JDBC | Banco relacional | Servidor não verificado; driver gerenciado | `application*.yaml`, migrações, entidades | Uso implementado; conexão do contexto falhou |
| Flyway e suporte PostgreSQL | Evolução de schema | Gerenciados pelo Boot | `pom.xml`; `db/migration/V1__schema_inicial.sql`, V2 | Configurado; execução bem-sucedida não comprovada aqui |
| Maven | Build e testes Java | Execução local 3.9.12; Docker declara linha 3.9 | Executável disponível; `pom.xml`, `Dockerfile` | Confirmado; wrapper falhou antes de iniciar Maven |
| JUnit Jupiter, Mockito e AssertJ | Testes backend | Gerenciados pelos starters de teste | `src/test/java`, Surefire | Confirmado na execução; não escolhas independentes com versão explícita |
| Angular common/compiler/core/forms/platform-browser/router | Interface, formulários e navegação | `^21.1.0` / 21.2.18 | `package.json`, lockfile, rotas e componentes | Confirmado |
| Angular compiler-cli | Compilação Angular | `^21.1.0` / 21.2.18 | Manifest e lockfile | Confirmado |
| Angular CLI e Angular Build | Build e executor de testes | `^21.1.3` / 21.2.19 | `angular.json`; comandos executados | Confirmado |
| TypeScript | Código frontend | `~5.9.2` / 5.9.3 | Manifest, lockfile e arquivos `.ts` | Confirmado |
| HTML e CSS | Templates, estilos e responsividade | Sem versão de pacote | Componentes e `src/styles` | Confirmado; qualidade visual não medida |
| RxJS | Comunicação e composição assíncrona | `~7.8.0` / 7.8.2 | Serviços HTTP; `forkJoin`, `finalize`, `switchMap` | Confirmado |
| tslib | Helpers TypeScript | `^2.3.0` / 2.8.1 | Manifest e lockfile | Dependência direta de suporte à compilação |
| Leaflet | Estilos de mapa | `^1.9.4` / 1.9.4 | Import CSS em `src/styles.css`; sem consumidor JS encontrado | Uso residual do CSS; não comprova mapa interativo |
| Tipos Leaflet | Tipagem de desenvolvimento | `^1.9.21` / 1.9.21 | Manifest e lockfile | Declarados; sem implementação de mapa encontrada |
| Vitest e jsdom | Testes frontend e DOM simulado | `^4.0.8` / 4.1.10; `^27.1.0` / 27.4.0 | Manifest, lockfile, execução Angular | Confirmado |
| Node.js e npm | Toolchain frontend | Node observado 20.20.0; npm declarado 10.9.3 | Execução Node; `packageManager` | Node confirmado; versão efetiva do npm não aferida |
| Node Test Runner | Teste de configuração de API | Integrado ao Node | `scripts/configure-api.test.mjs` | Confirmado; 1 subteste aprovado |
| Google Fonts e Material Symbols Rounded | Tipografia e ícones | Sem versão fixada | `src/styles.css`, `_tokens.css`, templates | Integração declarada no CSS; carga remota não observada |
| MediaRecorder e getUserMedia | Captação de áudio no navegador | APIs da plataforma, sem pacote | `RegistroDiarioComponent.alternarGravacao` | Implementação confirmada; microfone não exercitado |
| Gemini API | Perguntas, mensagem contextual e transcrição | API `v1beta`; modelos configurados `gemini-3.5-flash-lite` e fallback `gemini-3.1-flash-lite` | `GeminiService`, `ApoioEmocionalService`, YAML | Cliente implementado e testado com simulação; disponibilidade e acesso aos modelos não consultados |
| Java HttpClient e Jackson | HTTP externo e JSON | HttpClient do JDK 21; Jackson gerenciado | `GeminiService`, `GoogleTasksClient` | Confirmado; Jackson integra a stack, sem dependência direta adicional no POM |
| Google Tasks API e OAuth2 | Exportação/sincronização manual de tarefas | Serviço remoto sem versão de produto fixada; caminho da API no cliente | `GoogleTasksClient`, `GoogleTasksService`, testes | Backend implementado; componente frontend sem consumidor encontrado |
| AES-GCM e SHA-256 | Cifra de refresh token e proteção do estado OAuth | AES de 256 bits, GCM com tag de 128 bits | `GoogleTokenCipher`; `GoogleTasksService.hash` | Confirmado; não equivale a cifrar os relatos emocionais |
| Google Maps | Pesquisa externa de apoio próximo | URL de pesquisa, sem SDK | `apoio.component.html` | Link implementado; sem mapa incorporado ou consulta local de estabelecimentos |
| HTTP/JSON e TCP/IP | Comunicação cliente-servidor e integrações | Não versionados no escopo | `HttpClient` Angular/Java; endpoints REST/JDBC | HTTP/JSON confirmados no código; TCP/IP é infraestrutura subjacente, não medição de rede |
| Docker e Eclipse Temurin | Empacotamento e runtime backend | Imagens Java 21; runtime Jammy | `tcc_backend/Dockerfile` | Configuração existente; imagem não construída nesta análise |
| Vercel, Render e Supabase | Hospedagem proposta de frontend, API e banco | Sem versão fixada | `DEPLOY.md`, `vercel.json`, configuração de produção | Documentados/configurados; operação remota não verificada |
| Git e GitHub | Versionamento e hospedagem de código | Git sem versão aferida | Branch/HEAD locais; declaração documental | Git confirmado; hospedagem remota não validada |
| Draw.io, Excalidraw, Figma e Canva | Planejamento e design | Não informadas | Base acadêmica incorporada | Somente declarados; sem artefatos específicos encontrados |
| IntelliJ IDEA Community, VS Code e Postman | Desenvolvimento e validação | Não informadas | Base acadêmica; contexto IDE fornecido pelo usuário | Declarados; não comprovam execução de testes de API ou projeto de interface |
| Python, HumeAI e SenseVoice | Serviços auxiliares previstos | Não verificadas | Base acadêmica; busca de módulos e referências atuais | Previstos no documento; não encontrados como integrações ativas no código analisado |

A arquitetura observada é Angular → API Spring em camadas → PostgreSQL, com chamadas externas feitas pelo Java. A execução atual não depende de microserviço Python identificado. HumeAI aparece na visão geral documental e SenseVoice na descrição do servidor segundo a skill; nenhum dos dois foi confirmado no código atual. Gemini ocupa as funções observadas de transcrição e geração contextual, mas não foi encontrada decisão formal que documente a substituição desses serviços. Não se afirma coexistência operacional nem equivalência entre transcrição e reconhecimento de emoções na voz.

O uso de Flyway com `ddl-auto: validate` e o CORS por origens explícitas divergem de trechos antigos de `tcc_backend/AGENTS.md`, que ainda descrevem `update`, CORS permissivo e apenas teste de contexto. `GOOGLE_TASKS.md` também contém orientação antiga sobre `ddl-auto: update`. Para o estado técnico prevaleceram as configurações e os testes atuais. A lista completa de dependências transitivas permanece no lockfile e no gerenciamento Maven; não é apresentada como conjunto de decisões tecnológicas diretas.

## 4. Matriz dos requisitos funcionais

As referências E01–E14 são detalhadas na seção 12, com caminhos e símbolos. O texto original foi preservado. A interpretação de “manter perfil” inclui os dados cadastrais consultáveis, enquanto “definir metas” exige uma escolha explícita do usuário; ambos os pontos podem ser refinados pelos autores mediante delimitação formal de escopo.

### RF01–RF06 — Cadastro e organização

| ID e texto original | Critérios avaliados | Status | Implementado | Pendente | Evidências | Nível de verificação |
| --- | --- | --- | --- | --- | --- | --- |
| RF01 — Permitir cadastrar usuario no sistema. | Formulário, validação, API e persistência do usuário | Completamente entregue | Cadastro Angular envia nome, apelido, e-mail, senha e nascimento; API valida DTO; serviço verifica duplicidade e aplica BCrypt antes de salvar | Exercitar cadastro real e erros HTTP; não é lacuna de conexão detectada | E01; `POST /autenticacao/cadastro`; V1 `usuario` | E; B do frontend, sem cadastro ponta a ponta |
| RF02 — Permitir manter os dados do perfil do usuario. | Consultar e alterar dados do perfil autenticado | Parcialmente entregue | Consulta perfil, altera apelido, avatar autorizado e preferência de tema; estado sincronizado no frontend | Nome, e-mail e nascimento não possuem edição no fluxo examinado; senha e exclusão de conta também não têm fluxo encontrado, sem tratá-las como obrigações adicionais do RF | E02; `GET/PUT /usuarios/me`; `UsuarioUpdateRequest` | E; T em perfil, tema, avatar e serviço Angular; B |
| RF03 — Permitir cadastrar, editar, excluir e concluir tarefas academicas e pessoais. | Quatro operações, categoria, prazo, proprietário e banco | Completamente entregue | Formulários e serviços conectados; tarefas categorizadas, com descrição, prioridade e prazo; propriedade validada no backend | Verificação real do CRUD; a categoria obrigatória requer configuração inicial pelo usuário | E03; `POST/PUT/DELETE /tarefas`, `PATCH /tarefas/{id}/concluir`; V1 | E; B; sem teste específico executado do CRUD de tarefas |
| RF04 — Permitir visualizar a lista de tarefas cadastradas. | Consulta da lista do próprio usuário e apresentação | Completamente entregue | Lista API/Angular, filtros por status, prioridade, categoria e vencimento, estados de carregamento e erro | Validar visualmente filtros e volume de dados | E03; `TarefaService.listar`; `TarefasComponent.carregarTarefas` | E; B |
| RF05 — Permitir organizar tarefas em calendario ou agenda. | Associar prazo e selecionar dia para consultar tarefas | Completamente entregue | Data/hora persistida; faixa de dias filtra tarefas por prazo e amplia o intervalo futuro | Calendário começa no dia de abertura; sem navegação passada identificada. Essa limitação não impede o critério mínimo de agenda e não foi convertida em exigência não documentada | E03; `diasCalendario`, `selecionarDia`, `tarefasFiltradas`; `data_limite` | E; B |
| RF06 — Permitir cadastrar, editar e excluir habitos. | CRUD com interface, API, regra e persistência | Completamente entregue | Formulário, listagem, criação, edição e exclusão; serviço valida proprietário e remove registros vinculados na exclusão | Validar persistência real e concorrência | E04; `/habitos`; V1 `habitos` e `registros_habitos` | E; B; testes de hábito executados cobrem marcação, não todo o CRUD |

### RF07–RF12 — Hábitos, emoções e autocuidado

| ID e texto original | Critérios avaliados | Status | Implementado | Pendente | Evidências | Nível de verificação |
| --- | --- | --- | --- | --- | --- | --- |
| RF07 — Permitir registrar a realizacao diaria de habitos e acompanhar os dias consecutivos de realizacao. | Marcar/desmarcar hoje, consultar dias concluídos e sequência | Completamente entregue | Registros por hábito/data, unicidade no schema, semana visível e sequência agregada; gamificação calcula sequência atual e melhor sequência | Harmonizar cálculo: `HabitoService` exige atividade hoje para iniciar sequência; `GamificacaoService` também aceita ontem. Não há sequência individual por hábito identificada; o RF original não explicita essa granularidade | E04, E09; `/habitos/{id}/registros/{data}`, `/habitos/resumo` | E; T de bloqueio de datas, desmarcação e gamificação; B |
| RF08 — Permitir registrar diariamente o estado emocional do usuario. | Escolher emoção, registrar conteúdo e vincular ao dia/usuário | Completamente entregue | Fluxo guiado salva sentimento, detalhe, relato, explorações, reflexão e complemento; serviço cria ou atualiza o registro do dia | Datas dependem do relógio local do servidor neste serviço; não houve teste em virada de dia/fusos | E05; `POST /registros-emocionais`; V1 com unicidade usuário/data | E; T de criação e atualização diária; B |
| RF09 — Permitir visualizar o historico dos registros emocionais. | Consultar dias anteriores e reler conteúdo, além de marcações | Completamente entregue | API retorna registros do usuário; calendário navega entre meses; clique abre diálogo com emoção, relato, perguntas/respostas, mensagem e complemento | Paginação e tratamento mais visível de erro de carregamento são melhorias; não impedem o fluxo identificado | E06; `EmocionalComponent.abrirRegistro`; `RegistroDetalhesComponent` | E; T do componente emocional e mapper; B; sem consulta real ao banco |
| RF10 — Permitir apresentar graficos e indicadores relacionados ao estado emocional do usuario. | Evolução ao longo do tempo e indicadores baseados nos registros | Completamente entregue | Dashboard apresenta posições de humor por dia, períodos semanal/mensal, navegação temporal e dias com registro; lacunas não recebem humor fictício | Validar leitura visual; escala categórica não é medida clínica nem intensidade validada | E07; `diasEmocionais`, `intervalo`, `diasComRegistro`; template do gráfico | E; B; sem teste específico do cálculo do dashboard |
| RF11 — Permitir recomendar atividades de autocuidado. | Sugerir cuidado ao usuário; diferenciar recomendação de catálogo | Completamente entregue | Serviço de apoio pede cuidado simples adequado ao relato e respostas; interface recebe mensagem contextual; catálogo oferece práticas complementares | Avaliar pertinência das respostas reais e eventual recomendação longitudinal; não foi encontrado recomendador automático de sobrecarga de tarefas | E08; `ApoioEmocionalService.apoio`; `/registros-emocionais/ia/apoio` | E; T com respostas simuladas; B; provedor não acionado |
| RF12 — Permitir disponibilizar exercicios de relaxamento e mindfulness. | Consultar instruções de práticas executáveis pelo usuário | Completamente entregue | Seis práticas em quatro categorias, com passos e duração indicativa, incluindo respiração, aterramento e caminhada consciente | Não há sessão audiovisual ou temporizador observado; não exigidos pelo texto original | E10; `RecursosComponent.recursos` e template | E; B; funcionalidade legitimamente local ao frontend |

### RF13–RF18 — Motivação, metas, IA e painel

| ID e texto original | Critérios avaliados | Status | Implementado | Pendente | Evidências | Nível de verificação |
| --- | --- | --- | --- | --- | --- | --- |
| RF13 — Permitir exibir feedbacks visuais e mensagens motivacionais. | Resposta visual às ações, progresso e mensagens de incentivo | Completamente entregue | Toast de registro, progressos e conquistas, estados de sucesso/erro e mensagem motivacional no dashboard | Avaliar compreensão e efeito motivacional com participantes | E06, E07, E09; `mostrarToast`, trilha e template de conquistas | E; T parcial de componentes e gamificação; B |
| RF14 — Permitir definir metas relacionadas a organizacao e ao bem-estar. | Usuário definir objetivo e acompanhar progresso | Parcialmente entregue | Metas de conquistas e de cinco tarefas semanais predefinidas; quantidade de hábitos ativos compõe meta diária | Não encontrados formulário, endpoint ou entidade para configurar meta própria, prazo ou valor-alvo; criar tarefa/hábito não equivale automaticamente a definir uma meta | E09; `META_TAREFAS_SEMANAL`; busca em rotas, DTOs, entidades, serviços e templates | E; T de metas fixas na gamificação; B |
| RF15 — Permitir consultar conteudos de apoio sobre saude mental. | Acessar conteúdo textual de apoio/autocuidado | Completamente entregue | Página de recursos com explicações e passos para autocuidado e organização; página de apoio orienta acesso a serviços | Conteúdo breve e estático; sem biblioteca editorial extensa ou curadoria científica documentada. Classificação considera que práticas textuais constituem conteúdo de apoio; rever se o artigo exigir material psicoeducativo separado | E10, E11; `/recursos` e `/apoio` | E; B |
| RF16 — Permitir utilizar recursos de Inteligencia Artificial como apoio ao usuario. | Orientações, recomendações e feedback contextual com dados informados | Completamente entregue | Relato, sentimento, detalhe e respostas informam três perguntas e mensagem final personalizada; persistência permite releitura; há opção de continuar sem IA | IA não consulta automaticamente histórico, tarefas ou hábitos; não foi exigida essa personalização longitudinal para considerar atendido o texto original. Validar provedor e qualidade das respostas | E08, E05; `contextoIa`, `gerarPerguntas`, `gerarApoio`, `GeminiService` | E; T de Gemini e apoio com simulação; B |
| RF17 — Permitir registrar emocoes por meio da voz. | Gravar fala, transformar em relato revisável e salvar no registro | Completamente entregue | Microfone via MediaRecorder, limite de tempo, envio multipart, validação de MIME/tamanho, transcrição Gemini incorporada ao texto editável e persistência junto ao sentimento escolhido | Permissões, formatos e captura real não testados; não se identifica emoção pela entonação nem se armazena áudio como parte do diário | E08; `alternarGravacao`; `/registros-emocionais/ia/transcricao`; `salvar` via `avancar` | E; T de validação/contratos simulados; B; sem teste real de áudio |
| RF18 — Permitir visualizar um dashboard com o resumo das tarefas e do estado emocional. | Resumir tarefas pendentes, hábitos, metas, conquistas e emoção conforme detalhamento documental | Parcialmente entregue | Tarefas do dia/atrasadas, conclusão e criação, humor atual, gráfico, resumo de hábitos, sequência e meta semanal de tarefas | Não encontrada síntese explícita de conquistas no template do dashboard, embora o serviço carregue esse conjunto; conquistas são apresentadas em página própria. Metas configuráveis também permanecem ausentes | E07, E09; `DashboardComponent.carregar`, template e `ResumoGamificacaoResponse` | E; B; sem teste ponta a ponta |

**Fechamento:** RF completos: 01, 03, 04, 05, 06, 07, 08, 09, 10, 11, 12, 13, 15, 16 e 17; parciais: 02, 14 e 18. Total 15 + 3 + 0 + 0 = 18. Nenhum RF foi marcado não verificável porque a inspeção sustenta uma decisão de implementação; a execução externa continua explicitamente não verificada. Essa distinção é essencial para interpretar RF11, RF16 e RF17.

## 5. Matriz dos requisitos não funcionais

Não existem metas quantitativas aprovadas na base incorporada. Foram usados critérios técnicos observáveis, sem estabelecer SLA, latência ou nota arbitrária. A existência de um mecanismo não comprova a garantia ampla descrita pelo requisito.

| ID e texto original | Critérios avaliados | Status | Implementado | Pendente e limites de comprovação | Evidências | Nível de verificação |
| --- | --- | --- | --- | --- | --- | --- |
| RNF01 — Garantir disponibilidade do sistema. | Inicialização, observação operacional e continuidade diante de falhas | Não verificável | Dockerfile, configuração de hospedagem, timeout e fallback Gemini demonstram preparação técnica | Sem monitoramento, janela de observação ou teste de recuperação; contexto falhou no banco configurado. Isso não prova indisponibilidade permanente ou ausência de entrega em produção | E12, E14; `DEPLOY.md`, `GeminiService.enviarComRetentativas` | E; T do contexto com erro e de falhas simuladas; sem medição operacional |
| RNF02 — Garantir armazenamento seguro dos dados do usuario. | Persistência íntegra, proteção de senhas/tokens, proteção operacional do banco | Parcialmente entregue | BCrypt, entidades/constraints, migrações, configuração externa, AES-GCM para refresh token Google; SQL não exibido por padrão | Não comprovados cifra em repouso do banco/backups, restauração, permissões efetivas e configuração TLS implantada. Relatos são TEXT sem cifra de aplicação; isso isoladamente não determina insegurança do armazenamento, mas limita a garantia. `DEPLOY.md` pede troca de chave JWT anteriormente versionada | E01, E12, E13; V1/V2, `GoogleTokenCipher` | E; T de cifra; conexão real falhou |
| RNF03 — Garantir confidencialidade dos dados emocionais. | Autorização, isolamento por usuário e exposição controlada | Parcialmente entregue | Usuário obtido do JWT; consultas emocionais filtradas por usuário; DTOs sem senha; logs do serviço Gemini evitam imprimir relatos/áudio; envio para IA limitado ao contexto do formulário | Não executado teste HTTP entre duas contas; não auditados banco, logs de infraestrutura e política do provedor. Conteúdo/áudio sai para serviço externo quando o recurso é usado; não foram comprovadas retenção e governança desse tratamento | E05, E06, E08, E12; `UsuarioAutenticadoService`, `RegistroEmocionalRepository` | E; T unitário do registro e mapper; sem teste de isolamento ponta a ponta |
| RNF04 — Garantir autenticacao segura dos usuarios. | Hash de senha, assinatura/expiração, controle de rotas, transporte e ciclo de sessão | Parcialmente entregue | BCrypt, JWT RS256 de duas horas, validação pelo Resource Server, rotas privadas autenticadas, CORS explícito, interceptor limita envio do token à API e trata expiração/401 | Token em localStorage exige avaliar exposição a XSS; não encontrados controles de tentativas, revogação de JWT ou testes HTTP de autenticação. HTTPS e renovação de chaves implantados não comprovados. Regra pública `/**/publico` é ampla e deve ser revisada em futuras rotas | E01, E02, E12; `SecurityConfig`, `AutenticacaoService`, `TokenService`, interceptor | E; T do interceptor e CORS; sem login real |
| RNF05 — Garantir facilidade de manutencao do sistema. | Separação de responsabilidades, contratos, migrações, testes e consistência | Parcialmente entregue | Camadas controller/service/repository, requests/responses/mappers; features Angular com lazy loading; TypeScript; testes executáveis e migrations versionadas | Documentação técnica divergente; Google Tasks sem consumidor frontend; critérios de streak/fusos inconsistentes; contexto depende de banco configurado; não encontrada comprovação de pipeline obrigatório. Não houve medição de esforço de manutenção | E03–E14; manifests, instruções dos módulos e suíte executada | E; T e B com os limites registrados |

**Fechamento:** 0 completos + 4 parciais + 0 não entregues + 1 não verificável = 5. Zero RNF completos não significa ausência de mecanismos de qualidade: indica que as garantias amplas não foram demonstradas integralmente.

## 6. Promessas narrativas, extras e alterações de escopo

### 6.1 Capacidades previstas fora dos RF

| Item narrativo da base | Situação observada | Evidência e diferença de escopo |
| --- | --- | --- |
| Centros psicológicos próximos com Google Maps | Parcialmente entregue | `apoio.component.html` abre pesquisa externa. Não há mapa incorporado, geolocalização própria ou lista local de centros, distâncias e horários. Leaflet CSS não supre esse fluxo |
| Sugestões de controle de estresse diante de sobrecarga | Parcialmente entregue | Mensagem Gemini pode responder a sobrecarga relatada; recursos incluem organização e relaxamento. Não foi encontrado detector baseado em quantidade/prazo de tarefas ou correlação longitudinal |
| Apoio discreto e acessível | Parcialmente verificável | Navegação web, elementos responsivos, conteúdo breve e diálogos existem; não foram executadas auditorias AXE/WCAG ou pesquisa sobre privacidade percebida e facilidade de uso |
| Gerenciamento de conta | Parcialmente entregue | Cadastro, login, logout, consulta e personalização presentes; manutenção de cadastro restrita ao apelido e preferências |
| Assistência inteligente | Implementada no recorte do diário | Perguntas e mensagem com base no relato atual; não foi encontrado chat geral independente ou acompanhamento clínico |
| Gamificação e conquistas | Implementadas | `GamificacaoService` gera 13 conquistas com estado/progresso e sequências; frontend apresenta conquistas e trilhas. Já previstas na base, portanto não são extras automaticamente |
| Painel de acompanhamento | Parcialmente entregue | RF18; ausência de síntese de conquistas no painel, apesar de página própria |

### 6.2 Extras funcionais e candidatos incompletos

| Capacidade | Classificação | Evidência, benefício potencial e limite |
| --- | --- | --- |
| Contatos pessoais de emergência com principal e ligação | Extra funcional em relação à base incorporada, sujeito à conferência do artigo integral | CRUD em `ApoioComponent`/`ContatoEmergenciaService`, `/contatos-emergencia`, tabela própria; limite de dez contatos e propriedade verificada; links de ligação. Pode facilitar acesso a pessoas de confiança; dois testes do serviço passaram. A base incorporada não especifica esse cadastro, embora mapas e apoio sejam previstos |
| Exportação manual para Google Tasks | Candidato a extra, parcialmente implementado; não contabilizado como extra entregue de ponta a ponta | Backend e seis testes existem; OAuth state expira e é consumido, tokens são cifrados, tarefas locais são enviadas a lista remota. `GoogleTasksComponent` tem interface/callback, mas busca por classe/seletor só encontrou os próprios arquivos, sem importação/uso em rotas/templates consumidores; `TarefasComponent` não o importa. Não há importação das alterações remotas |
| Avatares liberados por conquistas | Desdobramento da gamificação/perfil | `AvatarServiceTests`, `UsuarioService.atualizarAvatar`, biblioteca Angular e tabela `usuario_avatar`; quatro testes de avatar passaram. Não tratado como extra independente sem delimitação acadêmica adicional |
| Preferência de tema persistida | Aprimoramento de perfil/usabilidade | `TemaService`, `UsuarioService.atualizarTema`, coluna `tema_escuro`; testes backend/frontend aprovados. Não é novo RF oficial |
| Converter prática em novo hábito | Integração entre RF06 e RF12 | Link `/habitos` com query params preenche formulário; `HabitosComponent` recebe parâmetros e exige salvar. Não é criação automática sem confirmação de formulário |

### 6.3 Melhorias técnicas e substituições

Flyway, validação de schema, CORS por origem, Docker com usuário sem privilégios, índice de proprietário, proteção AES-GCM de refresh token e fallback Gemini são melhorias técnicas. Não são novos requisitos funcionais. O controle de versão otimista da conexão Google aparece na migração V2. A política de fallback possui prazo total e limite compartilhado de tentativas; não garante resposta quando os dois modelos falham.

A passagem do desenho com serviços Python/HumeAI/SenseVoice para cliente Java/Gemini é uma divergência entre proposta e implementação observada. Deve ser formalizada no artigo se essa for a decisão final. A infraestrutura também precisa de atualização coerente: `DEPLOY.md` descreve frontend Vercel, enquanto `GOOGLE_TASKS.md` apresenta exemplo de frontend no Render. Esses textos não comprovam onde a versão atual está publicada.

## 7. Atendimento aos objetivos

OE01–OE05 são identificadores locais extraídos pela skill do texto corrido do artigo; não são IDs oficiais criados pelo documento original.

| Objetivo | Resultados relacionados | Evidências | Grau de atendimento | Limitações |
| --- | --- | --- | --- | --- |
| Geral — Desenvolver aplicação que integre organização e monitoramento emocional, visando bem-estar, identificação de sobrecarga e incentivo a apoio | Tarefas, hábitos e diário reunidos; histórico, gráfico, práticas e acesso a apoio | E03–E11; matrizes RF | Parcial: integração técnica substancialmente atendida | Identificação automática de sobrecarga e transtornos não demonstrada; contribuição ao bem-estar não foi medida |
| OE01 — Investigar estudos sobre saúde mental dos estudantes | Motivação e conceitos constam da base documental | Skill, introdução e referencial nela descritos | Não verificável como execução de pesquisa | Sem PDF integral, fichamentos, protocolo, bibliografia completa ou artefatos de pesquisa disponíveis |
| OE02 — Analisar funcionalidades e limitações de soluções existentes | Há escolha de funcionalidades compatíveis com o tema | Base incorporada; módulos implementados | Não verificável como análise comparativa | Não foram encontrados quadro comparativo ou registros de avaliação dos aplicativos concorrentes |
| OE03 — Identificar design e estratégias motivacionais | Trilhas, conquistas, avatares, mensagens, identidade visual e tema | E09; componentes, tokens e instruções frontend | Parcialmente evidenciado | Código comprova aplicação de estratégias, não pesquisa que as fundamentou ou eficácia de engajamento |
| OE04 — Projetar estrutura considerando usabilidade e apoio emocional | Features, rotas, formulários e camadas; práticas e acesso a apoio | E01–E12; arquitetura observada | Parcialmente atendido | Sem artefatos de design e teste de usabilidade/acessibilidade que confirmem a qualidade pretendida |
| OE05 — Desenvolver protótipo funcional com recursos definidos | Fluxos de 15 RF completos em inspeção; build e testes executados | Matrizes e registro de comandos | Substancialmente atendido, com lacunas | Três RF parciais, integração extra desconectada e ausência de validação ponta a ponta com banco/provedores |

## 8. Discussão dos resultados por eixo

### Organização de tarefas e hábitos

As operações de tarefas e o acompanhamento semanal de hábitos concretizam recursos de organização pessoal e autorregulação. A definição de prioridades e prazos pode apoiar o planejamento da rotina, mas a análise não mediu produtividade. A sequência de hábitos representa dias com realização registrada, sem comprovar formação de hábito ou aderência sustentada. A divergência entre duas formas de sequência pode gerar números diferentes no painel e nas trilhas, sobretudo antes da realização do primeiro hábito do dia. Metas fixas fornecem referência de progresso, mas limitam adequação da experiência à capacidade individual.

### Registro e evolução emocional

O diário permite registrar e reler experiências e observar categorias de humor em períodos distintos. Diferentemente de mera marcação no calendário, o histórico abre o conteúdo de cada dia. Há um registro por usuário/data; uma nova gravação no mesmo dia atualiza o existente, o que precisa ser comunicado na descrição acadêmica. Os gráficos representam categorias autodeclaradas, sem instrumento psicométrico ou inferência diagnóstica. A ausência de teste do banco e de estudo longitudinal impede afirmar confiabilidade operacional integral e benefícios emocionais decorrentes do uso.

### Autocuidado e acesso a apoio

O catálogo de seis práticas e as recomendações contextuais do diário oferecem caminhos complementares de autocuidado. As instruções estáticas são capacidades funcionais legítimas do frontend; não devem ser descartadas como mock apenas por não possuírem banco. A busca externa no Maps facilita iniciar a procura de atendimento, mas não constitui diretório próprio ou confirmação de disponibilidade de profissionais. A qualidade e adequação dos conteúdos precisam de revisão especializada e validação com o público-alvo.

### Gamificação e engajamento

As 13 conquistas ligam tarefas, hábitos e registros emocionais a progressos verificáveis por regras de código. Os avatares desbloqueados permanecem no conjunto do usuário e podem ser selecionados mediante autorização. Esses mecanismos materializam estratégias motivacionais da proposta; o fato de existirem não demonstra aumento de engajamento. A avaliação futura deve observar se metas predefinidas e sequências estimulam continuidade sem produzir cobrança excessiva.

### Voz e inteligência artificial

A gravação oferece alternativa à digitação: a fala é transcrita, revisada e salva como relato textual, junto ao sentimento escolhido pelo usuário. Não há evidência de análise prosódica, reconhecimento de emoção pela voz ou execução de SenseVoice/HumeAI. A geração de perguntas e mensagem final usa o contexto do registro atual. Os testes de Gemini verificam respostas, erros, limites e fallback com dependências simuladas; não validam modelos remotos, custo, acurácia de transcrição ou adequação clínica. A possibilidade de continuar sem IA reduz a dependência do provedor para concluir o diário textual.

### Arquitetura, qualidade e segurança

A separação entre interface, API e persistência favorece a localização de responsabilidades. Migrações e constraints tornam explícito parte do modelo de dados, enquanto testes executados oferecem evidência sobre regras específicas. Entretanto, a falha do contexto mostra dependência do ambiente de banco, e a desconexão do componente Google Tasks revela diferença entre código isolado e funcionalidade acessível. Autenticação, filtros por proprietário e logs restritos são mecanismos relevantes; sua presença não substitui testes de autorização, revisão de infraestrutura e demonstração das garantias não funcionais.

## 9. Base textual para a seção de resultados

A implementação analisada do Mindly integra organização de tarefas, acompanhamento de hábitos e registro emocional em uma aplicação web com frontend Angular e backend Java/Spring Boot. A persistência foi modelada em PostgreSQL, com migrações versionadas pelo Flyway. A inspeção das interfaces, contratos HTTP e serviços identificou fluxos conectados para criação, edição, exclusão e conclusão de tarefas, bem como para associação de prazos e consulta por dia. O módulo de hábitos permite registrar a realização no dia corrente e apresentar dias concluídos e indicadores de sequência.

No acompanhamento emocional, o protótipo oferece registro diário com seleção de sentimento, relato e campos de reflexão. Os registros anteriores podem ser consultados por calendário, com abertura de seus conteúdos em diálogo próprio. O dashboard apresenta categorias de humor ao longo de períodos semanais e mensais, preservando os dias sem registro como ausência de informação. Esses resultados evidenciam a implementação técnica do registro e da visualização temporal, sem constituir avaliação psicológica ou comprovação de melhoria no bem-estar dos participantes.

A assistência por inteligência artificial foi implementada por integração com Gemini, responsável pela elaboração de perguntas relacionadas ao relato, pela geração de mensagem de apoio contextual e pela transcrição de fala. A captura de áudio ocorre no navegador e o texto transcrito pode ser revisado antes de compor o diário. A solução observada diverge da previsão documental de serviços Python, HumeAI e SenseVoice, não identificados como dependências ativas. A implementação atual não permite afirmar que o sistema reconhece emoções pela voz, pois o sentimento é selecionado pelo próprio usuário e a função de áudio é a transcrição do conteúdo falado.

A avaliação da base original classificou 15 dos 18 requisitos funcionais como completamente entregues no escopo de implementação, correspondendo a 83,3%, e três como parcialmente entregues. As lacunas concentram-se na edição dos dados de perfil, na definição de metas pelo usuário e na composição integral do dashboard conforme seu detalhamento documental. A consulta de apoio próximo ocorre por pesquisa externa no Google Maps, e não por mapa incorporado. Como capacidade complementar à base incorporada, observou-se cadastro de contatos pessoais de emergência com acesso à ligação. A integração Google Tasks permanece incompleta do ponto de vista da navegação atual, apesar de possuir backend e testes específicos.

Na verificação automatizada, os 21 testes frontend foram aprovados e o build de desenvolvimento Angular foi concluído. O teste Node da configuração de API também foi aprovado. No backend, foram executados 60 testes: 59 passaram e um apresentou erro ao carregar o contexto por falha de conexão com o PostgreSQL configurado. Dessa forma, os resultados sustentam a existência das implementações e a correção dos recortes cobertos pelos testes, mas não comprovam a operação integral da aplicação com banco e provedores externos. Os requisitos não funcionais foram avaliados como quatro parcialmente entregues e um não verificável, em razão da diferença entre mecanismos implementados e garantias demonstradas.

### Figuras sugeridas, ainda a produzir

1. Lista de tarefas com categorias, prazos e seleção de dia, usando conta e dados fictícios.
2. Semana de hábitos com marcação do dia e sequência, após esclarecer o critério de cálculo.
3. Registro emocional guiado e consulta posterior do mesmo relato no calendário.
4. Gráfico semanal e mensal do dashboard, incluindo um dia sem registro.
5. Práticas de autocuidado e abertura do formulário de novo hábito preenchido pela prática.
6. Conquistas e biblioteca de avatares com um exemplo bloqueado e outro liberado.
7. Página de apoio mostrando a pesquisa externa e contatos fictícios, sem dados pessoais reais.
8. Diagrama da arquitetura efetiva Angular/Spring/PostgreSQL/Gemini; Google Tasks deve aparecer como integração parcial até reconectar a interface.

As capturas e o diagrama não foram produzidos nesta avaliação. Capturas de mensagens de IA devem identificar a origem automática e não ser apresentadas como evidência de qualidade clínica ou depoimento de usuário.

## 10. Base textual para a conclusão

O desenvolvimento do Mindly resultou em um protótipo web que reúne recursos de organização pessoal e acompanhamento emocional voltados ao contexto de jovens estudantes. A integração entre tarefas, hábitos, diário, histórico e visualização temporal demonstra a viabilidade técnica de concentrar essas funções em uma mesma aplicação. A implementação também disponibiliza práticas de autocuidado, estratégias de gamificação e apoio contextual por inteligência artificial, mantendo a possibilidade de concluir o registro textual quando a IA não estiver disponível.

Em relação ao problema de pesquisa, os resultados mostram como uma aplicação pode oferecer instrumentos de planejamento, auto-observação e acesso a apoio, mas não permitem concluir que seu uso melhora o bem-estar psicológico. Não foram apresentados nesta avaliação dados de intervenção com estudantes, medidas de sobrecarga, instrumentos validados de saúde mental ou comparação antes e depois do uso. A identificação de possíveis transtornos emocionais e a eficácia das recomendações também não foram demonstradas. Assim, a contribuição constatada é tecnológica e corresponde à construção e avaliação técnica parcial do protótipo, com potencial de apoio complementar ao cuidado profissional.

O atendimento ao objetivo de desenvolvimento foi substancial, com 15 requisitos funcionais completos em inspeção de implementação e três parciais. Permanecem limitações de manutenção cadastral, personalização de metas e síntese de conquistas no painel. A disponibilidade e as garantias de segurança exigem evidências operacionais adicionais, especialmente diante da falha de inicialização associada ao banco configurado. Os testes aprovados reduzem incertezas sobre regras específicas, mas não substituem a validação ponta a ponta e a avaliação com o público-alvo.

Como trabalhos futuros, propõem-se finalizar as lacunas funcionais, consolidar a integração de tarefas externas, uniformizar regras temporais, validar o armazenamento e a autorização entre contas e avaliar a acessibilidade e usabilidade. Também se recomenda conduzir estudo com participantes e revisão especializada dos conteúdos e respostas automatizadas, com procedimentos adequados ao tratamento de dados emocionais. Essas etapas permitirão investigar a contribuição do aplicativo ao cotidiano dos estudantes sem antecipar conclusões de eficácia a partir apenas da presença das funcionalidades.

## 11. Pendências e próximos passos

| Prioridade | Natureza | Ação e critério concreto de conclusão |
| --- | --- | --- |
| Alta | Ambiente e verificação | Corrigir a configuração/conectividade do banco de teste e separar ambiente de teste de dados reais; executar contexto e migrações em banco descartável e comprovar sucesso |
| Alta | Validação de segurança | Testar com duas contas a impossibilidade de consultar/alterar recursos alheios; validar respostas sem token, token expirado e assinatura inválida; conferir HTTPS e rotação das chaves na implantação |
| Alta | Finalização de RF14 | Permitir que o usuário escolha metas e respectivos valores/prazos, com persistência e progresso, ou formalizar redução de escopo acadêmico |
| Alta | Finalização de RF02 e RF18 | Delimitar e implementar edição cadastral esperada; apresentar resumo de conquistas no dashboard conforme detalhamento original |
| Média | Integração funcional | Reconectar Google Tasks a uma rota/template consumidor e validar callback OAuth e sincronização manual com conta de teste autorizada; conservar a distinção entre exportação e importação |
| Média | Regras e consistência | Uniformizar fuso e critério de sequência entre diário, hábitos e gamificação; verificar comportamento em virada de dia e preservar informação sobre atualização do registro diário |
| Média | Escopo narrativo | Definir se o apoio próximo continuará como link externo ou terá mapa/lista próprios; explicitar alcance da detecção de sobrecarga e da assistência inteligente |
| Média | Validação do protótipo | Exercitar CRUD, histórico e gráficos com banco real de teste; testar microfone em navegadores-alvo e validar falhas do provedor sem impedir salvamento textual |
| Média | Qualidade e pesquisa | Executar auditoria de acessibilidade e estudo de usabilidade; revisar conteúdo e respostas de IA; planejar estudo que permita investigar efeitos sem presumir eficácia |
| Média | Atualização acadêmica | Substituir contagem histórica pela matriz desta versão, citando método e limites; atualizar arquitetura e tecnologias; registrar decisões de substituição e produzir figuras com dados fictícios |
| Baixa | Documentação técnica | Harmonizar `AGENTS.md`, `GOOGLE_TASKS.md` e `DEPLOY.md` quanto a migrations, CORS, testes e hospedagem efetiva; remover afirmações de funcionalidades acessíveis enquanto estiverem desconectadas |

As ações acima são propostas de continuidade, não alterações executadas durante a geração deste documento.

## 12. Registro de evidências e verificações

### 12.1 Catálogo rastreável de arquivos e símbolos

Os caminhos abaixo são relativos à raiz do projeto. São fornecidos arquivos e símbolos em vez de números de linha, que podem mudar na árvore de trabalho em edição.

| Referência | Arquivos e símbolos principais | O que sustenta |
| --- | --- | --- |
| E01 | `tcc_frontend/src/app/features/auth/auth.component.ts` (`enviar`); `core/auth/auth.service.ts`; `features/auth/services/auth.service.ts` (`AuthApiService`); `tcc_backend/src/main/java/com/example/tcc_backend/controller/AutenticacaoController.java`; `service/UsuarioService.java` (`cadastrar`); `service/AutenticacaoService.java`; `controller/request/CadastroRequest.java`; `mapper/UsuarioMapper.java` | Cadastro, autenticação, validação e hash antes de persistir |
| E02 | `tcc_frontend/src/app/features/perfil/perfil.component.ts` (`salvar`, `alternarTema`); `features/perfil/biblioteca-avatares.component.ts`; `core/user/usuario.service.ts`; `tcc_backend/src/main/java/com/example/tcc_backend/controller/UsuarioController.java`; `service/UsuarioService.java` (`atualizarPerfil`, `atualizarAvatar`, `atualizarTema`); `controller/request/UsuarioUpdateRequest.java` | Limites de edição do perfil e preferências |
| E03 | `tcc_frontend/src/app/features/tarefas/tarefas.component.ts` e `.html`; `features/tarefas/services/tarefa-api.service.ts`; `tcc_backend/src/main/java/com/example/tcc_backend/controller/TarefaController.java`; `service/TarefaService.java` (`criar`, `atualizar`, `concluir`, `excluir`, `listar`); `service/CategoriaService.java`; `repository/TarefaRepository.java`; `domain/Tarefa.java` | CRUD, lista, categorias, propriedade e agenda |
| E04 | `tcc_frontend/src/app/features/habitos/habitos.component.ts` (`alternarRegistro`, `carregar`, `salvar`); `features/habitos/services/habito-api.service.ts`; `tcc_backend/src/main/java/com/example/tcc_backend/controller/HabitoController.java`; `service/HabitoService.java` (`marcarConcluido`, `obterResumo`, `validarDataRegistro`); `repository/RegistroHabitoRepository.java` | CRUD e registro de hábitos, sequência agregada |
| E05 | `tcc_frontend/src/app/features/emocional/registro-diario.component.ts` (`avancar`, `continuarSemIa`); `features/emocional/emocional-api.service.ts`; `tcc_backend/src/main/java/com/example/tcc_backend/controller/RegistroEmocionalController.java`; `service/RegistroEmocionalService.java` (`registrar`, `listar`); `repository/RegistroEmocionalRepository.java`; `domain/RegistroEmocional.java`; `mapper/RegistroEmocionalMapper.java` | Registro diário, atualização do dia e persistência |
| E06 | `tcc_frontend/src/app/features/emocional/emocional.component.ts` (`carregarRegistros`, `navegarMes`, `abrirRegistro`); `emocional.component.html`; `registro-detalhes.component.ts` e `.html` | Consulta do histórico e conteúdo de registros anteriores |
| E07 | `tcc_frontend/src/app/features/dashboard/dashboard.component.ts` (`carregar`, `intervalo`, `diasEmocionais`, `registroHoje`, `tarefasHoje`); `dashboard.component.html` | Painel, gráfico por período, resumo e mensagem motivacional |
| E08 | `tcc_frontend/src/app/features/emocional/registro-diario.component.ts` (`contextoIa`, `alternarGravacao`, `avancar`); `emocional-api.service.ts`; `tcc_backend/src/main/java/com/example/tcc_backend/controller/ApoioEmocionalController.java`; `service/ApoioEmocionalService.java` (`perguntas`, `apoio`, `transcrever`, `validarAudio`); `service/GeminiService.java` (`gerar`, `transcrever`, `enviarComRetentativas`) | Personalização contextual, áudio, transcrição e tratamento de falhas |
| E09 | `tcc_backend/src/main/java/com/example/tcc_backend/service/GamificacaoService.java` (`obterResumo`, `criarConquistas`, `calcularSequenciaAtual`, `META_TAREFAS_SEMANAL`); `controller/GamificacaoController.java`; `controller/response/ResumoGamificacaoResponse.java`; `tcc_frontend/src/app/features/conquistas/conquistas.component.ts` e `.html`; `shared/components/trilha-gamificacao` | Conquistas, metas fixas e indicadores |
| E10 | `tcc_frontend/src/app/features/recursos/recursos.component.ts` (`recursos`, `recursosPorCategoria`); `recursos.component.html`; `features/habitos/habitos.component.ts` (leitura de `queryParamMap`) | Práticas textuais e conversão em formulário de hábito |
| E11 | `tcc_frontend/src/app/features/apoio/apoio.component.ts` e `.html`; `apoio-api.service.ts`; `tcc_backend/src/main/java/com/example/tcc_backend/controller/ContatoEmergenciaController.java`; `service/ContatoEmergenciaService.java` (`listar`, `criar`, `atualizar`, `excluir`); `repository/ContatoEmergenciaRepository.java` | Link externo Maps e contatos pessoais |
| E12 | `tcc_backend/src/main/java/com/example/tcc_backend/security/SecurityConfig.java`; `security/CorsConfig.java`; `service/UsuarioAutenticadoService.java`; `src/main/resources/application.yaml`, `application-prod.yaml`; `tcc_frontend/src/app/core/auth/token.service.ts`; `core/interceptors/jwt.interceptor.ts`; `core/guards/auth.guard.ts`; `app.routes.ts` | Autenticação, isolamento, CORS e limites de configuração |
| E13 | `tcc_backend/src/main/java/com/example/tcc_backend/service/GoogleTasksService.java` (`autorizar`, `conectar`, `sincronizar`, `desconectar`); `GoogleTasksClient.java`; `GoogleTokenCipher.java`; `controller/GoogleTasksController.java`; `tcc_frontend/src/app/features/tarefas/google-tasks.component.ts`; `tarefas.component.ts`; `tarefas.routes.ts` | Integração opcional e ausência de consumidor frontend encontrada na busca |
| E14 | `tcc_backend/pom.xml`, `Dockerfile`, `src/main/resources/db/migration/V1__schema_inicial.sql`, `V2__versao_google_e_indices.sql`; `tcc_frontend/package.json`, `package-lock.json`, `angular.json`, `vercel.json`, `scripts/configure-api.mjs`, `scripts/configure-api.test.mjs`; `DEPLOY.md` | Inventário, schema, infraestrutura e verificação |

Os caminhos Java abreviados nas linhas de E01–E13 mantêm o prefixo `tcc_backend/src/main/java/com/example/tcc_backend/`. As referências de teste abaixo usam `tcc_backend/src/test/java/com/example/tcc_backend/` e os arquivos frontend terminados em `.spec.ts` nos diretórios das respectivas funcionalidades.

### 12.2 Comandos e resultados observados

| Comando/verificação | Local | Resultado |
| --- | --- | --- |
| `git status --short`, `git branch --show-current`, `git rev-parse HEAD` | Raiz | Branch/commit registrados; numerosas alterações locais anteriores à análise, incluindo estilos e templates |
| `rg --files` com exclusão de dependências/artefatos e filtros de documentação, Python e PDF | Raiz | Dois módulos; nenhum PDF ou módulo Python encontrado no conjunto pesquisado |
| `rg -n` para `TODO`, `mock`, `placeholder`, `meta`, `motiv`, `leaflet`, `Hume`, `SenseVoice`, `Python` | Fontes frontend e backend | Metas fixas e usos atuais identificados; mocks de testes não usados como evidência de integração real |
| `rg -n 'GoogleTasksComponent\|app-google-tasks\|google-tasks'` (padrões alternativos) | `tcc_frontend/src/app` | Referências nos próprios arquivos e serviço; sem consumidor de classe/seletor encontrado; rotas e imports de tarefas conferidos |
| `.\mvnw.cmd -o test` | `tcc_backend` | Falha do wrapper: erro PowerShell de matriz nula; Maven não iniciou por esse caminho |
| `mvn -o test` | `tcc_backend` | Maven local executou; 60 testes, zero falhas de asserção, um erro; resultado global `BUILD FAILURE` |
| `node --test scripts/configure-api.test.mjs` | `tcc_frontend` | 1 subteste aprovado, zero falhas |
| `npx --no-install ng test --watch=false` | `tcc_frontend` | Falhou no ambiente por `EPERM` ao resolver caminho sob diretório de usuário; substituído pela CLI local |
| `npx --no-install ng build --configuration development` | `tcc_frontend` | Mesmo impedimento de resolução de caminho, sem conclusão do build por essa chamada |
| `node node_modules/@angular/cli/bin/ng.js test --watch=false` | `tcc_frontend` | 8 arquivos e 21 testes aprovados; Vitest 4.1.10 |
| `node node_modules/@angular/cli/bin/ng.js build --configuration development` | `tcc_frontend` | Sucesso; saída `dist/tcc_frontend`; não valida budgets ou implantação de produção |
| Leitura de manifests/lockfile e relatórios Surefire XML | Respectivos módulos | Versões resolvidas e totais de testes conferidos sem reproduzir credenciais |

O build/test Angular foi invocado diretamente para evitar o erro do launcher `npx` e não regravar a configuração de API pelo script de início. O teste Node de configuração foi executado separadamente. Não foi executado `npm run build` de produção; portanto, não se afirma aprovação de validação de `API_URL`, download/inlining de fontes ou budgets de produção nesta sessão.

O erro do contexto Spring decorreu da conexão PostgreSQL: o servidor informou que o tenant/usuário configurado não foi encontrado. O identificador foi omitido. Não se conclui se a causa é credencial, configuração de pooler, projeto remoto ou estado do serviço; essa confirmação depende do ambiente. Não houve nova tentativa de contexto nem alteração de credenciais.

### 12.3 Detalhamento da suíte backend desta execução

| Classe de teste | Executados | Aprovados | Erros |
| --- | ---: | ---: | ---: |
| RegistroEmocionalMapperTests | 2 | 2 | 0 |
| CorsConfigTests | 2 | 2 | 0 |
| ApoioEmocionalServiceTests | 5 | 5 | 0 |
| AvatarServiceTests | 4 | 4 | 0 |
| ContatoEmergenciaServiceTests | 2 | 2 | 0 |
| GamificacaoServiceTests | 3 | 3 | 0 |
| GeminiServiceTests | 27 | 27 | 0 |
| GoogleTasksServiceTests | 6 | 6 | 0 |
| GoogleTokenCipherTests | 1 | 1 | 0 |
| HabitoServiceTests | 2 | 2 | 0 |
| RegistroEmocionalServiceTests | 2 | 2 | 0 |
| TemaServiceTests | 3 | 3 | 0 |
| TccBackendApplicationTests | 1 | 0 | 1 |
| **Total** | **60** | **59** | **1** |

A suíte frontend aprovada abrange `app`, interceptor JWT, usuário, tema, hábitos, emocional, perfil e componente de carregamento. Os testes não constituem cobertura completa de todos os RF. Não foram identificados testes específicos de CRUD de tarefas, cálculo do gráfico do dashboard ou fluxo real de gravação e transcrição entre os arquivos executados.

### 12.4 Pontos que ainda precisam de confirmação

- Conferência dos critérios e das promessas no PDF integral e em eventual versão acadêmica posterior; especialmente granularidade de sequências, alcance de manutenção de perfil e conteúdo psicoeducativo separado.
- Comprovação de pesquisa bibliográfica/comparativa e dos artefatos de design para avaliar OE01–OE04 com fontes próprias de pesquisa.
- Ambiente efetivo de publicação, disponibilidade, aplicação das migrações, backups e permissões do banco.
- Configuração e acesso efetivos aos modelos Gemini declarados, qualidade de respostas e transcrição; os nomes registrados são valores de código, não informação externa verificada.
- Funcionamento de cadastro, login, CRUD, histórico e gráfico com dados fictícios em ambiente de teste; isolamento entre usuários e compatibilidade real do áudio.
- Decisão formal sobre serviços Python/HumeAI/SenseVoice, pesquisa externa Maps, metas configuráveis e integração Google Tasks.

Este relatório é autocontido quanto à avaliação e suas contagens, mas suas evidências de implementação permanecem vinculadas à árvore de trabalho identificada. Alterações posteriores exigem reavaliar os requisitos afetados e atualizar a data e a referência da versão.
