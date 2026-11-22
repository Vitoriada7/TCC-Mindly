package com.example.tcc_backend.service.gemini.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TextoGeradoResponse(String texto) {}
