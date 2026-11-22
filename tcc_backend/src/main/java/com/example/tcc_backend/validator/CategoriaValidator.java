package com.example.tcc_backend.validator;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class CategoriaValidator {

    private CategoriaValidator() {}

    public static String normalizarNome(String nome) {
        String nomeNormalizado = nome == null ? "" : nome.trim();

        if (nomeNormalizado.length() < 2 || nomeNormalizado.length() > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O nome da categoria deve possuir entre 2 e 50 caracteres");
        }

        return nomeNormalizado;
    }

    public static void validarExclusao(boolean possuiTarefas) {
        if (possuiTarefas) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Não é possível excluir uma categoria que possui tarefas vinculadas");
        }
    }

}
