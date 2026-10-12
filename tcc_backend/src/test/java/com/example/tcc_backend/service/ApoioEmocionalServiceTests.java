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
    void analiseVocalRetornaObservacoesValidas() {
        var audio = new MockMultipartFile("audio", "relato.webm", "audio/webm;codecs=opus", new byte[]{1});
        var resultado = mapper.readTree("{\"texto\":\"Meu dia foi difícil.\",\"analiseDisponivel\":true,\"observacoesVocais\":[\"Sua voz parece pausada.\"]}");
        when(gemini.analisarVoz(any(byte[].class), eq("audio/webm"))).thenReturn(resultado);
        when(gemini.texto(resultado, 10000, true)).thenReturn("Meu dia foi difícil.");
        var resposta = service.analisarVoz(audio);
        assertThat(resposta.analiseDisponivel()).isTrue();
        assertThat(resposta.observacoesVocais()).containsExactly("Sua voz parece pausada.");
    }

    @Test
    void preservaTranscricaoQuandoObservacaoForInvalida() {
        var audio = new MockMultipartFile("audio", "relato.webm", "audio/webm", new byte[]{1});
        var resultado = mapper.readTree("{\"analiseDisponivel\":true,\"observacoesVocais\":[123]}");
        when(gemini.analisarVoz(any(byte[].class), anyString())).thenReturn(resultado);
        when(gemini.texto(resultado, 10000, true)).thenReturn("Relato compreensível.");
        var resposta = service.analisarVoz(audio);
        assertThat(resposta.texto()).isEqualTo("Relato compreensível.");
        assertThat(resposta.analiseDisponivel()).isFalse();
        assertThat(resposta.observacoesVocais()).isEmpty();
    }

    @Test
    void silencioNaoProduzObservacoesMesmoQuandoModeloAsRetorna() {
        var audio = new MockMultipartFile("audio", "relato.wav", "audio/wav", new byte[]{1});
        var resultado = mapper.readTree("{\"analiseDisponivel\":true,\"observacoesVocais\":[\"Voz calma.\"]}");
        when(gemini.analisarVoz(any(byte[].class), anyString())).thenReturn(resultado);
        when(gemini.texto(resultado, 10000, true)).thenReturn("");
        assertThat(service.analisarVoz(audio).observacoesVocais()).isEmpty();
    }

    @Test
    void analiseRejeitaAudioInvalidoAntesDeChamarGemini() {
        var audio = new MockMultipartFile("audio", "arquivo.txt", "text/plain", new byte[]{1});
        assertThatThrownBy(() -> service.analisarVoz(audio)).isInstanceOf(ResponseStatusException.class);
        verifyNoInteractions(gemini);
    }
}
