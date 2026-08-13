package com.example.tcc_backend.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContatoEmergenciaRequest {

    @NotBlank
    @Size(max = 100)
    private String nome;

    @NotBlank
    @Size(min = 8, max = 20)
    private String telefone;

    @Size(max = 50)
    private String relacionamento;

    private boolean principal;
}
