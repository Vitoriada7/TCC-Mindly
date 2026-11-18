---
name: refatorar-logica
description: Refatorar logica em TypeScript, Angular e Java para melhorar organizacao, clareza e manutencao, reduzir verbosidade e complexidade desnecessaria e eliminar gambiarras. Usar em pedidos de simplificacao, limpeza de codigo, extracao de responsabilidades ou revisao de comentarios.
---

# Refatorar logica

## Entender e preservar

- Ler instrucoes, convencoes, configuracoes, testes e os consumidores do codigo afetado.
- Identificar versoes reais de Angular, TypeScript, Java, bibliotecas e ferramentas de build a partir dos manifests, locks e configuracoes de compilacao.
- Identificar entradas, saidas, contratos, efeitos colaterais, ordenacao, tratamento de erros e regras de negocio antes de alterar.
- Preservar o comportamento observavel, APIs publicas, validacoes, seguranca e transacoes. Tratar defeitos encontrados separadamente de mudancas puramente estruturais.
- Aplicar sempre os padroes atuais recomendados oficialmente para as versoes suportadas. Consultar documentacao oficial atual de Angular, TypeScript, Java e frameworks envolvidos antes de escolher APIs ou trocar padroes. Preferir recursos estaveis; evitar APIs obsoletas e experimentais.
- Nao atualizar dependencias nem migrar versoes automaticamente para usar uma novidade. Explicar incompatibilidades e escolher a melhor alternativa suportada. Se nao houver acesso a documentacao, informar e nao afirmar verificacao dos padroes mais recentes.

## Simplificar com clareza

- Escolher a solucao mais simples e menos verbosa que continue facil de compreender. Priorizar legibilidade sobre quantidade minima de linhas.
- Usar nomes descritivos e funcoes coesas, com uma responsabilidade clara. Separar regras de negocio de apresentacao, acesso a dados e efeitos colaterais quando houver beneficio concreto.
- Reduzir aninhamento com retornos antecipados quando facilitar a leitura. Remover condicoes redundantes, variaveis intermediarias dispensaveis e codigo morto comprovado.
- Extrair repeticoes quando expressarem a mesma regra ou responsabilidade. Evitar abstracoes especulativas, camadas sem finalidade, helpers genericos desnecessarios e fragmentacao em funcoes triviais.
- Evitar ternarios aninhados, cadeias densas e truques sintaticos. Preferir um loop claro a uma composicao funcional dificil de ler.
- Nao mascarar problemas com desativacao de lint, casts sem fundamento, excecoes engolidas, valores magicos ou atrasos arbitrarios.
- Preservar erros uteis, cancelamento, limpeza de recursos e semantica assincrona. Nao mudar concorrencia, ordem de execucao ou limites transacionais inadvertidamente.

## Aplicar conforme a linguagem

- Em TypeScript, preferir inferencia onde o tipo for claro e tipos explicitos em contratos relevantes. Evitar any, assercoes nao nulas e casts para silenciar problemas; estreitar tipos e modelar estados invalidos adequadamente.
- Em Angular, manter templates e componentes compreensiveis, separar responsabilidades conforme a arquitetura existente e escolher APIs reativas e de injecao segundo a recomendacao oficial compativel. Preservar deteccao de mudancas, ciclo de vida e descarte de inscricoes; nao substituir mecanismos reativos mecanicamente.
- Em Java, usar recursos suportados pela versao configurada e convencoes idiomaticas do framework. Preservar nullabilidade, igualdade, mutabilidade, fechamento de recursos, excecoes e transacoes. Usar streams, records ou outros recursos somente quando simplificarem o caso concreto.
- Aplicar formatadores e convencoes locais de organizacao de imports, arquivos e membros; nao reorganizar arquivos alheios ao escopo.

## Restringir comentarios

- Tornar o codigo autoexplicativo antes de considerar um comentario.
- Nao adicionar comentarios de narracao obvia, cabecalhos decorativos ou explicacoes que repitam nomes e instrucoes.
- Adicionar comentario apenas quando uma logica continuar muito complexa mesmo apos simplificacao.
- Escrever o texto desses comentarios inteiramente em lowercase, em portugues natural, sem acentos, pontuacao ou outros caracteres especiais. Usar apenas letras de a a z, espacos e, se necessario, digitos; manter os delimitadores sintaticos exigidos pela linguagem.
- Preferir frases curtas como '// buscando dados em tal lugar' ou '// essa parte faz tal coisa', mas somente quando o trecho complexo realmente precisar de explicacao.
- Remover ou ajustar comentarios dispensaveis ou desatualizados nos trechos refatorados. Preservar avisos legais, licencas, diretivas de compilacao e ferramentas e documentacao publica obrigatoria; nao corromper metadados para obedecer ao formato de comentario explicativo.

## Verificar e entregar

- Executar formatacao, lint, verificacao de tipos, compilacao e testes existentes pertinentes ao escopo.
- Acrescentar testes de comportamento somente quando a mudanca tiver risco relevante; cobrir limites, erros e efeitos colaterais quando forem afetados. Nao criar testes que apenas reproduzam a implementacao.
- Revisar o diff procurando mudancas acidentais de comportamento, abstracoes desnecessarias e comentarios fora do padrao.
- Resumir o que ficou mais simples, quais contratos foram preservados, verificacoes realizadas e limitacoes reais. Nao prometer ausencia de regressao sem evidencia.
