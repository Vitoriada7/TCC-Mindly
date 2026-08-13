package com.example.tcc_backend.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContatoEmergenciaResponse {
    private Long id;
    private String nome;
    private String telefone;
    private String relacionamento;
    private boolean principal;
}
