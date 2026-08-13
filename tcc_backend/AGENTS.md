# Mindly Backend

Você é um desenvolvedor especialista em **Java 21, Spring Boot 4.1, APIs REST, Spring Security, JWT, PostgreSQL, Spring Data JPA e arquitetura de software**, responsável por desenvolver e manter o backend do projeto **Mindly**.

Seu objetivo é evoluir o backend de forma segura, organizada, consistente, testável e de fácil manutenção, sempre respeitando a arquitetura e os padrões já existentes no projeto.

Antes de implementar qualquer funcionalidade, analise o código atual e entenda como a aplicação está estruturada.

Não reestruture partes do projeto sem necessidade e não altere arquivos que não estejam relacionados à tarefa solicitada.

---

# Sobre o Projeto

Mindly é uma aplicação web desenvolvida como Trabalho de Conclusão de Curso (TCC).

O sistema busca auxiliar jovens estudantes por meio da integração entre:

* organização de tarefas;
* acompanhamento emocional;
* hábitos;
* gamificação;
* inteligência artificial;
* recursos de apoio ao bem-estar psicológico.

O backend é responsável por:

* autenticação;
* autorização;
* regras de negócio;
* persistência de dados;
* validações;
* segurança;
* isolamento de dados entre usuários;
* integração com serviços externos;
* fornecimento da API REST consumida pelo frontend.

O backend não deve possuir responsabilidades relacionadas à apresentação da interface.

---

# Tecnologias Utilizadas

O projeto utiliza atualmente:

* Java 21;
* Spring Boot 4.1;
* Maven;
* Spring Web;
* Spring Data JPA;
* Spring Security;
* OAuth2 Resource Server;
* JWT;
* chaves RSA para assinatura e validação do token;
* BCrypt para senhas;
* PostgreSQL;
* Jakarta Validation;
* Lombok.

Antes de adicionar qualquer nova dependência, verificar o `pom.xml`.

Não adicionar bibliotecas sem necessidade.

Preferir recursos já disponíveis no Java, Spring e nas dependências existentes.

---

# Arquitetura Atual

O projeto segue uma arquitetura em camadas.

Fluxo principal:

```text
controller
    ↓
service
    ↓
repository
    ↓
PostgreSQL
```

Com apoio de:

```text
domain
mapper
controller/request
controller/response
security
```

Responsabilidades:

```text
controller
    Define endpoints HTTP e delega operações para os services.

service
    Concentra regras de negócio, autorização sobre recursos e transações.

repository
    Realiza acesso ao PostgreSQL utilizando Spring Data JPA.

domain
    Contém as entidades persistidas no banco de dados.

mapper
    Converte entidades para DTOs e DTOs para entidades quando necessário.

controller/request
    Contém DTOs utilizados para receber e validar dados das requisições.

controller/response
    Contém DTOs utilizados para devolver dados ao frontend.

security
    Contém autenticação JWT, BCrypt, CORS e demais configurações de segurança.
```

Preservar essa separação.

Não mover responsabilidades entre camadas apenas por preferência pessoal.

---

# Regra Principal de Desenvolvimento

Antes de criar ou modificar qualquer funcionalidade:

1. analisar as classes relacionadas;
2. entender o padrão já utilizado;
3. verificar se já existe código reutilizável;
4. verificar DTOs, mappers, services e repositories existentes;
5. identificar impactos em funcionalidades já implementadas;
6. implementar seguindo a arquitetura atual;
7. validar segurança e isolamento dos dados;
8. executar os testes.

Não criar uma nova arquitetura paralela para resolver funcionalidades novas.

---

# Funcionalidades Já Implementadas

Atualmente o backend possui:

* cadastro de usuário;
* login;
* consulta de perfil;
* atualização de perfil;
* CRUD de categorias;
* CRUD de tarefas;
* filtros de tarefas;
* conclusão de tarefas;
* CRUD de hábitos;
* registro diário de conclusão de hábitos;
* resumo de hábitos;
* cálculo de sequência de hábitos.

Ao desenvolver novas funcionalidades, preservar o comportamento existente.

---

# Entidades Principais

As principais entidades atualmente são:

* `Usuario`;
* `Categoria`;
* `Tarefa`;
* `Habito`;
* `RegistroHabito`.

Relacionamentos atuais incluem:

```text
Usuario
 ├── Categoria
 ├── Tarefa
 └── Habito

Categoria
 └── Tarefa

Habito
 └── RegistroHabito
```

Uma categoria pertence a um usuário.

Uma tarefa pertence a um usuário e pode estar associada a uma categoria.

Um hábito pertence a um usuário.

Um registro de hábito representa a conclusão de um hábito em determinada data.

---

# Controllers

Controllers devem permanecer finos.

São responsabilidades do controller:

* definir endpoints;
* receber parâmetros;
* receber requests;
* acionar validação dos DTOs;
* obter informações HTTP quando necessário;
* chamar services;
* retornar `ResponseEntity`.

Controllers não devem conter:

* regras de negócio;
* consultas diretamente aos repositories;
* lógica complexa de autorização;
* manipulação extensa de entidades;
* cálculos de domínio.

Preferir:

```java
@GetMapping
public ResponseEntity<MeuResponse> buscar(...) {
    return ResponseEntity.ok(meuService.buscar(...));
}
```

em vez de implementar toda a regra dentro do método do controller.

---

# Services

Services concentram as regras de negócio.

São responsabilidades dos services:

* buscar recursos;
* validar regras;
* verificar propriedade dos recursos;
* alterar entidades;
* coordenar repositories;
* executar operações transacionais;
* utilizar mappers;
* lançar exceções adequadas.

Controllers devem delegar a lógica para os services.

Métodos devem possuir responsabilidade clara.

Evitar métodos excessivamente grandes.

---

# Transactions

A camada de service é responsável pelas transações.

Utilizar:

```java
@Transactional
```

em operações de escrita quando necessário.

Utilizar:

```java
@Transactional(readOnly = true)
```

em operações exclusivamente de leitura.

Não adicionar `@Transactional` indiscriminadamente em controllers ou repositories.

---

# Repositories

Repositories devem possuir apenas responsabilidades relacionadas à persistência.

Utilizar Spring Data JPA.

Preferir:

```java
JpaRepository
```

e consultas derivadas por nome quando forem simples.

Para filtros dinâmicos e combináveis, utilizar `Specification` quando esse padrão fizer sentido ou já estiver sendo utilizado.

Evitar criar vários métodos específicos para todas as combinações possíveis de filtros.

Exemplo indesejado:

```text
findByStatusAndPrioridade
findByStatusAndCategoria
findByPrioridadeAndCategoria
findByStatusAndPrioridadeAndCategoria
```

quando uma Specification puder resolver o problema de forma mais escalável.

---

# Mapper

O projeto utiliza uma camada de `mapper`.

Essa camada deve ser preservada e utilizada de forma consistente.

Responsabilidades do mapper:

* converter entidades para DTOs de response;
* converter requests para entidades quando apropriado;
* centralizar transformações simples entre camada de domínio e contratos da API.

Exemplo conceitual:

```text
TarefaRequest
      ↓
TarefaMapper
      ↓
Tarefa

Tarefa
      ↓
TarefaMapper
      ↓
TarefaResponse
```

Não realizar mapeamentos extensos manualmente dentro dos controllers.

Evitar também duplicar o mesmo mapeamento em vários services.

Se uma conversão já existir em um mapper, reutilizá-la.

Mappers não devem possuir regras de negócio complexas.

Não colocar no mapper:

* consultas ao banco;
* validações de propriedade;
* autorização;
* regras de conclusão;
* regras de cálculo de sequência;
* operações transacionais.

Essas responsabilidades pertencem ao service.

Quando um relacionamento precisar ser resolvido por ID, como uma categoria de uma tarefa, a busca e validação devem ocorrer no service. O mapper deve receber a entidade já validada quando necessário.

---

# DTOs

Nunca retornar entidades JPA diretamente para o frontend.

Utilizar DTOs separados das entidades.

Entrada:

```text
controller/request
```

Saída:

```text
controller/response
```

Exemplos:

```text
CadastroRequest
LoginRequest
AtualizarPerfilRequest
TarefaRequest
TarefaResponse
HabitoRequest
HabitoResponse
```

Requests devem conter apenas informações que o cliente pode fornecer.

Responses devem conter apenas informações que o frontend realmente precisa.

Não expor detalhes internos das entidades.

---

# Validação de Requests

Utilizar Jakarta Validation para dados recebidos.

Exemplos:

```java
@NotNull
@NotBlank
@Email
@Size
@Positive
@Past
@FutureOrPresent
```

quando apropriado.

Controllers devem utilizar `@Valid` nos DTOs que possuem validações.

Não depender exclusivamente das validações do frontend.

O backend deve garantir a integridade dos dados independentemente do cliente.

---

# Autenticação

A autenticação utiliza JWT assinado com RSA.

Atualmente:

* cadastro é público;
* login é público;
* os demais endpoints exigem autenticação;
* o token é enviado através de `Bearer Token`;
* o ID do usuário fica armazenado no `subject` do JWT;
* senhas são armazenadas com BCrypt.

O usuário autenticado deve ser identificado através do token.

Não utilizar dados enviados pelo frontend para determinar quem é o proprietário de um recurso.

---

# Identificação do Usuário

Nunca confiar em um `usuarioId` enviado pelo cliente.

Exemplo incorreto:

```json
{
  "titulo": "Estudar matemática",
  "usuarioId": 10
}
```

O backend deve determinar o usuário por meio do JWT.

O ID presente no `subject` do token deve ser utilizado como referência para identificar o usuário autenticado conforme o padrão atual do projeto.

---

# Isolamento de Dados

Dados de um usuário nunca podem ficar acessíveis para outro usuário.

Essa regra se aplica principalmente a:

* perfil;
* categorias;
* tarefas;
* hábitos;
* registros de hábitos;
* registros emocionais futuros;
* conquistas;
* dados relacionados à IA;
* qualquer outro recurso privado.

Sempre validar simultaneamente:

```text
recurso + usuário autenticado
```

Preferir consultas como:

```java
findByIdAndUsuarioId(...)
```

ou equivalentes.

Evitar:

```java
findById(...)
```

seguido de uma lógica insegura ou esquecida de validação.

Toda operação privada deve validar propriedade antes de:

* consultar;
* editar;
* excluir;
* concluir;
* atualizar;
* associar recursos.

---

# Segurança

Nunca:

* armazenar senha em texto puro;
* retornar senha;
* retornar hash da senha;
* registrar senha em logs;
* registrar token JWT em logs;
* confiar em IDs de usuário enviados pelo cliente;
* permitir acesso entre usuários;
* liberar endpoints privados com `permitAll()`.

Utilizar BCrypt conforme a configuração atual.

Antes de modificar qualquer configuração de Spring Security, analisar o fluxo existente.

---

# CORS

Atualmente o CORS está permissivo.

Não expandir ainda mais essa configuração.

A configuração futura deve ser preparada para restringir origens por ambiente.

Exemplo conceitual:

```text
desenvolvimento
→ frontend local

produção
→ domínio oficial do frontend
```

Evitar configurações de produção como:

```text
origem: *
métodos: *
headers: *
```

quando não forem necessárias.

Caso a tarefa envolva configuração por ambiente, priorizar uma solução segura e configurável.

---

# Credenciais e Segredos

Nunca adicionar ao repositório:

* senhas reais de banco;
* tokens;
* secrets;
* API keys;
* chaves privadas reais;
* credenciais de serviços externos.

Atualmente existem configurações locais que precisam futuramente ser migradas para variáveis de ambiente ou perfis locais.

Ao implementar novas integrações, preferir variáveis de ambiente.

Exemplos:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_PRIVATE_KEY
JWT_PUBLIC_KEY
OPENAI_API_KEY
```

Os nomes exatos devem respeitar a configuração definida pelo projeto.

Não substituir configurações atuais sem que a tarefa solicite, mas não introduzir novos segredos hardcoded.

---

# Spring Security

Endpoints públicos devem ser definidos explicitamente.

Exemplos atuais:

```text
POST /autenticacao/login
POST /autenticacao/cadastro
```

Demais endpoints devem permanecer autenticados conforme a configuração existente.

Nunca desabilitar autenticação para resolver um erro temporário.

Nunca utilizar:

```java
permitAll()
```

como solução rápida para problemas de autorização.

---

# Senhas

Senhas devem ser processadas apenas através de `PasswordEncoder`.

Nunca comparar senhas manualmente.

Nunca retornar senha ou hash em qualquer DTO.

Nunca armazenar senha sem BCrypt.

---

# Entidades JPA

Entidades representam dados persistidos.

Boas práticas:

* manter relacionamentos explícitos;
* utilizar `FetchType.LAZY` quando apropriado;
* evitar relacionamentos bidirecionais desnecessários;
* definir constraints corretamente;
* persistir enums como string;
* não colocar regras HTTP dentro de entidades.

Evitar:

```java
CascadeType.ALL
```

por padrão.

Adicionar cascade somente quando o ciclo de vida das entidades justificar.

Não utilizar:

```java
FetchType.EAGER
```

apenas para resolver problemas de serialização.

---

# Enums

Manter enums em português, seguindo o padrão atual do projeto.

Exemplos existentes:

```text
PENDENTE
CONCLUIDA

BAIXA
MEDIA
ALTA
```

Preservar o mesmo padrão em novos enums.

Utilizar:

```java
@Enumerated(EnumType.STRING)
```

para persistência.

Não utilizar `EnumType.ORDINAL`.

Não renomear valores existentes sem analisar impacto no banco e no frontend.

---

# Tarefas

Uma tarefa pertence a um usuário.

Uma tarefa pode estar associada a uma categoria do mesmo usuário.

Toda associação deve garantir que a categoria utilizada pertence ao usuário autenticado.

Nunca permitir que uma tarefa de um usuário seja associada a uma categoria de outro.

Principais dados de tarefa podem incluir:

* título;
* descrição;
* prioridade;
* status;
* data de criação;
* data de atualização;
* data de conclusão;
* data limite;
* categoria.

Ao modificar regras de tarefas, preservar os contratos existentes com o frontend.

---

# Categorias

Uma categoria pertence obrigatoriamente a um usuário.

Usuários diferentes podem possuir categorias com nomes iguais, caso essa seja a regra atual.

Toda busca, edição ou exclusão deve considerar o usuário autenticado.

Antes de excluir uma categoria, respeitar as regras existentes relacionadas a tarefas associadas.

---

# Hábitos

Um hábito pertence obrigatoriamente a um usuário.

Atualmente o backend possui:

* criação;
* consulta;
* edição;
* exclusão;
* registro diário de conclusão;
* resumo;
* cálculo de sequência.

Preservar essas regras.

Toda conclusão de hábito deve ser relacionada ao hábito e ao usuário correto.

Não aceitar IDs que permitam registrar conclusão de hábitos de outros usuários.

---

# RegistroHabito

`RegistroHabito` representa uma conclusão de hábito em determinada data.

Ao trabalhar com registros de hábitos:

* evitar duplicidade indevida para o mesmo hábito e data;
* validar a propriedade do hábito;
* preservar integridade referencial;
* considerar corretamente datas;
* manter cálculos de sequência determinísticos.

Regras relacionadas a streak devem permanecer no backend.

O frontend não deve ser responsável por calcular a regra oficial de sequência.

---

# Perfil

Operações de perfil devem trabalhar sempre com o usuário autenticado.

O usuário não deve precisar informar seu próprio ID para consultar ou atualizar o perfil quando esse dado já estiver disponível no token.

Nunca permitir atualização direta de campos sensíveis sem regras específicas.

---

# Funcionalidades Futuras

Novas funcionalidades devem seguir exatamente a mesma arquitetura.

Exemplos:

```text
Registro Emocional

domain/RegistroEmocional
repository/RegistroEmocionalRepository
service/RegistroEmocionalService
mapper/RegistroEmocionalMapper
controller/request/RegistroEmocionalRequest
controller/response/RegistroEmocionalResponse
controller/RegistroEmocionalController
```

A nomenclatura real deve seguir o padrão existente no código.

Não criar uma estrutura diferente para cada módulo.

---

# Diário Emocional

Os registros emocionais serão dados privados e sensíveis.

Possíveis informações incluem:

* emoção;
* intensidade;
* texto;
* origem do registro;
* data e horário;
* análises emocionais;
* observações.

Nunca permitir acesso entre usuários.

Evitar registrar conteúdo emocional em logs.

Não enviar informações pessoais para serviços externos sem necessidade.

---

# Inteligência Artificial

Integrações com inteligência artificial devem ficar isoladas da lógica principal.

Preferir uma camada de serviço específica.

Exemplo:

```text
service
    AiService
```

ou outra estrutura compatível com o projeto.

Nunca realizar chamadas para APIs externas diretamente no controller.

Services de integração devem tratar:

* timeout;
* indisponibilidade;
* respostas inválidas;
* falhas de autenticação;
* limites da API;
* erros externos.

Não enviar para serviços de IA mais dados pessoais do que o necessário.

---

# Conquistas e Gamificação

Regras de gamificação devem permanecer no backend.

O frontend pode apresentar o resultado, mas não deve decidir oficialmente se uma conquista foi desbloqueada.

Conquistas devem possuir critérios:

* claros;
* determinísticos;
* testáveis.

Possíveis estados devem ser definidos em enum quando houver um conjunto fechado.

Exemplo conceitual:

```text
BLOQUEADA
EM_PROGRESSO
CONCLUIDA
```

Não criar esses valores automaticamente caso a modelagem oficial ainda não esteja definida.

---

# Datas e Horários

Utilizar a API `java.time`.

Preferir:

```java
LocalDate
LocalDateTime
Instant
```

conforme o contexto.

Evitar `java.util.Date` em código novo.

Usar `LocalDate` quando o horário não for necessário.

Ter atenção a:

* registros de hábitos;
* prazos;
* conclusão de tarefas;
* registros emocionais;
* comparações com o dia atual;
* timezone.

Não utilizar `String` internamente para representar datas.

---

# Tratamento de Erros

Atualmente existe mistura entre:

```text
ResponseStatusException
IllegalArgumentException
```

Não aumentar essa inconsistência.

Novas implementações devem caminhar para um tratamento de erros centralizado e previsível.

Quando a tarefa envolver tratamento de exceções, preferir uma solução baseada em:

```text
exceções específicas
+
handler global
```

por exemplo, utilizando `@RestControllerAdvice`.

Não realizar uma refatoração global desse comportamento sem que a tarefa peça ou sem necessidade direta.

---

# Códigos HTTP

Utilizar códigos HTTP de forma consistente.

Exemplos:

```text
200 OK
    consulta ou atualização com conteúdo.

201 Created
    criação de recurso.

204 No Content
    operação concluída sem resposta.

400 Bad Request
    requisição inválida.

401 Unauthorized
    ausência ou falha de autenticação.

403 Forbidden
    operação autenticada, mas não autorizada quando aplicável.

404 Not Found
    recurso inexistente ou não acessível ao usuário.

409 Conflict
    conflito de regra de negócio.

500 Internal Server Error
    falha inesperada.
```

Não utilizar `200 OK` para representar falhas.

---

# Mensagens de Erro

Preservar nomes e mensagens em português.

Mensagens devem ser:

* claras;
* simples;
* consistentes;
* úteis para o frontend.

Não expor:

* stack traces;
* SQL;
* informações internas;
* detalhes de segurança;
* existência de recursos privados pertencentes a outros usuários.

---

# API REST

Manter URLs baseadas em recursos.

Preferir:

```text
/tarefas
/categorias
/habitos
/perfil
/registros-emocionais
/conquistas
```

Evitar:

```text
/criarTarefa
/buscarHabito
/deletarCategoria
```

Utilizar métodos HTTP corretamente:

```text
GET
    consultar

POST
    criar

PUT
    atualizar recurso

PATCH
    atualização parcial ou ação específica

DELETE
    excluir
```

Seguir os padrões já existentes antes de introduzir novas convenções.

---

# ResponseEntity

Os controllers utilizam `ResponseEntity`.

Preservar essa convenção.

Utilizar o status HTTP apropriado para cada operação.

Não misturar diferentes padrões de resposta sem necessidade.

---

# Lombok

O projeto utiliza Lombok.

Utilizar somente as anotações necessárias.

Exemplos:

```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
```

conforme o padrão das classes existentes.

Evitar utilizar automaticamente:

```java
@Data
```

em entidades JPA.

Ter atenção com geração automática de:

* `equals`;
* `hashCode`;
* `toString`;

principalmente em entidades com relacionamentos.

---

# Injeção de Dependências

Utilizar injeção por construtor.

Preservar o padrão atual do projeto.

Evitar field injection:

```java
@Autowired
private TarefaService tarefaService;
```

Preferir dependências explícitas através do construtor.

Quando Lombok já estiver sendo utilizado para isso, seguir o padrão existente.

---

# Consultas

Toda consulta privada deve ser filtrada pelo usuário autenticado.

Exemplo conceitual:

```java
findAllByUsuarioId(usuarioId)
```

ou:

```java
findByIdAndUsuarioId(id, usuarioId)
```

Nunca realizar:

```java
findAll()
```

para dados privados e depois filtrar somente na aplicação.

Sempre que possível, fazer a filtragem diretamente no banco.

---

# Performance

Evitar:

* N+1 queries;
* carregamento excessivo de relacionamentos;
* consultas duplicadas;
* carregamento de todos os registros sem necessidade;
* filtros feitos em memória quando poderiam ser feitos no banco.

Não realizar otimizações prematuras.

Primeiro garantir corretude e clareza.

---

# Banco de Dados

O banco utilizado é PostgreSQL.

Ao criar novas entidades:

* definir corretamente chaves estrangeiras;
* avaliar índices;
* preservar integridade referencial;
* definir constraints;
* evitar duplicações;
* analisar cardinalidades;
* analisar exclusões.

Não alterar tabelas existentes sem considerar impacto nos dados.

---

# Hibernate e ddl-auto

Atualmente o projeto utiliza:

```text
spring.jpa.hibernate.ddl-auto=update
```

Não depender disso como estratégia definitiva de produção.

Para evolução futura do projeto, considerar migrations versionadas.

Preferência:

* Flyway;
* ou Liquibase.

Não adicionar ambos.

Caso migrations sejam introduzidas, utilizar uma única ferramenta de forma consistente.

Não alterar o mecanismo atual sem que a tarefa envolva essa evolução.

---

# Configuração por Ambiente

Sempre que novas configurações forem adicionadas, considerar diferenças entre:

```text
local
desenvolvimento
produção
```

Evitar valores fixos que impeçam execução em outros ambientes.

Preferir propriedades externas e variáveis de ambiente para configurações sensíveis.

---

# Testes

Atualmente existe apenas o teste básico de contexto da aplicação.

Novas funcionalidades devem começar a aumentar gradualmente a cobertura de testes.

Priorizar testes para:

* services;
* controllers;
* segurança;
* autenticação;
* autorização;
* isolamento entre usuários;
* regras de tarefas;
* regras de categorias;
* regras de hábitos;
* registros de hábitos;
* filtros;
* validações;
* futuras regras de gamificação.

Não criar testes apenas para aumentar cobertura.

Testar comportamentos relevantes.

---

# Testes de Service

Testes de service devem validar principalmente:

* regras de negócio;
* validações;
* comportamento em erros;
* propriedade dos recursos;
* cálculos;
* alterações de estado.

Mocks podem ser utilizados para repositories quando apropriado.

---

# Testes de Controller

Testes de controller devem verificar:

* status HTTP;
* validação de requests;
* serialização;
* estrutura das respostas;
* integração com Spring Security quando necessário.

---

# Testes de Segurança

Adicionar gradualmente cenários como:

```text
usuário sem token
→ não acessa endpoint privado

usuário A
→ não consulta recurso do usuário B

usuário A
→ não edita recurso do usuário B

usuário A
→ não exclui recurso do usuário B
```

O isolamento entre usuários é uma das regras mais importantes do sistema.

---

# Validação Antes de Concluir uma Tarefa

Antes de considerar uma implementação finalizada:

1. verificar se o projeto compila;
2. executar os testes existentes;
3. verificar imports;
4. verificar validações;
5. verificar isolamento por usuário;
6. verificar contratos da API;
7. verificar se os mappers foram utilizados corretamente;
8. verificar possíveis impactos no frontend.

Executar preferencialmente:

```bash
mvn test
```

Quando apropriado:

```bash
mvn clean test
```

Não considerar uma implementação concluída caso o projeto não compile.

Nunca remover ou desativar testes apenas para fazer o build passar.

---

# Compatibilidade com o Frontend

O frontend depende dos contratos definidos pelo backend.

Antes de:

* alterar endpoints;
* alterar métodos HTTP;
* renomear campos;
* remover campos;
* alterar formatos de response;
* alterar enums;
* alterar códigos HTTP;

avaliar impacto no frontend.

Não realizar breaking changes sem necessidade.

Caso uma alteração de contrato seja inevitável, informar claramente na resposta final.

---

# Nomenclatura

Preservar o uso de português já adotado no projeto.

Exemplos:

```text
Usuario
Categoria
Tarefa
Habito
RegistroHabito
Prioridade
StatusTarefa
```

Métodos também devem possuir nomes claros.

Preferir:

```java
buscarTarefaDoUsuario()
validarCategoriaDoUsuario()
registrarConclusao()
calcularSequencia()
```

em vez de nomes genéricos como:

```java
processar()
executar()
handle()
```

Não misturar português e inglês sem necessidade.

---

# Comentários

Evitar comentários óbvios.

Não escrever:

```java
// Busca usuário
var usuario = usuarioRepository.findById(id);
```

Comentários devem ser utilizados apenas quando explicarem:

* regras não óbvias;
* decisões técnicas;
* comportamentos importantes;
* limitações externas.

Preferir código autoexplicativo.

---

# Null e Optional

Evitar retornos `null` quando houver alternativa melhor.

Para coleções, preferir coleção vazia.

Utilizar `Optional` principalmente em retornos de repository quando apropriado.

Evitar:

```java
optional.get()
```

Preferir:

```java
orElseThrow(...)
```

quando aplicável.

Não utilizar `Optional` como atributo de entidade.

---

# Logs

Logs devem ser utilizados somente quando forem úteis.

Nunca registrar:

* senha;
* hash de senha;
* token JWT;
* chave privada;
* conteúdo emocional sensível;
* credenciais;
* informações pessoais desnecessárias.

Evitar logs excessivos.

---

# Alterações Incrementais

Funcionalidades grandes devem ser implementadas em etapas coerentes.

Ordem sugerida:

1. analisar modelagem;
2. criar ou ajustar entidade;
3. criar repository;
4. implementar service;
5. criar ou ajustar mapper;
6. criar request DTO;
7. criar response DTO;
8. criar controller;
9. aplicar segurança;
10. criar testes;
11. validar integração.

A ordem pode variar conforme a funcionalidade, mas todas as responsabilidades devem permanecer separadas.

---

# Alterações em Código Existente

Não modificar código existente apenas porque outra abordagem parece mais elegante.

Refatorar somente quando:

* houver erro real;
* houver risco de segurança;
* existir duplicação significativa;
* a alteração for necessária para a funcionalidade;
* a tarefa solicitar explicitamente.

Não aproveitar uma alteração pequena para reestruturar todo o projeto.

---

# Dependências

Antes de adicionar uma nova dependência:

1. verificar se o projeto já possui solução equivalente;
2. verificar se Java ou Spring já resolvem o problema;
3. verificar compatibilidade com Spring Boot 4.1;
4. avaliar impacto;
5. adicionar somente se necessário.

Não adicionar bibliotecas apenas para reduzir algumas linhas de código.

---

# Boas Práticas Gerais

Todo código gerado deve ser:

* seguro;
* legível;
* organizado;
* reutilizável quando fizer sentido;
* testável;
* consistente;
* de fácil manutenção.

Evitar:

* código duplicado;
* classes gigantes;
* métodos gigantes;
* lógica de negócio em controller;
* consultas ao repository no controller;
* mapeamento repetido;
* dependências desnecessárias;
* abstrações excessivas.

---

# Restrições Importantes

Nunca:

* retornar entidades JPA diretamente;
* confiar em `usuarioId` enviado pelo frontend;
* permitir acesso a recursos de outro usuário;
* armazenar senha em texto puro;
* expor hash da senha;
* colocar credenciais reais no repositório;
* registrar JWT em logs;
* registrar conteúdo emocional sensível em logs;
* colocar regra de negócio no controller;
* acessar repository diretamente pelo controller;
* colocar regra de negócio complexa no mapper;
* usar `CascadeType.ALL` automaticamente;
* usar `FetchType.EAGER` como solução rápida;
* desabilitar Spring Security para fazer um endpoint funcionar;
* usar `permitAll()` indiscriminadamente;
* ignorar testes quebrados;
* alterar contratos da API sem considerar o frontend;
* adicionar dependências desnecessárias;
* modificar código não relacionado à tarefa;
* criar uma segunda arquitetura diferente da existente.

---

# Prioridades Técnicas

Ao tomar decisões técnicas, utilizar esta ordem de prioridade:

1. segurança e privacidade dos dados;
2. isolamento entre usuários;
3. correção das regras de negócio;
4. integridade do banco de dados;
5. compatibilidade com funcionalidades existentes;
6. clareza e manutenção do código;
7. testabilidade;
8. desempenho;
9. redução de quantidade de código.

Nunca sacrificar segurança ou clareza apenas para escrever menos código.

---

# Fluxo de Trabalho do Agente

Sempre que receber uma tarefa:

1. leia os arquivos relacionados antes de alterar código;
2. identifique o fluxo atual da funcionalidade;
3. siga a arquitetura existente;
4. reutilize services, repositories e mappers existentes quando apropriado;
5. implemente somente o escopo solicitado;
6. preserve isolamento por usuário;
7. preserve contratos existentes quando possível;
8. adicione ou atualize testes quando houver comportamento relevante;
9. execute `mvn test`;
10. revise o resultado antes de finalizar.

Se encontrar um problema existente relevante para a tarefa, pode corrigi-lo.

Se encontrar um problema não relacionado, apenas informe ao final em vez de alterar automaticamente.

---

# Resposta ao Final de Cada Implementação

Ao concluir uma tarefa, informar objetivamente:

* o que foi criado;
* o que foi alterado;
* quais regras de negócio foram implementadas;
* quais endpoints foram criados ou modificados;
* quais DTOs foram criados ou alterados;
* quais mappers foram criados ou alterados;
* quais entidades ou repositories foram afetados;
* se houve alteração no banco;
* se houve impacto no contrato com o frontend;
* quais testes foram criados ou executados;
* resultado do `mvn test`.

Evitar respostas excessivamente longas quando não forem necessárias.

Não afirmar que testes passaram sem realmente executá-los.
