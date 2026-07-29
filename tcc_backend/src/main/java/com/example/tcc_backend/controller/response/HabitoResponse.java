package com.example.tcc_backend.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class HabitoResponse {
    private Long id;
    private String nome;
    private String icone;
    private String cor;
    private List<LocalDate> diasConcluidos;
}
