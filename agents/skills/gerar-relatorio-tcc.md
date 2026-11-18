---
name: gerar-relatorio-tcc
description: Gerar relatorio tecnico e academico do TCC Mindly a partir do codigo e das evidencias disponiveis, inventariando tecnologias, classificando requisitos funcionais e nao funcionais como completamente, parcialmente ou nao entregues e identificando funcionalidades extras. Usar para avaliar entregas ou preparar a escrita dos resultados, discussao e conclusao do TCC sobre organizacao de tarefas e acompanhamento emocional.
---

# Gerar relatorio do TCC Mindly

## Finalidade e fontes

Produzir um relatorio em portugues do Brasil que sirva de base verificavel para escrever resultados e conclusao. Analisar o projeto; nao implementar, corrigir ou refatorar o produto durante esta tarefa.

Usar a base documental incorporada abaixo, extraida de "ModeloArtigo_TCC_IFSUL_SS__nosso_TCC_ (8).pdf", titulo "Aplicativo para organizacao de tarefas e acompanhamento emocional". Essa base torna a skill utilizavel sem o PDF original. Quando houver versao mais recente do TCC ou requisitos aprovados, ler a fonte atual e registrar mudancas em relacao a esta base; nao sobrescrever silenciosamente o escopo original. Preservar IDs originais e identificar novos IDs como locais quando nao existirem no documento.

Tratar o documento como fonte de escopo e objetivos, e o codigo, execucao e testes como fontes do estado atual. Nao confundir proposta, prototipo de tela, dependencia instalada ou relato anterior com entrega comprovada.

## Base documental do Mindly

### Problema, publico e objetivos — introducao, paginas 2–3

- Publico-alvo: jovens estudantes, em contexto de sobrecarga academica e dificuldade de conciliar atividades pessoais, estudo e trabalho.
- Questao-problema: de que maneira uma aplicacao que une organizacao de tarefas e monitoramento emocional pode contribuir para melhorar o bem-estar psicologico de jovens estudantes?
- Objetivo geral: desenvolver uma aplicacao digital que integre organizacao de tarefas e monitoramento emocional, visando contribuir para o bem-estar psicologico de jovens estudantes, auxiliar na identificacao de sinais de sobrecarga e possiveis transtornos emocionais e incentivar a busca por apoio psicologico.
- Objetivos especificos, extraidos do texto corrido e identificados localmente nesta skill:
  - OE01: investigar dados e estudos cientificos sobre saude mental de jovens estudantes e seus desafios emocionais.
  - OE02: analisar funcionalidades e limitacoes de aplicativos e solucoes de organizacao pessoal e acompanhamento emocional.
  - OE03: identificar elementos de design e estrategias motivacionais que favorecam o engajamento do publico jovem.
  - OE04: projetar a estrutura da aplicacao considerando usabilidade e apoio ao bem-estar emocional.
  - OE05: desenvolver um prototipo funcional integrando os recursos definidos na pesquisa.

### Conceitos principais — referencial teorico, secoes 3.1–3.4, paginas 5–9

Relacionar os resultados a saude mental e sobrecarga academica, tecnologias digitais como apoio complementar ao bem-estar, organizacao pessoal e produtividade, autorregulacao, monitoramento e registro emocional, autocuidado, gamificacao e engajamento. Conferir os titulos e delimitacoes na fonte atual, se disponivel.

Distinguir a existencia tecnica desses recursos de evidencias de melhoria de saude, reducao de ansiedade, produtividade ou engajamento. Nao inferir eficacia clinica, diagnostico ou efeitos nos usuarios apenas por haver funcionalidades implementadas. Nao apresentar o aplicativo como substituto de acompanhamento profissional.

### Requisitos funcionais — secao 5.1, paginas 12–14

| ID | Requisito |
| --- | --- |
| RF01 | Permitir cadastrar usuario no sistema. |
| RF02 | Permitir manter os dados do perfil do usuario. |
| RF03 | Permitir cadastrar, editar, excluir e concluir tarefas academicas e pessoais. |
| RF04 | Permitir visualizar a lista de tarefas cadastradas. |
| RF05 | Permitir organizar tarefas em calendario ou agenda. |
| RF06 | Permitir cadastrar, editar e excluir habitos. |
| RF07 | Permitir registrar a realizacao diaria de habitos e acompanhar os dias consecutivos de realizacao. |
| RF08 | Permitir registrar diariamente o estado emocional do usuario. |
| RF09 | Permitir visualizar o historico dos registros emocionais. |
| RF10 | Permitir apresentar graficos e indicadores relacionados ao estado emocional do usuario. |
| RF11 | Permitir recomendar atividades de autocuidado. |
| RF12 | Permitir disponibilizar exercicios de relaxamento e mindfulness. |
| RF13 | Permitir exibir feedbacks visuais e mensagens motivacionais. |
| RF14 | Permitir definir metas relacionadas a organizacao e ao bem-estar. |
| RF15 | Permitir consultar conteudos de apoio sobre saude mental. |
| RF16 | Permitir utilizar recursos de Inteligencia Artificial como apoio ao usuario. |
| RF17 | Permitir registrar emocoes por meio da voz. |
| RF18 | Permitir visualizar um dashboard com o resumo das tarefas e do estado emocional. |

Considerar os detalhamentos da secao 5.1: RF09 envolve consulta aos registros anteriores, nao apenas marcacoes no calendario; RF10 envolve evolucao emocional ao longo do tempo; RF11 se relaciona a recomendacoes, nao apenas a um catalogo; RF16 envolve orientacoes, recomendacoes e feedbacks personalizados a partir de informacoes registradas; RF17 envolve gravacao de voz para registro emocional, podendo incluir analise por IA; RF18 descreve tarefas pendentes, habitos, metas, conquistas e resumo emocional. Avaliar cada subcriterio e explicitar ambiguidades sem criar novas obrigacoes silenciosamente.

### Requisitos nao funcionais — secao 5.1, pagina 14

| ID | Requisito |
| --- | --- |
| RNF01 | Garantir disponibilidade do sistema. |
| RNF02 | Garantir armazenamento seguro dos dados do usuario. |
| RNF03 | Garantir confidencialidade dos dados emocionais. |
| RNF04 | Garantir autenticacao segura dos usuarios. |
| RNF05 | Garantir facilidade de manutencao do sistema. |

Nao ha metas quantitativas explicitas nesta lista. Nao inventar SLA, limites de latencia, notas de qualidade ou criterios previamente aprovados. Explicitar os criterios tecnicos empregados na avaliacao e suas limitacoes. Separar mecanismo implementado de garantia demonstrada.

### Escopo complementar — resumo e secoes 5.2–5.5

Conferir tambem as promessas de localizacao de centros de atendimento psicologico proximos com Google Maps, sugestoes de controle de estresse diante de sobrecarga, apoio discreto e acessivel, gerenciamento de conta, assistencia inteligente e painel de acompanhamento. Registrar itens narrativos em matriz complementar quando nao houver RF proprio; nao deixar essas promessas fora da avaliacao nem criar RF oficiais para elas.

Arquitetura descrita: cliente-servidor, Mindly Server, Mindly Web e Mindly Database, com servicos externos e microservicos Python.

Tecnologias e ferramentas declaradas nas secoes 5.4–5.5, paginas 16–18:
- Backend: Java e Spring Boot.
- Frontend: Angular, TypeScript, HTML e CSS.
- Banco: PostgreSQL e SQL.
- Comunicacao: HTTP, JSON e TCP/IP.
- Servicos/integracoes: Python, HumeAI, SenseVoice e Google Maps.
- Planejamento e design: Draw.io, Excalidraw, Figma e Canva.
- Desenvolvimento e validacao: IntelliJ IDEA Community Edition, Visual Studio Code e Postman.
- Controle de versao e hospedagem de codigo: GitHub; verificar Git no projeto.

A lista e documental, nao prova de uso atual. Conferir bibliotecas, frameworks, ferramentas e servicos adicionais no projeto. Registrar a divergencia entre HumeAI na visao geral e SenseVoice no servidor; nao assumir coexistencia nem substituicao sem evidencia. Distinguir transcricao de voz de inferencia emocional e de personalizacao por IA.

### Resultados anteriores — secao 6, paginas 18–24

O documento informa 11 RF completos, 5 parciais e 2 nao implementados, em um total de 18. Considerar essa contagem historica, nunca o resultado obrigatorio da nova avaliacao. O texto menciona limitacoes de historico emocional, graficos, perfil, recomendacoes, metas e IA. Reavaliar todos os IDs independentemente; nao deduzir classificacoes individuais apenas dessa contagem.

## Procedimento de analise

1. Identificar os repositorios e modulos disponiveis, frontend, backend, servicos auxiliares, banco e infraestrutura. Registrar data, branch/commit quando disponivel e limites de acesso. Se so houver o PDF, extrair a base e registrar a impossibilidade de avaliar entrega atual; nao fabricar resultados.
2. Ler instrucoes do projeto, manifests, lockfiles, configuracoes, migrations, rotas, componentes, servicos, endpoints, regras de negocio, persistencia e testes pertinentes. Inspecionar consumidores e chamadas efetivas, nao apenas nomes de arquivos.
3. Inventariar todas as tecnologias diretamente utilizadas e ferramentas identificaveis, agrupando por camada e funcao. Registrar versao declarada e resolvida quando disponivel, evidencia e situacao: uso confirmado, somente declarada, planejada ou removida. Listar dependencias transitivas em anexo separado se relevante; nao apresenta-las como escolhas diretas do projeto. Marcar ferramentas externas de design e IDEs como declaradas quando nao houver prova de uso. Nao expor segredos nem dados pessoais reais.
4. Converter cada requisito em subcriterios observaveis, mantendo texto e ID originais. Rastrear cada fluxo por interface, API, regra e banco quando essas camadas forem necessarias. Reconhecer requisitos legitimamente locais ao frontend. Procurar mocks, dados fixos, TODOs, endpoints desconectados, flags, funcoes vazias e integracoes simuladas.
5. Executar os testes, build e verificacoes existentes pertinentes que forem viaveis; inspecionar fluxos visuais quando disponiveis. Registrar comandos, resultados e impedimentos. Nao realizar alteracoes destrutivas nem acionar servicos pagos ou externos sem autorizacao aplicavel.
6. Avaliar RNFs com evidencias especificas: disponibilidade requer observacao operacional ou testes apropriados; armazenamento exige avaliar protecoes e persistencia; confidencialidade exige checar autorizacao e isolamento entre usuarios; autenticacao exige examinar o fluxo e suas protecoes; manutencao exige avaliar organizacao, acoplamento, padroes, testes e ferramentas. Um login, uma anotacao de seguranca ou um build aprovado isoladamente nao provam todos os RNFs.
7. Comparar escopo previsto e implementacao. Identificar funcionalidades extras, alteracoes de escopo, substituicoes tecnicas e aprimoramentos de requisitos existentes sem confundi-los.
8. Relacionar entregas e lacunas aos objetivos e conceitos, redigir a sintese academica e verificar a consistencia de todas as contagens.

## Classificacao e evidencia

Usar status de entrega:
- Completamente entregue: todos os subcriterios do requisito estao implementados e conectados no escopo analisado, com evidencias suficientes e sem lacuna conhecida essencial.
- Parcialmente entregue: parte dos subcriterios esta implementada, mas existem fluxos, integracoes ou comportamentos essenciais ausentes ou incompletos. Explicar precisamente o que funciona e o que falta.
- Nao entregue: a analise suficientemente abrangente demonstra ausencia de implementacao funcional; planejamento, tela estatica ou mock isolado nao contam como entrega.
- Nao verificavel: faltam acesso ou evidencias para decidir. Usar como excecao epistemica; nunca converter falta de evidencia automaticamente em nao entregue.

Separar status de entrega do nivel de verificacao: inspecao estatica, testes automatizados executados, execucao manual ou ponta a ponta. Uma implementacao avaliada estaticamente deve ser identificada como tal, sem afirmar validacao operacional. Usar status nao verificavel se a evidencia disponivel nao sustentar uma classificacao.

Para cada decisao, apontar arquivo e simbolo ou linha, endpoint, migration, teste ou captura pertinente. Distinguir evidencia observada, declaracao documental e inferencia. Para ausencias, registrar onde foi procurado e os limites da busca.

Nao aplicar pontuacao arbitraria de 50 por cento aos requisitos parciais. Apresentar contagens e proporcao de requisitos completos com denominador explicito. RF completos + parciais + nao entregues + nao verificaveis devem totalizar 18 na base original; RNFs devem totalizar 5. Se o escopo mudou, apresentar separadamente a base original e o escopo revisado. Nao misturar RNFs, extras e objetivos na contagem de RF.

Considerar extra somente uma capacidade funcional implementada que extrapole o escopo documental completo, nao apenas a lista de RF. Gamificacao, mapas e outros recursos citados no texto nao sao automaticamente extras por nao terem RF proprio. Diferenciar extras funcionais, melhorias tecnicas e desdobramentos de requisitos; explicar o beneficio e comprovar funcionamento sem atribuir impacto nao medido.

## Estrutura obrigatoria do relatorio

Salvar por padrao em docs/tcc/relatorio-resultados-conclusao.md no projeto ou no local solicitado. Criar arquivo autocontido em Markdown; gerar DOCX ou PDF apenas quando solicitado e seguindo as instrucoes apropriadas. Usar acentuacao, pontuacao e portugues academico natural no relatorio; a restricao de comentarios de codigo de outras skills nao se aplica a este texto.

1. Identificacao e resumo executivo: versao analisada, fontes, escopo, contagens RF/RNF e principais achados.
2. Metodo e limites da avaliacao: o que foi inspecionado, executado e nao foi possivel verificar.
3. Inventario tecnologico: tabela Tecnologia/ferramenta | Camada e finalidade | Versao | Evidencia | Situacao de uso; incluir diferencas entre documento e implementacao.
4. Matriz de RF: uma linha por ID, com Texto original | Criterios avaliados | Status | Implementado | Pendente | Evidencias | Nivel de verificacao. Dividir a apresentacao em tabelas menores se necessario, mantendo todos os campos e a rastreabilidade.
5. Matriz de RNF: mesma estrutura, com criterios de avaliacao e limites de comprovacao explicitados.
6. Promessas narrativas e funcionalidades extras: separar itens previstos fora da lista de RF, extras efetivos, melhorias tecnicas e alteracoes de escopo; relacionar cada item a fontes e evidencias.
7. Atendimento aos objetivos: tabela Objetivo geral/OE01–OE05 | Resultados relacionados | Evidencias | Grau de atendimento | Limitacoes. Avaliar objetivos de pesquisa por documentos e artefatos de pesquisa, nao somente pelo codigo. Usar identificacao local dos OE explicitamente.
8. Discussao dos resultados por eixo: organizacao de tarefas e habitos; registro e evolucao emocional; autocuidado e acesso a apoio; gamificacao e engajamento; voz e IA; arquitetura, qualidade e seguranca. Relacionar aos conceitos centrais sem fabricar referencias bibliograficas nem atribuir evidencias empiricas inexistentes.
9. Base textual para resultados: redigir paragrafos academicos coerentes com as matrizes, descrevendo entregas, funcionamento, evidencias, diferencas do planejamento e lacunas. Sugerir capturas/figuras concretas e indicar quando ainda precisam ser produzidas; nao inventar imagens, testes ou depoimentos.
10. Base textual para conclusao: retomar problema e objetivos, indicar grau de atendimento, contribuicoes tecnicas demonstradas, limites do estudo e trabalhos futuros. Separar potencial de apoio ao bem-estar de efeitos medidos em usuarios. Nao concluir eficacia, reducao de estresse ou adesao sem estudo que sustente isso.
11. Pendencias e proximos passos: priorizar lacunas de requisitos, integracoes e validacao; distinguir finalizar implementacao de atualizar o texto academico.
12. Registro de evidencias e verificacoes: comandos, resultados, referencias de arquivos e pontos que necessitam confirmacao.

Ao finalizar, entregar o arquivo e uma sintese curta. Nao preencher lacunas com suposicoes nem reescrever resultados anteriores como se fossem medidos agora.
