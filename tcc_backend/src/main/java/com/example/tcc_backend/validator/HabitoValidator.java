package com.example.tcc_backend.validator;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;

public final class HabitoValidator {

    private HabitoValidator() {}

    public static void validarDataRegistro(LocalDate data) {
        if (data == null || !data.equals(LocalDate.now(ZoneId.of("America/Sao_Paulo")))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Somente o dia atual pode ser marcado ou desmarcado");
        }
    }

    public static Periodo resolverPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio != null && fim != null && fim.isBefore(inicio)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A data final não pode ser anterior à data inicial");
        }

        if (inicio != null && fim != null) {
            return new Periodo(inicio, fim);
        }

        LocalDate referencia = inicio != null ? inicio : fim != null ? fim : LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        LocalDate inicioSemana = referencia.with(DayOfWeek.MONDAY);
        LocalDate fimSemana = inicioSemana.plusDays(6);
        return new Periodo(inicio != null ? inicio : inicioSemana, fim != null ? fim : fimSemana);
    }

    public record Periodo(LocalDate inicio, LocalDate fim) { }
}
