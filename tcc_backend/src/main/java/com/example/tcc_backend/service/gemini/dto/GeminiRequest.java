package com.example.tcc_backend.service.gemini.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public record GeminiRequest(Instrucao systemInstruction, List<Conteudo> contents,
                            Configuracao generationConfig) {
    public record Instrucao(List<Parte> parts) {}

    public record Conteudo(String role, List<Parte> parts) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Parte(String text, Audio inlineData) {
        public static Parte texto(String texto) {
            return new Parte(texto, null);
        }

        public static Parte audio(String mime, String dados) {
            return new Parte(null, new Audio(mime, dados));
        }
    }

    public record Audio(String mimeType, String data) {}

    public record Configuracao(String responseMimeType, GeminiSchema responseSchema, int maxOutputTokens) {}
}
