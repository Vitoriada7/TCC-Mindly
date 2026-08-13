package com.example.tcc_backend.mapper;

import com.example.tcc_backend.controller.request.ContatoEmergenciaRequest;
import com.example.tcc_backend.controller.response.ContatoEmergenciaResponse;
import com.example.tcc_backend.domain.ContatoEmergencia;

public final class ContatoEmergenciaMapper {

    private ContatoEmergenciaMapper() { }

    public static ContatoEmergencia toEntity(ContatoEmergenciaRequest request) {
        return ContatoEmergencia.builder()
                .nome(request.getNome().trim())
                .telefone(normalizarTelefone(request.getTelefone()))
                .relacionamento(normalizarTextoOpcional(request.getRelacionamento()))
                .principal(request.isPrincipal())
                .build();
    }

    public static void atualizarEntidade(ContatoEmergencia contato, ContatoEmergenciaRequest request) {
        contato.setNome(request.getNome().trim());
        contato.setTelefone(normalizarTelefone(request.getTelefone()));
        contato.setRelacionamento(normalizarTextoOpcional(request.getRelacionamento()));
        contato.setPrincipal(request.isPrincipal());
    }

    public static ContatoEmergenciaResponse toResponse(ContatoEmergencia contato) {
        ContatoEmergenciaResponse response = new ContatoEmergenciaResponse();
        response.setId(contato.getId());
        response.setNome(contato.getNome());
        response.setTelefone(contato.getTelefone());
        response.setRelacionamento(contato.getRelacionamento());
        response.setPrincipal(Boolean.TRUE.equals(contato.getPrincipal()));
        return response;
    }

    private static String normalizarTelefone(String telefone) {
        String prefixo = telefone.trim().startsWith("+") ? "+" : "";
        return prefixo + telefone.replaceAll("\\D", "");
    }

    private static String normalizarTextoOpcional(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return texto.trim();
    }
}
