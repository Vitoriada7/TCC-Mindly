package com.example.tcc_backend.service.gamificacao;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CalculoSequenciasTests {

    private final LocalDate hoje = LocalDate.of(2026, 11, 21);

    @Test
    void contaDiasDistintosMesmoForaDeOrdem() {
        List<LocalDate> dias = List.of(hoje.minusDays(2), hoje, hoje.minusDays(1), hoje, hoje.minusDays(2));
        assertThat(CalculoSequencias.calcularSequenciaAtual(dias, hoje)).isEqualTo(3);
        assertThat(CalculoSequencias.calcularMelhorSequencia(dias)).isEqualTo(3);
    }

    @Test
    void consideraOntemQuandoHojeAindaNaoFoiRegistradoEIgnoraFuturoNaSequenciaAtual() {
        List<LocalDate> dias = List.of(hoje.plusDays(2), hoje.minusDays(1), hoje.minusDays(2), hoje.minusDays(1));
        assertThat(CalculoSequencias.calcularSequenciaAtual(dias, hoje)).isEqualTo(2);
    }

    @Test
    void preservaMelhorSequenciaAnteriorAoIntervalo() {
        List<LocalDate> dias = List.of(hoje, hoje.minusDays(4), hoje.minusDays(3), hoje.minusDays(5));
        assertThat(CalculoSequencias.calcularSequenciaAtual(dias, hoje)).isEqualTo(1);
        assertThat(CalculoSequencias.calcularMelhorSequencia(dias)).isEqualTo(3);
    }

    @Test
    void retornaZeroSemRegistros() {
        assertThat(CalculoSequencias.calcularSequenciaAtual(List.of(), hoje)).isZero();
        assertThat(CalculoSequencias.calcularMelhorSequencia(List.of())).isZero();
    }

}
