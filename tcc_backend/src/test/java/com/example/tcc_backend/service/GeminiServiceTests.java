package com.example.tcc_backend.service;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import static org.assertj.core.api.Assertions.assertThat;

class GeminiServiceTests {
    private final GeminiService service = new GeminiService("", "gemini-3.5-flash-lite", new ObjectMapper());

    @Test
    void diagnosticoPreservaStatusSemExporMensagemDoGoogle() {
        assertThat(service.statusSeguro("{\"error\":{\"status\":\"RESOURCE_EXHAUSTED\",\"message\":\"conteudo privado\"}}"))
                .isEqualTo("RESOURCE_EXHAUSTED");
        assertThat(service.statusSeguro("{\"error\":{\"status\":\"PERMISSION_DENIED\"}}"))
                .isEqualTo("PERMISSION_DENIED");
    }

    @Test
    void diagnosticoNaoRegistraConteudoArbitrarioOuRespostaInvalida() {
        assertThat(service.statusSeguro("{\"error\":{\"status\":\"segredo ou relato privado\"}}"))
                .isEqualTo("DESCONHECIDO");
        assertThat(service.statusSeguro("resposta nao JSON")).isEqualTo("DESCONHECIDO");
        assertThat(service.statusSeguro("null")).isEqualTo("DESCONHECIDO");
    }
}
