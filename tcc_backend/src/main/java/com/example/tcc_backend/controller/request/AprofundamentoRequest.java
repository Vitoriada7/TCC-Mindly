package com.example.tcc_backend.controller.request;

import jakarta.validation.constraints.*;
import java.util.List;

public record AprofundamentoRequest(
        @NotBlank @Size(max = 40) String sentimento,
        @Size(max = 60) String sentimentoDetalhado,
        @NotBlank @Size(max = 10000) String pensamento,
        @Size(max = 3) List<@NotBlank @Size(max = 300) String> perguntas,
        @Size(max = 3) List<@NotNull @Size(max = 4000) String> respostas,
        @Size(max = 3) List<@NotBlank @Size(max = 200) String> observacoesVocais) {
    public AprofundamentoRequest(String sentimento, String sentimentoDetalhado, String pensamento,
                                List<String> perguntas, List<String> respostas) {
        this(sentimento, sentimentoDetalhado, pensamento, perguntas, respostas, null);
    }
}
