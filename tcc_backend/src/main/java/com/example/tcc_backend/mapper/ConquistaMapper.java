package com.example.tcc_backend.mapper;

import com.example.tcc_backend.controller.response.ConquistaResponse;
import com.example.tcc_backend.domain.enums.Conquista;
import com.example.tcc_backend.domain.enums.StatusConquista;

public final class ConquistaMapper {

    private ConquistaMapper() {}

    public static ConquistaResponse toResponse(Conquista conquista, int progresso) {
        int meta = conquista.getMeta();
        StatusConquista status = progresso >= meta ? StatusConquista.CONCLUIDA
                : progresso > 0 ? StatusConquista.EM_PROGRESSO : StatusConquista.BLOQUEADA;
        return new ConquistaResponse(conquista.name(), conquista.getTitulo(), conquista.getDescricao(),
                conquista.getTrilha(), status, Math.min(progresso, meta), meta);
    }

}
