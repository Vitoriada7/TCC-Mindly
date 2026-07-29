package com.example.tcc_backend.mapper;

import com.example.tcc_backend.controller.request.TarefaRequest;
import com.example.tcc_backend.controller.response.TarefaResponse;
import com.example.tcc_backend.domain.Tarefa;
import com.example.tcc_backend.domain.enums.StatusTarefa;

import java.time.LocalDateTime;

public class TarefaMapper {

    private TarefaMapper() {
    }

    public static Tarefa toEntity(TarefaRequest request) {
        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(request.getTitulo());
        tarefa.setDescricao(request.getDescricao());
        tarefa.setPrioridade(request.getPrioridade());
        tarefa.setDataLimite(request.getDataLimite());
        return tarefa;
    }

    public static void atualizarEntidade(Tarefa tarefa, TarefaRequest request) {
        tarefa.setTitulo(request.getTitulo());
        tarefa.setDescricao(request.getDescricao());
        tarefa.setPrioridade(request.getPrioridade());
        tarefa.setDataLimite(request.getDataLimite());
    }

    public static TarefaResponse toResponse(Tarefa tarefa) {
        TarefaResponse response = new TarefaResponse();
        response.setId(tarefa.getId());
        response.setTitulo(tarefa.getTitulo());
        response.setDescricao(tarefa.getDescricao());
        response.setStatus(tarefa.getStatus());
        response.setPrioridade(tarefa.getPrioridade());
        response.setDataCriacao(tarefa.getDataCriacao());
        response.setDataAtualizacao(tarefa.getDataAtualizacao());
        response.setDataLimite(tarefa.getDataLimite());
        response.setDataConclusao(tarefa.getDataConclusao());
        response.setCategoriaId(tarefa.getCategoria().getId());
        response.setVencida(tarefa.getStatus() != StatusTarefa.CONCLUIDA
                && tarefa.getDataLimite() != null
                && tarefa.getDataLimite().isBefore(LocalDateTime.now()));
        return response;
    }
}
