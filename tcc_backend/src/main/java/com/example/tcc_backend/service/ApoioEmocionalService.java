package com.example.tcc_backend.service;

import com.example.tcc_backend.controller.request.AprofundamentoRequest;
import com.example.tcc_backend.service.apoioemocional.ApoioEmocionalConteudo;
import com.example.tcc_backend.service.gemini.GeminiConteudo;
import com.example.tcc_backend.validator.ApoioEmocionalValidator;
import com.example.tcc_backend.validator.GeminiValidator;
import com.example.tcc_backend.service.gemini.dto.PerguntasGeradasResponse;
import com.example.tcc_backend.service.gemini.dto.TextoGeradoResponse;
import com.example.tcc_backend.service.gemini.dto.RelatoGeminiRequest;
import com.example.tcc_backend.controller.response.*;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
public class ApoioEmocionalService {

    private final GeminiService gemini;

    public ApoioEmocionalService(GeminiService gemini) {
        this.gemini = gemini;
    }

    public PerguntasEmocionaisResponse perguntas(AprofundamentoRequest request) {
        RelatoGeminiRequest relato = new RelatoGeminiRequest(request.sentimento(),
                Objects.toString(request.sentimentoDetalhado(), ""), request.pensamento());
        PerguntasGeradasResponse resultado = gemini.gerar(ApoioEmocionalConteudo.INSTRUCAO_PERGUNTAS,
                relato, ApoioEmocionalConteudo.SCHEMA_PERGUNTAS, PerguntasGeradasResponse.class);
        return new PerguntasEmocionaisResponse(ApoioEmocionalValidator.validarPerguntas(resultado));
    }

    public ApoioEmocionalResponse apoio(AprofundamentoRequest request) {
        TextoGeradoResponse resultado = gemini.gerar(ApoioEmocionalConteudo.INSTRUCAO_APOIO,
                request, GeminiConteudo.SCHEMA_TEXTO, TextoGeradoResponse.class);
        return new ApoioEmocionalResponse(GeminiValidator.validarTexto(resultado, 1800, false));
    }

    public ApoioEmocionalResponse transcrever(MultipartFile audio) {
        String mime = ApoioEmocionalValidator.validarAudio(audio);

        try {
            return new ApoioEmocionalResponse(gemini.transcrever(audio.getBytes(), mime));
        } catch (java.io.IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não foi possível ler o áudio.");
        }
    }

}
