package com.example.tcc_backend.service.gemini.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeminiResponse(List<Candidato> candidates, Erro error) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Candidato(String finishReason, Conteudo content) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Conteudo(List<Parte> parts) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Parte(String text, Boolean thought) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Erro(String status, String message) {}
}
