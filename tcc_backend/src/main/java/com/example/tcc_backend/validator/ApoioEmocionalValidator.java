package com.example.tcc_backend.validator;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.example.tcc_backend.service.gemini.dto.PerguntasGeradasResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ApoioEmocionalValidator {

    private ApoioEmocionalValidator() {}

    public static List<String> validarPerguntas(PerguntasGeradasResponse resultado) {
        List<String> perguntas = resultado == null ? null : resultado.perguntas();

        if (perguntas == null || perguntas.size() != 3)
            throw GeminiValidator.indisponivel();

        List<String> textos = new ArrayList<>();

        for (String pergunta : perguntas) {
            if (pergunta == null || pergunta.isBlank() || pergunta.length() > 300)
                throw GeminiValidator.indisponivel();

            textos.add(pergunta.trim());
        }

        if (textos.stream().distinct().count() != 3)
            throw GeminiValidator.indisponivel();

        return List.copyOf(textos);
    }

    public static String validarAudio(MultipartFile audio) {
        String mime = Objects.toString(audio.getContentType(), "").split(";")[0].trim();

        if (audio.isEmpty() || audio.getSize() > 8 * 1024 * 1024 || !tipoAudioPermitido(mime)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Envie um áudio válido de até 8 MB.");
        }

        return mime;
    }

    private static boolean tipoAudioPermitido(String mime) {
        return switch (mime) {
            case "audio/webm", "audio/mp4", "audio/ogg", "audio/wav", "audio/mpeg" -> true;
            default -> false;
        };
    }

}
