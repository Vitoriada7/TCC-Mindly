package com.example.tcc_backend.validator;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.example.tcc_backend.service.gemini.dto.GeminiResponse;
import com.example.tcc_backend.service.gemini.dto.TextoGeradoResponse;

import java.net.http.HttpResponse;
import java.util.Objects;

public final class GeminiValidator {

    private GeminiValidator() {}

    public static String normalizarModelo(String valor) {
        String nome = Objects.requireNonNull(valor, "Configure gemini.model").strip();

        if (nome.length() >= 2 && ((nome.startsWith("\"") && nome.endsWith("\""))
                || (nome.startsWith("'") && nome.endsWith("'")))) {
            nome = nome.substring(1, nome.length() - 1).strip();
        }

        if (nome.startsWith("models/"))
            nome = nome.substring("models/".length());
        if (!nome.matches("[A-Za-z0-9][A-Za-z0-9._-]*")) {
            throw new IllegalArgumentException("Configure GEMINI_MODEL com um nome de modelo, sem URL ou parametros.");
        }

        return nome;
    }

    public static String validarTexto(TextoGeradoResponse resultado, int limite, boolean permiteVazio) {
        String texto = resultado == null ? null : resultado.texto();

        if (texto == null || texto.length() > limite || (!permiteVazio && texto.isBlank())) {
            throw indisponivel();
        }

        return texto.trim();
    }

    public static void validarRespostaHttp(HttpResponse<String> response) {
        if (response.statusCode() != 200) {
            throw indisponivel();
        }
    }

    public static String extrairTextoCandidato(GeminiResponse resposta) {
        if (resposta == null || resposta.candidates() == null || resposta.candidates().isEmpty()
                || resposta.candidates().getFirst() == null) {
            throw indisponivel();
        }

        GeminiResponse.Candidato candidato = resposta.candidates().getFirst();
        String motivo = candidato.finishReason();

        if (!"STOP".equals(motivo)) {
            throw indisponivel();
        }

        if (candidato.content() == null || candidato.content().parts() == null) {
            throw indisponivel();
        }

        StringBuilder texto = new StringBuilder();

        for (GeminiResponse.Parte parte : candidato.content().parts()) {
            if (parte != null && !Boolean.TRUE.equals(parte.thought()) && parte.text() != null) {
                texto.append(parte.text());
            }
        }

        return texto.toString();
    }

    public static <R> R validarResultado(R resultado) {
        if (resultado == null) {
            throw indisponivel();
        }

        return resultado;
    }

    public static void validarChave(String chave) {
        if (chave.isBlank()) {
            throw indisponivel();
        }
    }

    public static ResponseStatusException indisponivel() {
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "A IA está indisponível no momento. Tente novamente ou continue sem IA.");
    }

}
