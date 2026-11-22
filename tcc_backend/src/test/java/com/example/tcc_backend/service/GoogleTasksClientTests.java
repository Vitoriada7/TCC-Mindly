package com.example.tcc_backend.service;

import com.example.tcc_backend.service.googletasks.dto.GoogleTarefaRequest;
import com.example.tcc_backend.service.googletasks.dto.GoogleTokenResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GoogleTasksClientTests {

    private final HttpClient http = mock(HttpClient.class);

    private final ObjectMapper mapper = new ObjectMapper();

    private final GoogleTasksClient client = new GoogleTasksClient("cliente", "segredo", "http://localhost:4200/tarefas", mapper, http);

    @SuppressWarnings("unchecked")
    private void responder(int status, String corpo) throws Exception {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(status);
        lenient().when(response.body()).thenReturn(corpo);
        doReturn(response).when(http).send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());
    }

    @Test
    void trocaCodigoComCamposOAuthCorretosELerTokensTipados() throws Exception {
        responder(200, "{\"access_token\":\"acesso\",\"refresh_token\":\"refresh\",\"scope\":\"tasks\",\"expires_in\":3600}");

        GoogleTokenResponse resposta = client.trocarCodigo("codigo com espaço");

        assertThat(resposta.accessToken()).isEqualTo("acesso");
        assertThat(resposta.refreshToken()).isEqualTo("refresh");
        assertThat(resposta.toString()).doesNotContain("acesso", "refresh");
        HttpRequest request = capturarRequest();
        assertThat(request.uri().toString()).isEqualTo("https://oauth2.googleapis.com/token");
        assertThat(corpo(request)).contains("grant_type=authorization_code", "code=codigo+com+espa%C3%A7o",
                "client_id=cliente", "client_secret=segredo", "redirect_uri=http%3A%2F%2Flocalhost%3A4200%2Ftarefas")
                .doesNotContain("refresh_token");
    }

    @Test
    void preservaNulosQueLimpamPrazoEConclusaoSemEnviarIdNaCriacao() throws Exception {
        responder(200, "{\"id\":\"nova\",\"kind\":\"tasks#task\"}");
        GoogleTarefaRequest tarefa = new GoogleTarefaRequest(null, "Estudar", "", "needsAction", null, null);

        assertThat(client.criarTarefa("lists/lista/tasks", "acesso", tarefa).id()).isEqualTo("nova");

        HttpRequest request = capturarRequest();
        Map<String, String> campos = mapper.readValue(corpo(request), new TypeReference<Map<String, String>>() {});
        assertThat(campos).doesNotContainKey("id").containsEntry("due", null).containsEntry("completed", null)
                .containsEntry("title", "Estudar").containsEntry("status", "needsAction");
        assertThat(request.headers().firstValue("Authorization")).contains("Bearer acesso");
    }

    @Test
    void enviaIdNaAtualizacao() throws Exception {
        responder(200, "{\"id\":\"existente\"}");
        GoogleTarefaRequest tarefa = new GoogleTarefaRequest("existente", "Estudar", "", "needsAction", null, null);

        client.atualizarTarefa("lists/lista/tasks/existente", "acesso", tarefa);

        HttpRequest request = capturarRequest();
        assertThat(request.method()).isEqualTo("PUT");
        assertThat(mapper.readValue(corpo(request), GoogleTarefaRequest.class).id()).isEqualTo("existente");
    }

    @Test
    void rejeitaTokenNumericoComErroSeguro() throws Exception {
        responder(200, "{\"access_token\":123}");

        assertThatThrownBy(() -> client.renovar("token-privado"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageNotContaining("token-privado")
                .satisfies(erro -> assertThat(((ResponseStatusException) erro).getStatusCode().value()).isEqualTo(502));
    }

    @Test
    void aceitaExclusaoSemCorpo() throws Exception {
        responder(204, "");
        assertThatCode(() -> client.excluirTarefa("lists/lista/tasks/id", "acesso")).doesNotThrowAnyException();
        assertThat(capturarRequest().method()).isEqualTo("DELETE");
    }

    @Test
    void preservaRevogacaoIdempotente() throws Exception {
        responder(400, "");
        assertThatCode(() -> client.revogar("refresh")).doesNotThrowAnyException();
    }

    @Test
    void preserva404ParaRecriarTarefasRemovidasNoGoogle() throws Exception {
        responder(404, "{}");
        GoogleTarefaRequest tarefa = new GoogleTarefaRequest("id", "Estudar", "", "needsAction", null, null);
        assertThatThrownBy(() -> client.atualizarTarefa("lists/lista/tasks/id", "acesso", tarefa))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(erro -> assertThat(((ResponseStatusException) erro).getStatusCode().value()).isEqualTo(404));
    }

    private HttpRequest capturarRequest() throws Exception {
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(http).send(captor.capture(), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());
        return captor.getValue();
    }

    private String corpo(HttpRequest request) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        CompletableFuture<String> resultado = new CompletableFuture<>();
        request.bodyPublisher().orElseThrow().subscribe(new Flow.Subscriber<ByteBuffer>() {
            public void onSubscribe(Flow.Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }
            public void onNext(ByteBuffer buffer) {
                byte[] parte = new byte[buffer.remaining()];
                buffer.get(parte);
                bytes.writeBytes(parte);
            }
            public void onError(Throwable erro) {
                resultado.completeExceptionally(erro);
            }
            public void onComplete() {
                resultado.complete(bytes.toString(StandardCharsets.UTF_8));
            }
        });
        return resultado.join();
    }

}
