package com.example.tcc_backend.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RegistroEmocionalResponse {
    private final Long id;
    private final String sentimento;
    private final String sentimentoDetalhado;
    private final String pensamento;
    private final String reflexao;
    private final LocalDateTime dataRegistro;
}
