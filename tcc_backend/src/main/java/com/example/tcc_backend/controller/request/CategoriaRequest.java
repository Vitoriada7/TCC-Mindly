package com.example.tcc_backend.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoriaRequest {

    @NotBlank
    @Size(min = 2, max = 50)
    private String nome;
}
