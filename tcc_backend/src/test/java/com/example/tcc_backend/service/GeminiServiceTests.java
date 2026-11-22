package com.example.tcc_backend.service;

import com.example.tcc_backend.service.gemini.GeminiConteudo;
import com.example.tcc_backend.service.gemini.dto.GeminiRequest;
import com.example.tcc_backend.service.gemini.dto.GeminiResponse;
import com.example.tcc_backend.service.gemini.dto.PerguntasGeradasResponse;
import com.example.tcc_backend.service.apoioemocional.ApoioEmocionalConteudo;
import com.example.tcc_backend.service.gemini.dto.TextoGeradoResponse;
import org.mockito.ArgumentCaptor;
import com.example.tcc_backend.validator.GeminiValidator;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.http.*;
import java.time.Duration;
import java.util.*;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import tools.jackson.databind.ObjectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GeminiServiceTests {

    @Test
    void normalizaNomeCopiadoDaListaDeModelosOuDoPainel() {
        assertThat(GeminiValidator.normalizarModelo("gemini-2.5-flash")).isEqualTo("gemini-2.5-flash");
        assertThat(GeminiValidator.normalizarModelo(" models/gemini-2.5-flash\n")).isEqualTo("gemini-2.5-flash");
        assertThat(GeminiValidator.normalizarModelo("\"gemini-2.5-flash\"")).isEqualTo("gemini-2.5-flash");
        assertThat(GeminiValidator.normalizarModelo("'models/gemini-2.5-flash'"))
                .isEqualTo("gemini-2.5-flash");
    }

    @Test
    void rejeitaUrlsParametrosOuConfiguracaoVazia() {
        for (String valor : new String[]{"", " ", "models/", "models/models/gemini-2.5-flash",
                "https://outro-site.com", "gemini-2.5-flash?key=segredo", "gemini-2.5-flash:generateContent"}) {
            assertThatThrownBy(() -> GeminiValidator.normalizarModelo(valor))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageNotContaining(valor.isBlank() ? "conteudo privado" : valor);
        }
    }

    @SuppressWarnings("unchecked")
    private HttpResponse<String> resposta(int status, String corpo, Map<String, List<String>> headers) {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(status);
        when(response.body()).thenReturn(corpo);
        when(response.headers()).thenReturn(HttpHeaders.of(headers, (nome, valor) -> true));
        return response;
    }

    private HttpResponse<String> indisponivelGoogle() {
        return resposta(503, "{\"error\":{\"status\":\"UNAVAILABLE\",\"message\":\"This model is currently experiencing high demand.\"}}", Map.of());
    }

    private HttpResponse<String> sucessoGoogle() {
        return resposta(200, "{\"candidates\":[{\"finishReason\":\"STOP\",\"content\":{\"parts\":[{\"text\":\"{\\\"texto\\\":\\\"ok\\\"}\"}]}}]}", Map.of());
    }

    @Test
    void concatenaPartesDeTextoIgnorandoPensamentos() throws Exception {
        HttpClient client = mock(HttpClient.class);
        GeminiService gemini = comFallback(client, new ArrayList<>());
        String corpo = new ObjectMapper().writeValueAsString(Map.of("candidates", List.of(Map.of(
                "finishReason", "STOP", "content", Map.of("parts", List.of(
                        Map.of("thought", true, "text", "conteudo interno"),
                        Map.of("text", "{\"texto\":"),
                        Map.of("text", "\"ok\"}")))))));
        doReturn(resposta(200, corpo, Map.of())).when(client)
                .send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());

        assertThat(gemini.gerar("teste", "ficticio", GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class).texto())
                .isEqualTo("ok");
        modelos(requests(client, 1), "gemini-3.5-flash-lite");
    }

    @Test
    void rejeitaConteudoQueNaoSejaObjetoJsonSemAcionarFallback() throws Exception {
        for (String conteudo : List.of("[]", "null", "texto invalido", "")) {
            HttpClient client = mock(HttpClient.class);
            GeminiService gemini = comFallback(client, new ArrayList<>());
            String corpo = new ObjectMapper().writeValueAsString(Map.of("candidates", List.of(Map.of(
                    "finishReason", "STOP", "content", Map.of("parts", List.of(Map.of("text", conteudo)))))));
            doReturn(resposta(200, corpo, Map.of())).when(client)
                    .send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());

            assertThatThrownBy(() -> gemini.gerar("teste", "ficticio", GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class))
                    .isInstanceOf(ResponseStatusException.class)
                    .satisfies(erro -> assertThat(((ResponseStatusException) erro).getStatusCode().value()).isEqualTo(503));
            modelos(requests(client, 1), "gemini-3.5-flash-lite");
        }
    }

    @Test
    void repete503ComEsperaCrescenteEAceitaRespostaPosterior() throws Exception {
        HttpClient client = mock(HttpClient.class);
        List<Duration> esperas = new ArrayList<>();
        GeminiService gemini = new GeminiService("chave-de-teste", "gemini-3.6-flash", "", new ObjectMapper(), client, esperas::add);
        doReturn(indisponivelGoogle(), indisponivelGoogle(), sucessoGoogle()).when(client).send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());

        assertThat(gemini.gerar("instrucao de teste", "relato ficticio", GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class).texto())
                .isEqualTo("ok");
        verify(client, times(3)).send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());
        assertThat(esperas).hasSize(2);
        assertThat(esperas.get(0).toMillis()).isBetween(1000L, 1249L);
        assertThat(esperas.get(1).toMillis()).isBetween(2000L, 2249L);
        ArgumentCaptor<HttpRequest> requests = org.mockito.ArgumentCaptor.forClass(HttpRequest.class);
        verify(client, times(3)).send(requests.capture(), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());
        assertThat(requests.getAllValues()).allSatisfy(request -> {
            assertThat(request.timeout().orElseThrow()).isLessThanOrEqualTo(Duration.ofSeconds(30));
            assertThat(request.uri().getPath()).isEqualTo("/v1beta/models/gemini-3.6-flash:generateContent");
        });
    }

    @Test
    void encerraAposTresRespostas503SemLoopInfinito() throws Exception {
        HttpClient client = mock(HttpClient.class);
        List<Duration> esperas = new ArrayList<>();
        GeminiService gemini = new GeminiService("chave-de-teste", "gemini-3.6-flash", "", new ObjectMapper(), client, esperas::add);
        doReturn(indisponivelGoogle()).when(client).send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());
        assertThatThrownBy(() -> gemini.gerar("teste", "ficticio", GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class))
                .isInstanceOf(ResponseStatusException.class);
        verify(client, times(3)).send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());
        assertThat(esperas).hasSize(2);
    }

    @Test
    void naoRepete404403Ou429() throws Exception {
        for (int status : new int[]{404, 403, 429}) {
            HttpClient client = mock(HttpClient.class);
            List<Duration> esperas = new ArrayList<>();
            GeminiService gemini = new GeminiService("chave-de-teste", "gemini-3.6-flash", "", new ObjectMapper(), client, esperas::add);
            doReturn(resposta(status, "{}", Map.of())).when(client).send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());
            assertThatThrownBy(() -> gemini.gerar("teste", "ficticio", GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class))
                    .isInstanceOf(ResponseStatusException.class);
            verify(client).send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());
            assertThat(esperas).isEmpty();
        }
    }

    @Test
    void respeitaRetryAfterQuandoCabeNoPrazo() throws Exception {
        HttpClient client = mock(HttpClient.class);
        List<Duration> esperas = new ArrayList<>();
        GeminiService gemini = new GeminiService("chave-de-teste", "gemini-3.6-flash", "", new ObjectMapper(), client, esperas::add);
        doReturn(resposta(503, "{}", Map.of("Retry-After", List.of("5"))), sucessoGoogle()).when(client).send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());
        assertThat(gemini.gerar("teste", "ficticio", GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class).texto()).isEqualTo("ok");
        assertThat(esperas).containsExactly(Duration.ofSeconds(5));
    }

    @Test
    void naoEsperaRetryAfterQueUltrapassaPrazoTotal() throws Exception {
        HttpClient client = mock(HttpClient.class);
        List<Duration> esperas = new ArrayList<>();
        GeminiService gemini = new GeminiService("chave-de-teste", "gemini-3.6-flash", "", new ObjectMapper(), client, esperas::add);
        ReflectionTestUtils.setField(gemini, "timeoutSegundos", 1);
        doReturn(resposta(503, "{}", Map.of("Retry-After", List.of("120")))).when(client).send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());
        assertThatThrownBy(() -> gemini.gerar("teste", "ficticio", GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class))
                .isInstanceOf(ResponseStatusException.class);
        verify(client).send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());
        assertThat(esperas).isEmpty();
    }

    @Test
    void timeoutNaoRepetePedidoQuePodeTerSidoProcessado() throws Exception {
        HttpClient client = mock(HttpClient.class);
        List<Duration> esperas = new ArrayList<>();
        GeminiService gemini = new GeminiService("chave-de-teste", "gemini-3.6-flash", "", new ObjectMapper(), client, esperas::add);
        when(client.send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenThrow(new HttpTimeoutException("timeout de teste"));
        assertThatThrownBy(() -> gemini.gerar("teste", "ficticio", GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class))
                .isInstanceOf(ResponseStatusException.class);
        verify(client).send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());
        assertThat(esperas).isEmpty();
    }

    private GeminiService comFallback(HttpClient client, List<Duration> esperas) {
        return new GeminiService("chave-de-teste", "gemini-3.5-flash-lite", " models/gemini-3.1-flash-lite ",
                new ObjectMapper(), client, esperas::add);
    }

    private List<HttpRequest> requests(HttpClient client, int quantidade) throws Exception {
        ArgumentCaptor<HttpRequest> captor = org.mockito.ArgumentCaptor.forClass(HttpRequest.class);
        verify(client, times(quantidade)).send(captor.capture(), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());
        return captor.getAllValues();
    }

    private void modelos(List<HttpRequest> requests, String... nomes) {
        assertThat(requests).extracting(request -> request.uri().getPath())
                .containsExactly(Arrays.stream(nomes).map(nome -> "/v1beta/models/" + nome + ":generateContent").toArray(String[]::new));
    }

    private String corpo(HttpRequest request) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        CompletableFuture<String> resultado = new CompletableFuture<String>();
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

    @Test
    void sucessoNoPrincipalNaoChamaFallback() throws Exception {
        HttpClient client = mock(HttpClient.class);
        List<Duration> esperas = new ArrayList<>();
        GeminiService gemini = comFallback(client, esperas);
        doReturn(sucessoGoogle()).when(client).send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());

        assertThat(gemini.gerar("teste", "ficticio", GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class).texto()).isEqualTo("ok");
        modelos(requests(client, 1), "gemini-3.5-flash-lite");
        assertThat(esperas).isEmpty();
    }

    @Test
    void primeiro503TrocaDeModeloSemAguardarRetryAfterDoPrincipal() throws Exception {
        HttpClient client = mock(HttpClient.class);
        List<Duration> esperas = new ArrayList<>();
        GeminiService gemini = comFallback(client, esperas);
        doReturn(resposta(503, "{}", Map.of("Retry-After", List.of("120"))), sucessoGoogle())
                .when(client).send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());

        assertThat(gemini.gerar("instrucao de teste", Map.of("relato", "ficticio"), GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class).texto())
                .isEqualTo("ok");
        List<HttpRequest> enviadas = requests(client, 2);
        modelos(enviadas, "gemini-3.5-flash-lite", "gemini-3.1-flash-lite");
        assertThat(corpo(enviadas.get(1))).isEqualTo(corpo(enviadas.get(0)));
        assertThat(enviadas).allSatisfy(request -> {
            assertThat(request.headers().firstValue("x-goog-api-key")).contains("chave-de-teste");
            assertThat(request.timeout().orElseThrow()).isLessThanOrEqualTo(Duration.ofSeconds(30));
        });
        assertThat(esperas).isEmpty();
    }

    @Test
    void fallbackFuncionaNaTranscricaoPreservandoAudioESchema() throws Exception {
        HttpClient client = mock(HttpClient.class);
        GeminiService gemini = comFallback(client, new ArrayList<>());
        doReturn(indisponivelGoogle(), sucessoGoogle()).when(client)
                .send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());

        assertThat(gemini.transcrever(new byte[]{1, 2, 3}, "audio/wav")).isEqualTo("ok");
        List<HttpRequest> enviadas = requests(client, 2);
        modelos(enviadas, "gemini-3.5-flash-lite", "gemini-3.1-flash-lite");
        assertThat(corpo(enviadas.get(1))).isEqualTo(corpo(enviadas.get(0)));
        GeminiRequest payload = new ObjectMapper().readValue(corpo(enviadas.get(1)), GeminiRequest.class);
        GeminiRequest.Audio audio = payload.contents().getFirst().parts().getFirst().inlineData();
        assertThat(audio.mimeType()).isEqualTo("audio/wav");
        assertThat(audio.data()).isEqualTo("AQID");
        assertThat(payload.generationConfig().responseSchema().required()).containsExactly("texto");
    }

    @Test
    void seFallbackTambemRetorna503RepeteSomenteFallbackDentroDoLimiteGlobal() throws Exception {
        HttpClient client = mock(HttpClient.class);
        List<Duration> esperas = new ArrayList<>();
        GeminiService gemini = comFallback(client, esperas);
        doReturn(indisponivelGoogle(), indisponivelGoogle(), sucessoGoogle()).when(client)
                .send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());

        assertThat(gemini.gerar("teste", "ficticio", GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class).texto()).isEqualTo("ok");
        modelos(requests(client, 3), "gemini-3.5-flash-lite", "gemini-3.1-flash-lite", "gemini-3.1-flash-lite");
        assertThat(esperas).hasSize(1);
        assertThat(esperas.get(0).toMillis()).isBetween(1000L, 1249L);
    }

    @Test
    void doisModelosIndisponiveisEncerramCom503SemReiniciarTentativas() throws Exception {
        HttpClient client = mock(HttpClient.class);
        List<Duration> esperas = new ArrayList<>();
        GeminiService gemini = comFallback(client, esperas);
        doReturn(indisponivelGoogle()).when(client).send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());

        assertThatThrownBy(() -> gemini.gerar("teste", "ficticio", GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class))
                .isInstanceOf(ResponseStatusException.class);
        modelos(requests(client, 3), "gemini-3.5-flash-lite", "gemini-3.1-flash-lite", "gemini-3.1-flash-lite");
        assertThat(esperas).hasSize(1);
    }

    @Test
    void timeoutDoPrincipalTentaFallbackUmaVez() throws Exception {
        HttpClient client = mock(HttpClient.class);
        List<Duration> esperas = new ArrayList<>();
        GeminiService gemini = comFallback(client, esperas);
        HttpResponse<String> sucesso = sucessoGoogle();
        when(client.send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenThrow(new HttpTimeoutException("timeout de teste")).thenReturn(sucesso);

        assertThat(gemini.gerar("teste", "ficticio", GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class).texto()).isEqualTo("ok");
        modelos(requests(client, 2), "gemini-3.5-flash-lite", "gemini-3.1-flash-lite");
        assertThat(esperas).isEmpty();
    }

    @Test
    void timeoutDoFallbackEncerraSemRepetirOuVoltarAoPrincipal() throws Exception {
        HttpClient client = mock(HttpClient.class);
        List<Duration> esperas = new ArrayList<>();
        GeminiService gemini = comFallback(client, esperas);
        when(client.send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenThrow(new HttpTimeoutException("timeout de teste"));

        assertThatThrownBy(() -> gemini.gerar("teste", "ficticio", GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class))
                .isInstanceOf(ResponseStatusException.class);
        modelos(requests(client, 2), "gemini-3.5-flash-lite", "gemini-3.1-flash-lite");
        assertThat(esperas).isEmpty();
    }

    @Test
    void errosDeConfiguracaoCotaEConteudoNaoAcionamFallback() throws Exception {
        List<HttpResponse<String>> respostas = List.of(resposta(400, "{}", Map.of()), resposta(401, "{}", Map.of()),
                resposta(403, "{}", Map.of()), resposta(404, "{}", Map.of()), resposta(429, "{}", Map.of()),
                resposta(200, "{\"candidates\":[{\"finishReason\":\"SAFETY\"}]}", Map.of()),
                resposta(200, "JSON invalido", Map.of()));
        for (HttpResponse<String> response : respostas) {
            HttpClient client = mock(HttpClient.class);
            List<Duration> esperas = new ArrayList<>();
            GeminiService gemini = comFallback(client, esperas);
            doReturn(response).when(client).send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());

            assertThatThrownBy(() -> gemini.gerar("teste", "ficticio", GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class))
                    .isInstanceOf(ResponseStatusException.class);
            modelos(requests(client, 1), "gemini-3.5-flash-lite");
            assertThat(esperas).isEmpty();
        }
    }

    @Test
    void erroDeCotaNoFallbackEncerraSemTerceiraChamada() throws Exception {
        HttpClient client = mock(HttpClient.class);
        GeminiService gemini = comFallback(client, new ArrayList<>());
        doReturn(indisponivelGoogle(), resposta(429, "{}", Map.of())).when(client)
                .send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());

        assertThatThrownBy(() -> gemini.gerar("teste", "ficticio", GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class))
                .isInstanceOf(ResponseStatusException.class);
        modelos(requests(client, 2), "gemini-3.5-flash-lite", "gemini-3.1-flash-lite");
    }

    @Test
    void modeloFallbackIgualAoPrincipalMantemRetentativasSemDuplicarCadeia() throws Exception {
        HttpClient client = mock(HttpClient.class);
        List<Duration> esperas = new ArrayList<>();
        GeminiService gemini = new GeminiService("chave-de-teste", "gemini-3.5-flash-lite", "models/gemini-3.5-flash-lite",
                new ObjectMapper(), client, esperas::add);
        doReturn(indisponivelGoogle()).when(client).send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());

        assertThatThrownBy(() -> gemini.gerar("teste", "ficticio", GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class))
                .isInstanceOf(ResponseStatusException.class);
        modelos(requests(client, 3), "gemini-3.5-flash-lite", "gemini-3.5-flash-lite", "gemini-3.5-flash-lite");
        assertThat(esperas).hasSize(2);
    }

    @Test
    void umaTentativaConfiguradaImpedeChamadasAdicionaisAoFallback() throws Exception {
        HttpClient client = mock(HttpClient.class);
        List<Duration> esperas = new ArrayList<>();
        GeminiService gemini = comFallback(client, esperas);
        ReflectionTestUtils.setField(gemini, "maxTentativas", 1);
        doReturn(indisponivelGoogle()).when(client).send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());

        assertThatThrownBy(() -> gemini.gerar("teste", "ficticio", GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class))
                .isInstanceOf(ResponseStatusException.class);
        modelos(requests(client, 1), "gemini-3.5-flash-lite");
        assertThat(esperas).isEmpty();
    }

    @Test
    void fallbackCompartilhaPrazoTotalIncluindoEsperas() throws Exception {
        HttpClient client = mock(HttpClient.class);
        AtomicLong tempo = new AtomicLong();
        AtomicInteger chamadas = new AtomicInteger();
        List<Duration> esperas = new ArrayList<>();
        GeminiService gemini = new GeminiService("chave-de-teste", "gemini-3.5-flash-lite", "gemini-3.1-flash-lite",
                new ObjectMapper(), client, atraso -> {
                    esperas.add(atraso);
                    tempo.addAndGet(atraso.toNanos());
                }, tempo::get);
        ReflectionTestUtils.setField(gemini, "timeoutSegundos", 60);
        HttpResponse<String> indisponivel = indisponivelGoogle();
        HttpResponse<String> sucesso = sucessoGoogle();
        doAnswer(invocation -> {
            if (chamadas.incrementAndGet() <= 2) {
                tempo.addAndGet(Duration.ofSeconds(20).toNanos());
                return indisponivel;
            }
            return sucesso;
        }).when(client).send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());

        assertThat(gemini.gerar("teste", "ficticio", GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class).texto()).isEqualTo("ok");
        List<HttpRequest> enviadas = requests(client, 3);
        assertThat(enviadas.get(0).timeout()).contains(Duration.ofSeconds(20));
        assertThat(enviadas.get(1).timeout()).contains(Duration.ofSeconds(20));
        assertThat(esperas).hasSize(1);
        assertThat(enviadas.get(2).timeout()).contains(Duration.ofSeconds(20).minus(esperas.get(0)));
    }

    @Test
    void naoChamaFallbackQuandoPrazoTotalJaEsgotou() throws Exception {
        HttpClient client = mock(HttpClient.class);
        AtomicLong tempo = new AtomicLong();
        GeminiService gemini = new GeminiService("chave-de-teste", "gemini-3.5-flash-lite", "gemini-3.1-flash-lite",
                new ObjectMapper(), client, atraso -> tempo.addAndGet(atraso.toNanos()), tempo::get);
        doAnswer(invocation -> {
            tempo.addAndGet(Duration.ofSeconds(90).toNanos());
            throw new HttpTimeoutException("timeout de teste");
        }).when(client).send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());

        assertThatThrownBy(() -> gemini.gerar("teste", "ficticio", GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class))
                .isInstanceOf(ResponseStatusException.class);
        modelos(requests(client, 1), "gemini-3.5-flash-lite");
    }

    @Test
    void resposta503DepoisDoPrazoEncerraSemTentarOutroModelo() throws Exception {
        HttpClient client = mock(HttpClient.class);
        AtomicLong tempo = new AtomicLong();
        GeminiService gemini = new GeminiService("chave-de-teste", "gemini-3.5-flash-lite", "gemini-3.1-flash-lite",
                new ObjectMapper(), client, atraso -> tempo.addAndGet(atraso.toNanos()), tempo::get);
        HttpResponse<String> response = indisponivelGoogle();
        doAnswer(invocation -> {
            tempo.addAndGet(Duration.ofSeconds(90).toNanos());
            return response;
        }).when(client).send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());

        assertThatThrownBy(() -> gemini.gerar("teste", "ficticio", GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class))
                .isInstanceOf(ResponseStatusException.class);
        modelos(requests(client, 1), "gemini-3.5-flash-lite");
    }

    @Test
    void rejeitaTiposIncompativeisComODtoSemAcionarFallback() throws Exception {
        for (String conteudo : List.of("{\"texto\":123}", "{\"texto\":true}", "{\"texto\":{}}")) {
            HttpClient client = mock(HttpClient.class);
            GeminiService gemini = comFallback(client, new ArrayList<>());
            GeminiResponse envelope = new GeminiResponse(List.of(new GeminiResponse.Candidato("STOP",
                    new GeminiResponse.Conteudo(List.of(new GeminiResponse.Parte(conteudo, false))))), null);
            doReturn(resposta(200, new ObjectMapper().writeValueAsString(envelope), Map.of())).when(client)
                    .send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());

            assertThatThrownBy(() -> gemini.gerar("teste", "ficticio", GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class))
                    .isInstanceOf(ResponseStatusException.class)
                    .satisfies(erro -> assertThat(((ResponseStatusException) erro).getStatusCode().value()).isEqualTo(503));
            modelos(requests(client, 1), "gemini-3.5-flash-lite");
        }
    }

    @Test
    void rejeitaNumerosNaListaDePerguntasEmVezDeConverterParaTexto() throws Exception {
        HttpClient client = mock(HttpClient.class);
        GeminiService gemini = comFallback(client, new ArrayList<>());
        GeminiResponse envelope = new GeminiResponse(List.of(new GeminiResponse.Candidato("STOP",
                new GeminiResponse.Conteudo(List.of(new GeminiResponse.Parte("{\"perguntas\":[\"Como foi?\",123,\"O que precisa?\"]}", false))))), null);
        doReturn(resposta(200, new ObjectMapper().writeValueAsString(envelope), Map.of())).when(client)
                .send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());

        assertThatThrownBy(() -> gemini.gerar("teste", "ficticio", ApoioEmocionalConteudo.SCHEMA_PERGUNTAS,
                PerguntasGeradasResponse.class)).isInstanceOf(ResponseStatusException.class);
        modelos(requests(client, 1), "gemini-3.5-flash-lite");
    }

    @Test
    void rejeitaEnvelopeSemCandidatoOuConteudo() throws Exception {
        for (String corpo : List.of("null", "{}", "{\"candidates\":[]}", "{\"candidates\":[null]}",
                "{\"candidates\":[{\"finishReason\":\"STOP\"}]}")) {
            HttpClient client = mock(HttpClient.class);
            GeminiService gemini = comFallback(client, new ArrayList<>());
            doReturn(resposta(200, corpo, Map.of())).when(client)
                    .send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());
            assertThatThrownBy(() -> gemini.gerar("teste", "ficticio", GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class))
                    .isInstanceOf(ResponseStatusException.class);
            modelos(requests(client, 1), "gemini-3.5-flash-lite");
        }
    }

    @Test
    void errosDoProvedorNaoExpoemMensagemOuCredenciaisAoUsuario() throws Exception {
        for (String corpo : List.of(
                "{\"error\":{\"status\":\"RESOURCE_EXHAUSTED\",\"message\":\"relato-privado segredo-api\"}}",
                "{\"error\":{\"status\":\"valor-arbitrario\",\"message\":\"relato-privado\"}}",
                "resposta-invalida segredo-api", "null")) {
            HttpClient client = mock(HttpClient.class);
            GeminiService gemini = comFallback(client, new ArrayList<>());
            doReturn(resposta(403, corpo, Map.of())).when(client)
                    .send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());

            assertThatThrownBy(() -> gemini.gerar("teste", "ficticio", GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageNotContaining("relato-privado").hasMessageNotContaining("segredo-api")
                    .satisfies(erro -> assertThat(((ResponseStatusException) erro).getStatusCode().value()).isEqualTo(503));
            modelos(requests(client, 1), "gemini-3.5-flash-lite");
        }
    }

    @Test
    void rejeitaConclusaoDiferenteDeStopSemExporSeuConteudo() throws Exception {
        for (String motivo : List.of("SAFETY", "MAX_TOKENS", "RECITATION", "segredo-api")) {
            HttpClient client = mock(HttpClient.class);
            GeminiService gemini = comFallback(client, new ArrayList<>());
            GeminiResponse envelope = new GeminiResponse(List.of(new GeminiResponse.Candidato(motivo,
                    new GeminiResponse.Conteudo(List.of(new GeminiResponse.Parte("relato-privado", false))))), null);
            doReturn(resposta(200, new ObjectMapper().writeValueAsString(envelope), Map.of())).when(client)
                    .send(any(HttpRequest.class), org.mockito.ArgumentMatchers.<HttpResponse.BodyHandler<String>>any());

            assertThatThrownBy(() -> gemini.gerar("teste", "ficticio", GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageNotContaining("relato-privado").hasMessageNotContaining("segredo-api");
            modelos(requests(client, 1), "gemini-3.5-flash-lite");
        }
    }

}
