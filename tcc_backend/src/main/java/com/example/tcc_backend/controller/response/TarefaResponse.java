package com.example.tcc_backend.controller.response;

import com.example.tcc_backend.domain.enums.Prioridade;
import com.example.tcc_backend.domain.enums.StatusTarefa;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TarefaResponse {

    private Long id;
    private String titulo;
    private String descricao;
    private StatusTarefa status;
    private Prioridade prioridade;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private LocalDateTime dataLimite;
    private LocalDateTime dataConclusao;
    private Long categoriaId;
    private Boolean vencida;
}
