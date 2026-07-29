package com.example.tcc_backend.controller;

import com.example.tcc_backend.controller.request.CadastroRequest;
import com.example.tcc_backend.controller.request.LoginRequest;
import com.example.tcc_backend.controller.response.LoginResponse;
import com.example.tcc_backend.controller.response.UsuarioResponse;
import com.example.tcc_backend.service.AutenticacaoService;
import com.example.tcc_backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/autenticacao")
public class AutenticacaoController {

    private final UsuarioService usuarioService;

    private final AutenticacaoService autenticacaoService;

    public AutenticacaoController(UsuarioService usuarioService, AutenticacaoService autenticacaoService) {
        this.usuarioService = usuarioService;
        this.autenticacaoService = autenticacaoService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody CadastroRequest request) {
        UsuarioResponse response = usuarioService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> autenticar(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = autenticacaoService.autenticar(request);
        return ResponseEntity.ok(response);
    }

}
