package com.example.tcc_backend.mapper;

import com.example.tcc_backend.domain.RegistroEmocional;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegistroEmocionalMapperTests {
    @Test
    void devolvePerguntasERespostasPreservandoQuebrasDeLinha() {
        var registro = RegistroEmocional.builder()
                .exploracoes("Como foi?\nFoi bom.\nCom amigos.\n\u001e\nO que ajudou?\nDescansar.")
                .build();

        assertThat(RegistroEmocionalMapper.toResponse(registro).getExploracoes())
                .containsExactly("Como foi?\nFoi bom.\nCom amigos.", "O que ajudou?\nDescansar.");
    }

    @Test
    void devolveListaVaziaParaRegistroSemExploracoes() {
        assertThat(RegistroEmocionalMapper.toResponse(RegistroEmocional.builder().build())
                .getExploracoes()).isEmpty();
        assertThat(RegistroEmocionalMapper.toResponse(RegistroEmocional.builder()
                .exploracoes("").build()).getExploracoes()).isEmpty();
    }
}
