package com.example.tcc_backend.validator;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class ContatoEmergenciaValidator {

    private static final int LIMITE_CONTATOS = 10;

    private ContatoEmergenciaValidator() {}

    public static void validarLimite(long quantidadeAtual) {
        if (quantidadeAtual >= LIMITE_CONTATOS) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O limite de 10 contatos foi atingido");
        }
    }

    public static void validarTelefone(String telefone) {
        String digitos = telefone == null ? "" : telefone.replaceAll("\\D", "");

        if (digitos.length() < 8 || digitos.length() > 15) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone inválido");
        }
    }

}
