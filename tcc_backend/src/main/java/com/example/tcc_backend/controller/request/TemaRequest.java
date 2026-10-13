package com.example.tcc_backend.controller.request;

import jakarta.validation.constraints.NotNull;

public record TemaRequest(@NotNull Boolean temaEscuro) {
}
