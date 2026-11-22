package com.example.tcc_backend.controller;

import com.example.tcc_backend.service.GoogleTasksService;
import com.example.tcc_backend.controller.request.GoogleTasksConectarRequest;
import com.example.tcc_backend.controller.response.GoogleTasksEstadoResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.tcc_backend.controller.response.GoogleTasksAutorizacaoResponse;
import com.example.tcc_backend.controller.response.GoogleTasksSincronizacaoResponse;

@RestController
@RequestMapping("/integracoes/google-tasks")
public class GoogleTasksController {

    private final GoogleTasksService service;

    public GoogleTasksController(GoogleTasksService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<GoogleTasksEstadoResponse> estado() {
        return ResponseEntity.ok(service.estado());
    }

    @PostMapping("/autorizar")
    public ResponseEntity<GoogleTasksAutorizacaoResponse> autorizar() {
        return ResponseEntity.ok(new GoogleTasksAutorizacaoResponse(service.autorizar()));
    }

    @PostMapping("/conectar")
    public ResponseEntity<Void> conectar(@Valid @RequestBody GoogleTasksConectarRequest request) {
        service.conectar(request.code(), request.state());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sincronizar")
    public ResponseEntity<GoogleTasksSincronizacaoResponse> sincronizar() {
        return ResponseEntity.ok(new GoogleTasksSincronizacaoResponse(service.sincronizar()));
    }

    @DeleteMapping
    public ResponseEntity<Void> desconectar() {
        service.desconectar();
        return ResponseEntity.noContent().build();
    }

}
