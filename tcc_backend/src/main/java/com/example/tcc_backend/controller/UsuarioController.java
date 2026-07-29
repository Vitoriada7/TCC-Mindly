package com.example.tcc_backend.controller;

import com.example.tcc_backend.controller.request.UsuarioUpdateRequest;
import com.example.tcc_backend.controller.response.UsuarioResponse;
import com.example.tcc_backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> obterPerfil() {
        return ResponseEntity.ok(usuarioService.obterPerfil());
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioResponse> atualizarPerfil(@Valid @RequestBody UsuarioUpdateRequest request) {
        return ResponseEntity.ok(usuarioService.atualizarPerfil(request));
    }
}
