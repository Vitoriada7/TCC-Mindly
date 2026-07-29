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

    private String nome;

    private String apelido;

    private String email;

    private LocalDate dataNascimento;

    private LocalDateTime dataCriacao;

}
