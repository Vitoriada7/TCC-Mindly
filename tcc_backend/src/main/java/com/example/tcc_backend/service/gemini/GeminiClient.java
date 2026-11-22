package com.example.tcc_backend.service.gemini;

import com.example.tcc_backend.validator.GeminiValidator;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.LongSupplier;

public final class GeminiClient {

    private final String chave;

    private final String modelo;

    private final String modeloFallback;

    private final HttpClient cliente;

    private final Espera espera;

    private final LongSupplier tempoNanos;

    public record RespostaGemini(String modelo, HttpResponse<String> response) {}

    @FunctionalInterface
    public interface Espera {
        void aguardar(Duration duracao) throws InterruptedException;
    }

    public GeminiClient(String chave, String modelo, String modeloFallback, HttpClient cliente,
                        Espera espera, LongSupplier tempoNanos) {
        this.chave = chave;
        this.modelo = GeminiValidator.normalizarModelo(modelo);
        String nomeFallback = modeloFallback.isBlank() ? "" : GeminiValidator.normalizarModelo(modeloFallback);
        this.modeloFallback = nomeFallback.equals(this.modelo) ? "" : nomeFallback;
        this.cliente = cliente;
        this.espera = espera;
        this.tempoNanos = tempoNanos;
    }

    private static URI endpoint(String modelo) {
        return URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + modelo + ":generateContent");
    }

    public RespostaGemini enviar(String corpo, int timeoutSegundos, int maxTentativas) throws java.io.IOException, InterruptedException {
        int limite = Math.max(1, Math.min(3, maxTentativas));
        long prazo = tempoNanos.getAsLong() + Duration.ofSeconds(timeoutSegundos).toNanos();
        Duration limitePorTentativa = Duration.ofSeconds(timeoutSegundos).dividedBy(limite);
        String modeloAtual = modelo;
        int tentativaDoModelo = 0;

        for (int tentativa = 1; tentativa <= limite; tentativa++) {
            long restante = prazo - tempoNanos.getAsLong();

            if (restante <= 0)
                throw new HttpTimeoutException("Prazo total da IA esgotado");

            Duration timeout = Duration.ofNanos(Math.min(restante, limitePorTentativa.toNanos()));
            HttpRequest request = HttpRequest.newBuilder(endpoint(modeloAtual))
                    .timeout(timeout).header("Content-Type", "application/json")
                    .header("x-goog-api-key", chave).POST(HttpRequest.BodyPublishers.ofString(corpo)).build();
            tentativaDoModelo++;
            HttpResponse<String> response;

            try {
                response = cliente.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (HttpTimeoutException e) {
                if (!podeUsarFallback(modeloAtual) || tentativa == limite || prazo - tempoNanos.getAsLong() <= 0)
                    throw e;

                modeloAtual = modeloFallback;
                tentativaDoModelo = 0;
                continue;
            }

            if (response.statusCode() != 503 || tentativa == limite || prazo - tempoNanos.getAsLong() <= 0) {
                return new RespostaGemini(modeloAtual, response);
            }

            if (podeUsarFallback(modeloAtual)) {
                modeloAtual = modeloFallback;
                tentativaDoModelo = 0;
                continue;
            }

            int atrasoMillis = (1 << (tentativaDoModelo - 1)) * 1000 + ThreadLocalRandom.current().nextInt(250);
            long recomendado = retryAfterMillis(response, timeoutSegundos);
            Duration atraso = Duration.ofMillis(Math.max(atrasoMillis, recomendado));

            if (prazo - tempoNanos.getAsLong() <= atraso.toNanos())
                return new RespostaGemini(modeloAtual, response);

            espera.aguardar(atraso);
        }

        throw new IllegalStateException("Nenhuma tentativa de IA executada");
    }

    private boolean podeUsarFallback(String modeloAtual) {
        return !modeloFallback.isEmpty() && modeloAtual.equals(modelo);
    }

    private long retryAfterMillis(HttpResponse<String> response, int timeoutSegundos) {
        String valor = response.headers().firstValue("Retry-After").orElse("");

        if (valor.isBlank())
            return 0;

        try {
            long segundos = Long.parseLong(valor);
            return segundos <= 0 ? 0 : Math.multiplyExact(segundos, 1000);
        } catch (NumberFormatException e) {
            try {
                java.time.Instant instante = java.time.ZonedDateTime.parse(valor,
                        java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
                return Math.max(0, Duration.between(java.time.Instant.now(), instante).toMillis());
            } catch (java.time.DateTimeException e2) {
                return 0;
            }
        } catch (ArithmeticException e) {
            return Duration.ofSeconds(timeoutSegundos).toMillis();
        }
    }

}
