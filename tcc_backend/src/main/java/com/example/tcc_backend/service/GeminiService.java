package com.example.tcc_backend.service;

import com.example.tcc_backend.service.gemini.GeminiClient;
import com.example.tcc_backend.service.gemini.GeminiConteudo;
import com.example.tcc_backend.validator.GeminiValidator;
import com.example.tcc_backend.service.integracao.JsonIntegracao;
import com.example.tcc_backend.service.gemini.dto.GeminiRequest;
import com.example.tcc_backend.service.gemini.dto.GeminiResponse;
import com.example.tcc_backend.service.gemini.dto.GeminiSchema;
import com.example.tcc_backend.service.gemini.dto.TextoGeradoResponse;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.net.http.HttpResponse;
import java.util.function.LongSupplier;

@Service
public class GeminiService {

    @Value("${gemini.request-timeout-seconds:90}")
    private int timeoutSegundos = 90;

    @Value("${gemini.max-attempts:3}")
    private int maxTentativas = 3;

    private final String chave;

    private final ObjectMapper mapper;

    private final GeminiClient cliente;

    @Autowired
    public GeminiService(@Value("${gemini.api-key:}") String chave,
                         @Value("${gemini.model:gemini-3.5-flash-lite}") String modelo,
                         @Value("${gemini.fallback-model:gemini-3.1-flash-lite}") String modeloFallback,
                         ObjectMapper mapper) {
        this(chave, modelo, modeloFallback, mapper,
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build(), Thread::sleep);
    }

    public GeminiService(String chave, String modelo, ObjectMapper mapper) {
        this(chave, modelo, "gemini-3.1-flash-lite", mapper);
    }

    GeminiService(String chave, String modelo, String modeloFallback, ObjectMapper mapper, HttpClient cliente, GeminiClient.Espera espera) {
        this(chave, modelo, modeloFallback, mapper, cliente, espera, System::nanoTime);
    }

    GeminiService(String chave, String modelo, String modeloFallback, ObjectMapper mapper, HttpClient cliente,
                  GeminiClient.Espera espera, LongSupplier tempoNanos) {
        this.chave = chave;
        this.mapper = JsonIntegracao.mapperEstrito(mapper);
        this.cliente = new GeminiClient(chave, modelo, modeloFallback, cliente, espera, tempoNanos);
    }

    public <D, R> R gerar(String instrucao, D dados, GeminiSchema schema, Class<R> tipoResposta) {
        return chamar(instrucao, List.of(GeminiRequest.Parte.texto(mapper.writeValueAsString(dados))), schema, tipoResposta);
    }

    public String transcrever(byte[] audio, String mime) {
        TextoGeradoResponse resultado = chamar(GeminiConteudo.INSTRUCAO_TRANSCRICAO,
                List.of(GeminiRequest.Parte.audio(mime, Base64.getEncoder().encodeToString(audio))),
                GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class);
        return GeminiValidator.validarTexto(resultado, 10000, true);
    }

    private <R> R chamar(String instrucao, List<GeminiRequest.Parte> partes, GeminiSchema schema,
                         Class<R> tipoResposta) {
        GeminiValidator.validarChave(chave);

        try {
            GeminiRequest corpo = new GeminiRequest(
                    new GeminiRequest.Instrucao(List.of(GeminiRequest.Parte.texto(instrucao))),
                    List.of(new GeminiRequest.Conteudo("user", partes)),
                    new GeminiRequest.Configuracao("application/json", schema, 2048));

            GeminiClient.RespostaGemini resposta = cliente.enviar(mapper.writeValueAsString(corpo), timeoutSegundos, maxTentativas);
            HttpResponse<String> response = resposta.response();
            GeminiValidator.validarRespostaHttp(response);

            GeminiResponse conteudo = mapper.readValue(response.body(), GeminiResponse.class);
            String texto = GeminiValidator.extrairTextoCandidato(conteudo);

            R resultado = mapper.readValue(texto, tipoResposta);
            return GeminiValidator.validarResultado(resultado);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw GeminiValidator.indisponivel();
        } catch (Exception e) {
            throw GeminiValidator.indisponivel();
        }
    }

}
