package com.example.tcc_backend.mapper;

import com.example.tcc_backend.controller.response.RegistroEmocionalResponse;
import com.example.tcc_backend.domain.RegistroEmocional;

public final class RegistroEmocionalMapper {
    private RegistroEmocionalMapper() { }

    public static RegistroEmocionalResponse toResponse(RegistroEmocional registro) {
        return new RegistroEmocionalResponse(registro.getId(), registro.getSentimento(),
                registro.getSentimentoDetalhado(), registro.getPensamento(), registro.getReflexao(),
                registro.getDataRegistro());
    }
}
