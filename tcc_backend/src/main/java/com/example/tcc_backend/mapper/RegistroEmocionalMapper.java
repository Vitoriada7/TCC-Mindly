package com.example.tcc_backend.mapper;

import com.example.tcc_backend.controller.request.RegistroEmocionalRequest;
import com.example.tcc_backend.controller.response.RegistroEmocionalResponse;
import com.example.tcc_backend.domain.RegistroEmocional;

public final class RegistroEmocionalMapper {

    private static final String SEPARADOR_EXPLORACOES = "\n\u001e\n";

    private RegistroEmocionalMapper() { }

    public static RegistroEmocionalResponse toResponse(RegistroEmocional registro) {
        return new RegistroEmocionalResponse(registro.getId(), registro.getSentimento(),
                registro.getSentimentoDetalhado(), registro.getPensamento(), registro.getReflexao(),
                registro.getComplementoFinal(), registro.getDataRegistro(),
                registro.getExploracoes() == null || registro.getExploracoes().isBlank()
                        ? java.util.List.of()
                        : java.util.List.of(registro.getExploracoes().split(SEPARADOR_EXPLORACOES, -1)));
    }

    public static void atualizarEntidade(RegistroEmocional registro, RegistroEmocionalRequest request) {
        registro.setSentimento(request.getSentimento().trim());
        registro.setSentimentoDetalhado(normalizar(request.getSentimentoDetalhado()));
        registro.setPensamento(normalizar(request.getPensamento()));
        registro.setReflexao(normalizar(request.getReflexao()));
        registro.setComplementoFinal(normalizar(request.getComplementoFinal()));
        registro.setExploracoes(request.getExploracoes() == null ? null
                : String.join(SEPARADOR_EXPLORACOES, request.getExploracoes()));
    }

    private static String normalizar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

}
