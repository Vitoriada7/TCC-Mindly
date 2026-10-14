# Google Tasks no Mindly

A conexão é opcional. Na tela `/tarefas`, conectar a conta e clicar em **Sincronizar tarefas** envia todas as tarefas do usuário para uma lista Google Tasks chamada Mindly. Títulos, descrições, data do prazo e conclusão são enviados; tarefas sincronizadas e excluídas no Mindly são removidas do Google na próxima sincronização. Não há importação de alterações do Google. Hábitos e registros emocionais não são enviados.

## Conexão no Render

Para o frontend `https://tcc-mindly.onrender.com` e backend `https://mindly-api-coct.onrender.com`, configure no serviço do backend, em Environment:

```text
GOOGLE_TASKS_REDIRECT_URI=https://tcc-mindly.onrender.com/tarefas
CORS_ALLOWED_ORIGINS=https://tcc-mindly.onrender.com
```

Cadastre também `GOOGLE_TASKS_CLIENT_ID`, `GOOGLE_TASKS_CLIENT_SECRET` do cliente OAuth Web e `GOOGLE_TASKS_ENCRYPTION_KEY` (32 bytes aleatórios em Base64). Se uma chave de criptografia já estiver em produção, preserve seu valor. Os arquivos `config/*.local.properties` não são enviados ao Render. Salve as variáveis e aguarde o novo deploy do backend.

No Google Cloud, no mesmo cliente OAuth Web, adicione o URI de redirecionamento autorizado `https://tcc-mindly.onrender.com/tarefas`, sem barra final. A rota `/emocional/registro` não processa o retorno da integração. O retorno fica no frontend; a troca de código e armazenamento dos tokens acontecem no backend autenticado.

No serviço do frontend, confirme `API_URL=https://mindly-api-coct.onrender.com`; mudar essa variável exige novo build. Abra `/tarefas`, entre no Mindly e inicie a conexão pelo ícone Google. Autorize e, após o retorno, clique em Sincronizar tarefas.

Hospedar o site no Render não altera o status OAuth no Google: se ele continuar em Teste, só contas cadastradas como testadoras podem autorizar. Para público externo, publique o OAuth e conclua as verificações exigidas pelo Google.

## Configuração local (desenvolvimento)

No cliente OAuth Web do Google Cloud, adicionar exatamente este URI de redirecionamento autorizado:

`http://localhost:4200/tarefas`

Ativar Google Tasks API, configurar o escopo `https://www.googleapis.com/auth/tasks` e adicionar as contas de teste ao público externo em teste. Não é necessário adicionar origem JavaScript para este fluxo: o backend troca os códigos, e todos os endpoints da integração exigem JWT do Mindly.

Criar `config/google.local.properties` (ignorado pelo Git) dentro de `tcc_backend`, com:

```properties
google.tasks.client-id=SEU_CLIENT_ID
google.tasks.client-secret=SEU_CLIENT_SECRET
google.tasks.redirect-uri=http://localhost:4200/tarefas
google.tasks.encryption-key=CHAVE_BASE64_DE_32_BYTES
```

Também é possível configurar `GOOGLE_TASKS_CLIENT_ID`, `GOOGLE_TASKS_CLIENT_SECRET`, `GOOGLE_TASKS_REDIRECT_URI` e `GOOGLE_TASKS_ENCRYPTION_KEY` por ambiente. Preserve a chave de criptografia entre reinícios: ela protege os refresh tokens persistidos no banco. Nunca comite o arquivo local. Em produção, configure o URI HTTPS do frontend e registre-o também no Google Cloud.

Reiniciar o backend após configurar. A configuração existente `ddl-auto: update` cria `conexoes_google` e `tarefas_google`. A integração usa estado OAuth aleatório, vinculado ao usuário autenticado, válido por dez minutos e consumido uma única vez. Refresh tokens ficam cifrados com AES-GCM; access tokens são usados somente em memória.

## Uso e limites

Depois da autorização, clicar em **Sincronizar tarefas**. Repetir após criar, editar, concluir ou excluir tarefas. Ao desconectar, a autorização é revogada e os vínculos locais são removidos; tarefas já enviadas permanecem no Google. Reconectar cria uma nova lista na primeira sincronização, inclusive se for a mesma conta; a lista anterior permanece disponível.

A Google Tasks API aceita apenas a data do prazo. Horários continuam no Mindly. Ativar a exibição de Tarefas no Google Calendar para visualizar os prazos. Autorizações de aplicativos externos em teste podem expirar, exigindo reconexão. Se uma lista Mindly for apagada no Google, desconectar e conectar novamente.

Se uma sincronização falhar, alterações já enviadas podem ter sido aplicadas; repetir para continuar. Uma interrupção entre a criação no Google e a gravação do vínculo local pode gerar duplicata. Nesta versão as ações de conexão e sincronização são serializadas em uma instância do backend; implantação com várias instâncias exige coordenação adicional.

## API autenticada

- `GET /integracoes/google-tasks`: disponibilidade, conexão e última sincronização.
- `POST /integracoes/google-tasks/autorizar`: URL OAuth com estado vinculado ao usuário.
- `POST /integracoes/google-tasks/conectar`: recebe `{code, state}` e salva a autorização.
- `POST /integracoes/google-tasks/sincronizar`: envia as tarefas e retorna a quantidade.
- `DELETE /integracoes/google-tasks`: revoga e desconecta.

Referências: [OAuth Web](https://developers.google.com/identity/protocols/oauth2/web-server) e [Google Tasks](https://developers.google.com/workspace/tasks/reference/rest/v1/tasks).
