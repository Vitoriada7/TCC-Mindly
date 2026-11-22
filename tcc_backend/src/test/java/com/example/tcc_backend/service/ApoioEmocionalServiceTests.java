package com.example.tcc_backend.service;

import com.example.tcc_backend.service.gemini.GeminiConteudo;
import com.example.tcc_backend.service.gemini.dto.GeminiSchema;
import com.example.tcc_backend.service.gemini.dto.PerguntasGeradasResponse;
import com.example.tcc_backend.service.gemini.dto.TextoGeradoResponse;
import com.example.tcc_backend.validator.GeminiValidator;
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
        AprofundamentoRequest request = new AprofundamentoRequest("cansada", null, "Uma prova me deixou preocupado.", null, null);
        when(gemini.gerar(anyString(), any(), any(GeminiSchema.class), eq(PerguntasGeradasResponse.class))).thenReturn(new PerguntasGeradasResponse(List.of("Como foi?", "Como foi?", "O que precisa?")));
        assertThatThrownBy(() -> service.perguntas(request)).isInstanceOf(ResponseStatusException.class);
        when(gemini.gerar(anyString(), any(), any(GeminiSchema.class), eq(PerguntasGeradasResponse.class))).thenReturn(new PerguntasGeradasResponse(List.of("Como foi?")));
        assertThatThrownBy(() -> service.perguntas(request)).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void geraApoioMesmoSemRespostas() {
        AprofundamentoRequest request = new AprofundamentoRequest("cansada", null, "Estou sobrecarregado com as provas.", List.of("O que precisa?"), List.of(""));
        TextoGeradoResponse resposta = new TextoGeradoResponse("Uma pausa pode ajudar.");
        when(gemini.gerar(anyString(), eq(request), any(GeminiSchema.class), eq(TextoGeradoResponse.class))).thenReturn(resposta);
        assertThat(service.apoio(request).texto()).isEqualTo("Uma pausa pode ajudar.");
    }

    @Test
    void rejeitaUploadInvalidoSemChamarGemini() {
        MockMultipartFile audio = new MockMultipartFile("audio", "arquivo.txt", "text/plain", new byte[]{1});
        assertThatThrownBy(() -> service.transcrever(audio)).isInstanceOf(ResponseStatusException.class);
        verifyNoInteractions(gemini);
    }

    @Test
    void chaveAusenteERespostaVaziaGeramErroSeguro() {
        GeminiService cliente = new GeminiService("", "gemini-3.5-flash-lite", mapper);
        assertThatThrownBy(() -> cliente.gerar("apoio", "relato", GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class))
                .isInstanceOf(ResponseStatusException.class);
        assertThatThrownBy(() -> GeminiValidator.validarTexto(new TextoGeradoResponse(""), 650, false))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void transcreveAudioSemAnaliseVocal() throws Exception {
        MockMultipartFile audio = new MockMultipartFile("audio", "relato.webm", "audio/webm;codecs=opus", new byte[]{1});
        when(gemini.transcrever(any(byte[].class), eq("audio/webm"))).thenReturn("Meu relato.");
        assertThat(service.transcrever(audio).texto()).isEqualTo("Meu relato.");
        verify(gemini).transcrever(new byte[]{1}, "audio/webm");
        verifyNoMoreInteractions(gemini);
    }

    @Test
    void preservaTiposDeAudioAceitos() {
        for (String mime : List.of("audio/webm", "audio/mp4", "audio/ogg", "audio/wav", "audio/mpeg")) {
            MockMultipartFile audio = new MockMultipartFile("audio", "relato", mime + ";codecs=teste", new byte[]{1});
            when(gemini.transcrever(any(byte[].class), eq(mime))).thenReturn("Relato.");
            assertThat(service.transcrever(audio).texto()).isEqualTo("Relato.");
        }
    }

}
