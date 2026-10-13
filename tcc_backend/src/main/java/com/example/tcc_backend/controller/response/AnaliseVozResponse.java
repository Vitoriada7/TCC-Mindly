package com.example.tcc_backend.controller.response;

import java.util.List;

public record AnaliseVozResponse(String texto, boolean analiseDisponivel, List<String> observacoesVocais) {}
