# Publicacao do Mindly

Arquitetura: Angular na Vercel, Spring Boot no Render e PostgreSQL no Supabase.
O Supabase hospeda o banco; o Flyway versiona e aplica as mudancas no schema.
A autenticacao continua no backend com JWT; nao e necessario usar Supabase Auth nem uma chave anon/service_role.

## 1. Supabase

Crie um projeto e abra Connect > Session pooler > JDBC. Use o pooler de sessao na porta 5432
para conexoes IPv4. Copie o host e o usuario indicados pelo painel, que pode ser postgres.PROJECT_REF.
Configure no backend:

```dotenv
DB_URL=jdbc:postgresql://HOST_DO_POOLER:5432/postgres?sslmode=require
DB_USERNAME=postgres.PROJECT_REF
DB_PASSWORD=SENHA_DO_BANCO
DB_SCHEMA=mindly
DB_POOL_SIZE=5
```

O profile prod usa por padrao o schema mindly, separado do public exposto pela Data API do Supabase.
Nao adicione mindly aos schemas expostos nem conceda acesso a anon/authenticated: o acesso aos dados
passa pela API Spring e suas regras de propriedade. O usuario JDBC precisa criar schemas/tabelas.
As migracoes nao alteram schemas internos como auth e storage.
Para verificar o certificado TLS, use sslmode=verify-full e sslrootcert com o certificado fornecido
pelo Supabase; sslmode=require cifra a conexao, mas nao verifica a identidade do servidor.

## 2. Migracoes e dados existentes

Em um schema vazio, o Flyway executa V1__schema_inicial.sql e V2__versao_google_e_indices.sql antes do Hibernate validar as entidades.
V2 garante controle de versao nao nulo da conexao Google e indices nas chaves estrangeiras,
aceitando a coluna e os indices ja existentes em bancos criados pelo Hibernate.
Nao edite uma migracao que ja foi aplicada; adicione V2, V3 etc. O baseline automatico fica desativado.

Para o banco local antigo, que foi criado por ddl-auto=update:

1. Faca backup antes de qualquer migracao.
2. Compare o schema real com V1 (colunas, tipos, nullability, constraints e relacionamentos).
   O validate do Hibernate sozinho nao verifica todos os indices e constraints.
3. Se o schema corresponder a V1, execute um baseline na versao 1. Para uma unica inicializacao,
   defina SPRING_FLYWAY_BASELINE_ON_MIGRATE=true e SPRING_FLYWAY_BASELINE_VERSION=1,
   com DB_SCHEMA=public e o banco correto. Remova essas variaveis depois.
4. Se houver diferencas em relacao a V1, corrija-as com SQL revisado antes do baseline. Nao apague tabelas para destravar o deploy.
   A ausencia da coluna conexoes_google.versao e esperada na V1: ela e adicionada pela V2 apos o baseline.

Para transferir dados ao Supabase, prefira criar o schema mindly pelas migracoes e importar apenas dados
nas tabelas correspondentes, respeitando a ordem das chaves estrangeiras. Ajuste as sequences de identidade
apos importar IDs. Um dump antigo de public nao e automaticamente convertido para mindly.
Nao importe os schemas internos do Supabase nem execute baseline em todo o public dele.

## 3. Chaves JWT

As chaves antigas sairam de src/main/resources e agora ficam em config/, ignoradas pelo Git.
Isso mantem o desenvolvimento local. Gere um par novo para producao, pois a chave anterior esteve
versionada no historico. Nao reutilize o par local em producao.

```sh
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out authz.pem
openssl pkey -in authz.pem -pubout -out authz.pub
```

No Render, cadastre os arquivos como Secret Files, com nomes authz.pem e authz.pub.
Configure JWT_PRIVATE_KEY=file:/etc/secrets/authz.pem e JWT_PUBLIC_KEY=file:/etc/secrets/authz.pub.
A chave privada deve estar em PEM PKCS#8 (BEGIN PRIVATE KEY).

## 4. Render

Crie um Web Service conectado ao repositorio, Root Directory tcc_backend, runtime Docker.
O Dockerfile compila com Maven e executa com Java 21, usando usuario sem privilegios.
Cadastre as variaveis de tcc_backend/.env.example no painel; o arquivo e um modelo, nao e carregado automaticamente.
Use SPRING_PROFILES_ACTIVE=prod. PORT e fornecida pelo Render; a aplicacao a respeita.
Configure CORS_ALLOWED_ORIGINS com a origem exata do frontend, sem barra final. Multiplas origens
sao separadas por virgula. Previews precisam ser autorizados explicitamente; nao sao liberados por curinga.

GEMINI_REQUEST_TIMEOUT_SECONDS configura o limite total de cada chamada ao Google (padrao: 90 segundos).
Um timeout nao confirma falha de credencial; significa que a chamada nao terminou no prazo.
GEMINI_API_KEY e usada apenas no backend. GEMINI_MODEL mantem o modelo configurado no projeto.
Para Google Tasks, configure GOOGLE_TASKS_CLIENT_ID, GOOGLE_TASKS_CLIENT_SECRET,
GOOGLE_TASKS_REDIRECT_URI e GOOGLE_TASKS_ENCRYPTION_KEY. Esta ultima precisa ser uma chave AES
em Base64: gere com openssl rand -base64 32. Guarde-a de forma permanente; muda-la impede decifrar tokens existentes.
Se a integracao estiver desativada, deixe client-id/secret/encryption-key vazios, mas configure o redirect de producao.

## 5. Vercel

Importe o mesmo repositorio com Root Directory tcc_frontend e preset Angular.
O vercel.json define npm run build, dist/tcc_frontend/browser e o fallback das rotas para index.html.
Cadastre API_URL=https://SUA_API.onrender.com nos ambientes desejados e publique.
O script de build valida HTTPS e gera a configuracao usada pelos services Angular; sem API_URL o build falha.
Esse valor e publico no bundle: nunca cadastre segredos do banco, JWT ou Gemini no frontend.
Cada alteracao em API_URL exige um novo build/deploy.

No Google Cloud, configure o cliente OAuth Web com a origem HTTPS do frontend e o redirect URI
exato https://SEU_FRONTEND.vercel.app/tarefas. O backend e o painel Google devem usar o mesmo valor.

## Banco local desta workspace

O banco local existente recebeu baseline na versao 1 e a migracao V2, preservando as tabelas e dados.
O backup anterior esta em tcc_backend/config/migration-backups/pre-flyway.sql e e ignorado pelo Git.
O baseline foi habilitado somente no comando de adocao; a configuracao normal continua com baseline automatico desativado.
Outros ambientes com banco antigo precisam seguir o procedimento de adocao descrito acima.

## Desenvolvimento local

O backend importa config/application.local.properties, config/gemini.local.properties e
config/google.local.properties quando existem. Use application.local.properties.example como modelo
para DB_PASSWORD; variaveis do processo tambem sao aceitas. Execute o Maven dentro de tcc_backend,
pois os caminhos dos arquivos locais sao relativos a essa pasta.
O Spring nao carrega .env automaticamente.

No PowerShell, dentro de tcc_frontend:

```powershell
npm start
# Build de producao:
$env:API_URL = 'https://SUA_API.onrender.com'
npm run build
# Testes:
npm test -- --watch=false
```

npm start usa localhost:8080 quando API_URL nao esta definida. Remova uma API_URL de producao
do terminal antes de voltar ao backend local. O arquivo api.generated.ts e sobrescrito pelos scripts;
nao o edite manualmente.

## Verificacao apos publicar

Teste cadastro/login, refresh direto de /tarefas, perfil, CRUD de tarefas/habitos e registros emocionais.
Teste Gemini e Google Tasks se configurados. Confirme que uma origem nao autorizada e rejeitada pelo CORS.
Configure backups e disponibilidade adequados no plano escolhido. Nenhuma conta ou recurso remoto
foi criado pelos arquivos deste repositorio.

Referencias:
- https://supabase.com/docs/guides/getting-started/quickstarts/spring-boot
- https://supabase.com/docs/guides/database/connecting-to-postgres
- https://docs.spring.io/spring-boot/how-to/data-initialization.html
- https://vercel.com/docs/project-configuration/vercel-json
- https://render.com/docs/docker
