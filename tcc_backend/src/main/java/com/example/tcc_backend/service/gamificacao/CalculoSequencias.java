package com.example.tcc_backend.service.gamificacao;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public final class CalculoSequencias {

    private CalculoSequencias() {}

    public static int calcularSequenciaAtual(List<LocalDate> dias, LocalDate hoje) {
        List<LocalDate> diasOrdenados = dias.stream()
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();
        LocalDate referencia = diasOrdenados.contains(hoje) ? hoje : hoje.minusDays(1);
        int sequencia = 0;

        for (LocalDate dia : diasOrdenados) {
            if (dia.isAfter(referencia))
                continue;

            if (!dia.equals(referencia))
                break;

            sequencia++;
            referencia = referencia.minusDays(1);
        }

        return sequencia;
    }

    public static int calcularMelhorSequencia(List<LocalDate> dias) {
        List<LocalDate> diasOrdenados = dias.stream().distinct().sorted().toList();
        LocalDate anterior = null;
        int sequencia = 0;
        int melhor = 0;

        for (LocalDate dia : diasOrdenados) {
            sequencia = anterior != null && dia.equals(anterior.plusDays(1)) ? sequencia + 1 : 1;
            melhor = Math.max(melhor, sequencia);
            anterior = dia;
        }

        return melhor;
    }

}
