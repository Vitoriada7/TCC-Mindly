package com.example.tcc_backend.mapper;

import com.example.tcc_backend.controller.request.HabitoRequest;
import com.example.tcc_backend.controller.response.HabitoResponse;
import com.example.tcc_backend.domain.Habito;

import java.time.LocalDate;
import java.util.List;

public final class HabitoMapper {
    private HabitoMapper() { }

    public static Habito toEntity(HabitoRequest request) {
        Habito habito = new Habito();
        atualizarEntidade(habito, request);
        return habito;
    }

    public static void atualizarEntidade(Habito habito, HabitoRequest request) {
        habito.setNome(request.getNome().trim());
        habito.setIcone(request.getIcone().trim());
        habito.setCor(request.getCor().trim());
    }

    public static HabitoResponse toResponse(Habito habito, List<LocalDate> diasConcluidos) {
        HabitoResponse response = new HabitoResponse();
        response.setId(habito.getId());
        response.setNome(habito.getNome());
        response.setIcone(habito.getIcone());
        response.setCor(habito.getCor());
        response.setDiasConcluidos(diasConcluidos);
        return response;
    }
}
