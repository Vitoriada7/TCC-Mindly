package com.example.tcc_backend.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ResumoGamificacaoResponse {

    private final HabitosGamificacaoResponse habitos;

    private final TarefasGamificacaoResponse tarefas;

    private final EmocionalGamificacaoResponse emocional;

    private final List<ConquistaResponse> conquistas;

    @Getter
    @AllArgsConstructor
    public static class HabitosGamificacaoResponse {
        private final int sequenciaAtual;

        private final int melhorSequencia;

        private final long concluidosHoje;

        private final long metaHoje;
    }

    @Getter
    @AllArgsConstructor
    public static class TarefasGamificacaoResponse {
        private final int sequenciaSemanal;

        private final int concluidasSemana;

        private final int metaSemanal;

        private final int concluidasNoPrazoSemana;
    }

    @Getter
    @AllArgsConstructor
    public static class EmocionalGamificacaoResponse {
        private final int sequenciaAtual;

        private final int melhorSequencia;

        private final boolean registrouHoje;

        private final String sentimentoHoje;

        private final int totalRegistros;
    }

}
