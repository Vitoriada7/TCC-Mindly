package com.example.tcc_backend.service.gemini.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PerguntasGeradasResponse(List<String> perguntas) {}
