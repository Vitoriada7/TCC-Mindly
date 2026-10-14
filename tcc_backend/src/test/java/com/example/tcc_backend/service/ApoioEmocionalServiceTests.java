package com.example.tcc_backend.service;

import com.example.tcc_backend.controller.request.AprofundamentoRequest;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class ApoioEmocionalServiceTests {
    private final ObjectMapper mapper = new ObjectMapper();
    private final GeminiService gemini = mock(GeminiService.class);
    private final ApoioEmocionalService service = new ApoioEmocionalService(gemini);

    @Test
    void rejeitaPerguntasDuplicadasOuIncompletas() {
        var request = new AprofundamentoRequest("cansada", null, "Uma prova me deixou preocupado.", null, null);
        when(gemini.gerar(anyString(), any(), anyMap())).thenReturn(mapper.readTree("{\"perguntas\":[\"Como foi?\",\"Como foi?\",\"O que precisa?\"]}"));
        assertThatThrownBy(() -> service.perguntas(request)).isInstanceOf(ResponseStatusException.class);
        when(gemini.gerar(anyString(), any(), anyMap())).thenReturn(mapper.readTree("{\"perguntas\":[\"Como foi?\"]}"));
        assertThatThrownBy(() -> service.perguntas(request)).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void geraApoioMesmoSemRespostas() {
        var request = new AprofundamentoRequest("cansada", null, "Estou sobrecarregado com as provas.", List.of("O que precisa?"), List.of(""));
        var resposta = mapper.readTree("{\"texto\":\"Uma pausa pode ajudar.\"}");
        when(gemini.gerar(anyString(), eq(request), anyMap())).thenReturn(resposta);
        when(gemini.texto(resposta, 1800, false)).thenReturn("Uma pausa pode ajudar.");
        assertThat(service.apoio(request).texto()).isEqualTo("Uma pausa pode ajudar.");
    }

    @Test
    void rejeitaUploadInvalidoSemChamarGemini() {
        var audio = new MockMultipartFile("audio", "arquivo.txt", "text/plain", new byte[]{1});
        assertThatThrownBy(() -> service.transcrever(audio)).isInstanceOf(ResponseStatusException.class);
        verifyNoInteractions(gemini);
    }

    @Test
    void chaveAusenteERespostaVaziaGeramErroSeguro() {
        var cliente = new GeminiService("", "gemini-3.5-flash-lite", mapper);
        assertThatThrownBy(() -> cliente.gerar("apoio", "relato", GeminiService.schemaTexto()))
                .isInstanceOf(ResponseStatusException.class);
        assertThatThrownBy(() -> cliente.texto(mapper.readTree("{\"texto\":\"\"}"), 650, false))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void transcreveAudioSemAnaliseVocal() throws Exception {
        var audio = new MockMultipartFile("audio", "relato.webm", "audio/webm;codecs=opus", new byte[]{1});
        when(gemini.transcrever(any(byte[].class), eq("audio/webm"))).thenReturn("Meu relato.");
        assertThat(service.transcrever(audio).texto()).isEqualTo("Meu relato.");
        verify(gemini).transcrever(new byte[]{1}, "audio/webm");
        verifyNoMoreInteractions(gemini);
    }
}
