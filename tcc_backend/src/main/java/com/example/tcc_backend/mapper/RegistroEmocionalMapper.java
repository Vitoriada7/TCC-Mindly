package com.example.tcc_backend.mapper;

import com.example.tcc_backend.controller.response.RegistroEmocionalResponse;
import com.example.tcc_backend.domain.RegistroEmocional;

public final class RegistroEmocionalMapper {
    private RegistroEmocionalMapper() { }

    public static RegistroEmocionalResponse toResponse(RegistroEmocional registro) {
        return new RegistroEmocionalResponse(registro.getId(), registro.getSentimento(),
                registro.getSentimentoDetalhado(), registro.getPensamento(), registro.getReflexao(),
                registro.getComplementoFinal(), registro.getDataRegistro(),
                registro.getExploracoes() == null || registro.getExploracoes().isBlank()
                        ? java.util.List.of()
                        : java.util.List.of(registro.getExploracoes().split("\n\u001e\n", -1)));
    }
}
