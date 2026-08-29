package com.example.tcc_backend.controller.response;

import com.example.tcc_backend.domain.enums.StatusConquista;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConquistaResponse {
    private final String codigo;
    private final String titulo;
    private final String descricao;
    private final String trilha;
    private final StatusConquista status;
    private final long progresso;
    private final long meta;
}
