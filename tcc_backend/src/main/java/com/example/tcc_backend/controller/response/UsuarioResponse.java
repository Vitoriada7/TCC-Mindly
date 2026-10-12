package com.example.tcc_backend.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UsuarioResponse {

    private Long id;
    private com.example.tcc_backend.domain.enums.Avatar avatar;
    private java.util.Set<com.example.tcc_backend.domain.enums.Avatar> avataresLiberados;


    private String nome;

    private String apelido;

    private String email;

    private LocalDate dataNascimento;

    private LocalDateTime dataCriacao;

}
