package com.example.tcc_backend.controller.response;

import java.time.Instant;

public record GoogleTasksEstadoResponse(boolean disponivel, boolean conectado, Instant ultimaSincronizacao) { }
