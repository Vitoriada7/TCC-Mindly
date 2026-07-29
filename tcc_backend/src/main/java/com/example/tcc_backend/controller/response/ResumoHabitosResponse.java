package com.example.tcc_backend.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResumoHabitosResponse {
    private final long sequenciaAtual;
    private final long habitosConcluidosHoje;
    private final long totalHabitosAtivos;
}
