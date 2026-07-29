package com.example.tcc_backend.controller.request;

import com.example.tcc_backend.domain.enums.Prioridade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TarefaRequest {

    @NotBlank
    @Size(max = 150)
    private String titulo;

    private String descricao;

    @NotNull
    private Prioridade prioridade;

    private LocalDateTime dataLimite;

    @NotNull
    private Long categoriaId;
}
