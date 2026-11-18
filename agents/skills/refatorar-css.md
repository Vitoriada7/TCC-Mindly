---
name: refatorar-css
description: Refatorar CSS e SCSS e os templates relacionados, especialmente em Angular, para organizar estilos, corrigir indentacao, consolidar utilitarios e extrair componentes reutilizaveis. Usar quando houver pedidos de limpeza de estilos, reducao de repeticao visual ou organizacao de CSS em utils, componentes e paginas.
---

# Refatorar CSS

## Inspecionar antes de alterar

- Ler as instrucoes do projeto, configuracoes de formatacao, estilos globais, templates, componentes e consumidores dos trechos afetados.
- Identificar as versoes instaladas de Angular, TypeScript, preprocessadores e bibliotecas, incluindo os arquivos de lock. Identificar encapsulamento, ordem de importacao e arquitetura de estilos.
- Preservar comportamento, aparencia, responsividade, acessibilidade e contratos publicos.
- Aplicar sempre os padroes atuais recomendados oficialmente para as versoes suportadas pelo projeto. Consultar documentacao oficial atual de Angular, TypeScript e tecnologias envolvidas antes de escolher APIs ou substituir padroes; distinguir APIs estaveis de experimentais e obsoletas. Nao congelar a skill em uma lista de novidades.
- Nao atualizar dependencias ou iniciar migracao de versao apenas para modernizar a refatoracao. Se o padrao atual exigir upgrade, explicar a limitacao e usar a melhor alternativa compativel. Se a documentacao nao estiver acessivel, declarar essa limitacao e nao afirmar que a recomendacao foi verificada.

## Organizar os estilos

- Usar indentacao e formatacao consistentes com o formatador do projeto; na ausencia dele, usar dois espacos. Manter nomes claros, blocos coesos e seletores simples.
- Neste projeto, separar componentes Angular em arquivos .ts, .html e .css com templateUrl e styleUrl ou styleUrls relativos. Nao manter template ou CSS inline, mesmo em componentes pequenos.
- Formatar tambem os templates HTML afetados com dois espacos de indentacao e linhas em branco entre blocos visuais para facilitar a leitura. Preservar espacos significativos em textos e elementos inline.
- Nao criar ou manter .gitkeep para versionar pastas vazias. Criar diretorios somente quando houver conteudo relevante.
- Organizar o diretorio CSS/SCSS em tres niveis de responsabilidade:
  - utils/: classes utilitarias pequenas e genericas, agrupadas por responsabilidade, por exemplo layout.css e spacing.css.
  - components.css ou components.scss: estilos visuais compartilhados de componentes reutilizaveis; dividir por componente quando o tamanho justificar.
  - pages/: arquivos exclusivos de paginas, somente quando houver regras especificas.
- Adaptar o caminho raiz a estrutura existente. Preservar estilos encapsulados de componentes Angular nos seus arquivos locais quando necessario; usar o arquivo compartilhado para estilos efetivamente compartilhados. Nao tornar todo estilo global para cumprir a estrutura.
- Centralizar utilitarios comuns em utils e reutiliza-los nos templates. Exemplo: .flex { display: flex; }, com utilitarios de alinhamento e espacamento apenas quando houver uso concreto. Usar tokens existentes para valores recorrentes.
- Se Tailwind ou outra biblioteca ja oferecer os utilitarios necessarios, reutilizar suas classes e integrar a organizacao em utils sem criar classes concorrentes ou duplicadas.
- Remover duplicacoes e regras sem uso somente apos verificar templates, classes dinamicas, estados e consumidores. Atualizar todas as referencias ao renomear classes.
- Respeitar cascata, especificidade, ordem de carregamento e encapsulamento ao mover regras. Evitar !important, seletores excessivamente aninhados e mecanismos de perfuracao de encapsulamento como solucao de conveniencia.

## Extrair reutilizacao com criterio

- Procurar repeticoes de estrutura, estilo e comportamento em templates e componentes.
- Extrair um componente quando repeticoes representarem a mesma responsabilidade visual ou funcional e puderem compartilhar um contrato pequeno de entradas, saidas e conteudo.
- Consolidar repeticoes apenas de declaracoes CSS em utilitarios ou estilos compartilhados; nao criar um componente de interface por coincidencia de estilo.
- Nao exigir um numero rigido de repeticoes. Considerar estabilidade da responsabilidade, variacoes e custo de manutencao; evitar componentes com muitos booleanos para acomodar casos sem relacao.
- Atualizar consumidores, imports e testes afetados ao extrair. Usar APIs Angular estaveis recomendadas para a versao instalada, preservando semantica HTML, foco, eventos e acessibilidade.

## Restringir comentarios

- Seguir o mesmo padrao de comentarios da skill refatorar-logica: preferir codigo autoexplicativo e remover narracao obvia ou cabecalhos decorativos.
- Comentar somente trechos cuja complexidade ainda exija explicacao. Usar portugues natural inteiramente em lowercase, sem acentos ou pontuacao, apenas letras de a a z, espacos e digitos entre os delimitadores CSS /* */.
- Preservar licencas, avisos legais e diretivas obrigatorias de ferramentas.

## Verificar e entregar

- Executar formatacao, lint, verificacao de tipos, build e testes existentes pertinentes ao escopo, conforme as ferramentas disponiveis.
- Comparar a interface antes e depois nos estados e larguras afetados quando houver ambiente visual. Conferir cascata, utilitarios globais e encapsulamento. Se nao for possivel verificar visualmente, informar.
- Nao criar testes para separacao de arquivos, formatacao de HTML ou refatoracao apenas visual sem mudanca de comportamento. Respeitar pedidos explicitos para nao criar ou executar testes.
- Resumir a organizacao resultante, reutilizacoes extraidas, verificacoes realizadas e limitacoes reais. Nao declarar garantia de ausencia de regressao sem evidencia.
