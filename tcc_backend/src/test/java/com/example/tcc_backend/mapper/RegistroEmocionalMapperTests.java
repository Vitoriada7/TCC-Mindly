package com.example.tcc_backend.mapper;

import com.example.tcc_backend.controller.response.RegistroEmocionalResponse;
import com.example.tcc_backend.controller.request.RegistroEmocionalRequest;
import com.example.tcc_backend.domain.RegistroEmocional;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegistroEmocionalMapperTests {

    @Test
    void devolvePerguntasERespostasPreservandoQuebrasDeLinha() {
        RegistroEmocional registro = RegistroEmocional.builder()
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

    @Test
    void atualizaRegistroPreservandoExploracoesMultilinhaEVazias() {
        RegistroEmocionalRequest request = new RegistroEmocionalRequest();
        request.setSentimento("  tranquilo  ");
        request.setPensamento("  ");
        request.setReflexao("  Um dia bom.  ");
        request.setExploracoes(java.util.List.of("Pergunta\nResposta\nOutra linha", ""));
        RegistroEmocional registro = new RegistroEmocional();

        RegistroEmocionalMapper.atualizarEntidade(registro, request);
        RegistroEmocionalResponse resposta = RegistroEmocionalMapper.toResponse(registro);

        assertThat(resposta.getSentimento()).isEqualTo("tranquilo");
        assertThat(resposta.getPensamento()).isNull();
        assertThat(resposta.getReflexao()).isEqualTo("Um dia bom.");
        assertThat(resposta.getExploracoes()).containsExactly("Pergunta\nResposta\nOutra linha", "");
    }

}
