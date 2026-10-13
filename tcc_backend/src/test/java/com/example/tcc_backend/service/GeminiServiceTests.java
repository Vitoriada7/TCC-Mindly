package com.example.tcc_backend.service;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GeminiServiceTests {
    private final GeminiService service = new GeminiService("", "gemini-3.5-flash-lite", new ObjectMapper());

    @Test
    void normalizaNomeCopiadoDaListaDeModelosOuDoPainel() {
        assertThat(GeminiService.normalizarModelo("gemini-2.5-flash")).isEqualTo("gemini-2.5-flash");
        assertThat(GeminiService.normalizarModelo(" models/gemini-2.5-flash\n")).isEqualTo("gemini-2.5-flash");
        assertThat(GeminiService.normalizarModelo("\"gemini-2.5-flash\"")).isEqualTo("gemini-2.5-flash");
        assertThat(GeminiService.normalizarModelo("'models/gemini-2.5-flash'"))
                .isEqualTo("gemini-2.5-flash");
    }

    @Test
    void rejeitaUrlsParametrosOuConfiguracaoVazia() {
        for (String valor : new String[]{"", " ", "models/", "models/models/gemini-2.5-flash",
                "https://outro-site.com", "gemini-2.5-flash?key=segredo", "gemini-2.5-flash:generateContent"}) {
            assertThatThrownBy(() -> GeminiService.normalizarModelo(valor))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageNotContaining(valor.isBlank() ? "conteudo privado" : valor);
        }
    }

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
