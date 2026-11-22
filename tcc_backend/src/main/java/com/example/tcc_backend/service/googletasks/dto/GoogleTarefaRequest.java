package com.example.tcc_backend.service.googletasks.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

public record GoogleTarefaRequest(@JsonInclude(JsonInclude.Include.NON_NULL) String id,
                                  String title, String notes, String status, String due, String completed) {
    public GoogleTarefaRequest comId(String novoId) {
        return new GoogleTarefaRequest(novoId, title, notes, status, due, completed);
    }
}
