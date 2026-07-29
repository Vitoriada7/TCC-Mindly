package com.example.tcc_backend.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class CadastroRequest {

    @NotBlank
    @Size(max = 100)
    private String nome;

    @NotBlank
    @Size(max = 30)
    private String apelido;

    @NotBlank
    @Email
    @Size(max = 150)
    private String email;

    @NotBlank
    @Size(min = 6)
    private String senha;

    @NotNull
    private LocalDate dataNascimento;

}
