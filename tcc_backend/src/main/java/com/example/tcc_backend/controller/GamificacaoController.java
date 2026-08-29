package com.example.tcc_backend.controller;

import com.example.tcc_backend.controller.response.ResumoGamificacaoResponse;
import com.example.tcc_backend.service.GamificacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gamificacao")
public class GamificacaoController {
    private final GamificacaoService gamificacaoService;

    public GamificacaoController(GamificacaoService gamificacaoService) {
        this.gamificacaoService = gamificacaoService;
    }

    @GetMapping("/resumo")
    public ResponseEntity<ResumoGamificacaoResponse> resumo() {
        return ResponseEntity.ok(gamificacaoService.obterResumo());
    }
}
