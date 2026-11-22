package com.example.tcc_backend.validator;

import com.example.tcc_backend.domain.Tarefa;
import com.example.tcc_backend.domain.enums.StatusTarefa;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class TarefaValidator {

    private TarefaValidator() {}

    public static void validarConclusao(Tarefa tarefa) {
        if (tarefa.getStatus() == StatusTarefa.CONCLUIDA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A tarefa já está concluída");
        }
    }

}
