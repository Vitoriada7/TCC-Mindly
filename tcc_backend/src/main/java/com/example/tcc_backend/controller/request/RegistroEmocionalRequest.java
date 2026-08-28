package com.example.tcc_backend.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RegistroEmocionalRequest {
    @NotBlank
    @Size(max = 40)
    private String sentimento;

    @Size(max = 60)
    private String sentimentoDetalhado;

    @Size(max = 10000)
    private String pensamento;

    @Size(max = 3)
    private List<@Size(max = 4000) String> exploracoes;

    @Size(max = 10000)
    private String reflexao;
}
