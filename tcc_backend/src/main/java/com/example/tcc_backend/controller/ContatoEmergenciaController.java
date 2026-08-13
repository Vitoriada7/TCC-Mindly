package com.example.tcc_backend.controller;

import com.example.tcc_backend.controller.request.ContatoEmergenciaRequest;
import com.example.tcc_backend.controller.response.ContatoEmergenciaResponse;
import com.example.tcc_backend.service.ContatoEmergenciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/contatos-emergencia")
public class ContatoEmergenciaController {

    private final ContatoEmergenciaService contatoService;

    public ContatoEmergenciaController(ContatoEmergenciaService contatoService) {
        this.contatoService = contatoService;
    }

    @GetMapping
    public ResponseEntity<List<ContatoEmergenciaResponse>> listar() {
        return ResponseEntity.ok(contatoService.listar());
    }

    @PostMapping
    public ResponseEntity<ContatoEmergenciaResponse> criar(@Valid @RequestBody ContatoEmergenciaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contatoService.criar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContatoEmergenciaResponse> atualizar(@PathVariable Long id,
                                                               @Valid @RequestBody ContatoEmergenciaRequest request) {
        return ResponseEntity.ok(contatoService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        contatoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
