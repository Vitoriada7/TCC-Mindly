package com.example.tcc_backend.mapper;

import com.example.tcc_backend.domain.Tarefa;
import com.example.tcc_backend.domain.enums.StatusTarefa;
import com.example.tcc_backend.service.googletasks.dto.GoogleTarefaRequest;

import java.time.ZoneId;

public final class GoogleTarefaMapper {

    private GoogleTarefaMapper() {}

    public static GoogleTarefaRequest toRequest(Tarefa tarefa) {
        return new GoogleTarefaRequest(null, tarefa.getTitulo(),
                tarefa.getDescricao() == null ? "" : tarefa.getDescricao(),
                tarefa.getStatus() == StatusTarefa.CONCLUIDA ? "completed" : "needsAction",
                tarefa.getDataLimite() == null ? null : tarefa.getDataLimite().toLocalDate() + "T00:00:00.000Z",
                tarefa.getDataConclusao() == null ? null
                        : tarefa.getDataConclusao().atZone(ZoneId.of("America/Sao_Paulo")).toInstant().toString());
    }

}
