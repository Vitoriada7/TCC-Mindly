package com.example.tcc_backend.controller;

import com.example.tcc_backend.controller.request.AprofundamentoRequest;
import com.example.tcc_backend.controller.response.*;
import com.example.tcc_backend.service.ApoioEmocionalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/registros-emocionais/ia")
public class ApoioEmocionalController {
    private final ApoioEmocionalService apoio;
    public ApoioEmocionalController(ApoioEmocionalService apoio) { this.apoio = apoio; }

    @PostMapping("/perguntas")
    public ResponseEntity<PerguntasEmocionaisResponse> perguntas(@Valid @RequestBody AprofundamentoRequest request) {
        return ResponseEntity.ok(apoio.perguntas(request));
    }
    @PostMapping("/apoio")
    public ResponseEntity<ApoioEmocionalResponse> apoio(@Valid @RequestBody AprofundamentoRequest request) {
        return ResponseEntity.ok(apoio.apoio(request));
    }
    @PostMapping(value = "/transcricao", consumes = "multipart/form-data")
    public ResponseEntity<ApoioEmocionalResponse> transcrever(@RequestPart("audio") MultipartFile audio) {
        return ResponseEntity.ok(apoio.transcrever(audio));
    }
}
