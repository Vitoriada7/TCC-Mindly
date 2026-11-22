package com.example.tcc_backend.service.gemini.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GeminiSchema(String type, Map<String, GeminiSchema> properties, List<String> required,
                           GeminiSchema items, Integer minItems, Integer maxItems) {
    public static GeminiSchema texto() {
        return new GeminiSchema("STRING", null, null, null, null, null);
    }

    public static GeminiSchema objeto(Map<String, GeminiSchema> propriedades, List<String> obrigatorios) {
        return new GeminiSchema("OBJECT", propriedades, obrigatorios, null, null, null);
    }

    public static GeminiSchema lista(GeminiSchema item, int minimo, int maximo) {
        return new GeminiSchema("ARRAY", null, null, item, minimo, maximo);
    }
}
