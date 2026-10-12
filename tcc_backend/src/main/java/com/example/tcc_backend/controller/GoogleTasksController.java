package com.example.tcc_backend.controller;

import com.example.tcc_backend.service.GoogleTasksService;
import com.example.tcc_backend.controller.request.GoogleTasksConectarRequest;
import com.example.tcc_backend.controller.response.GoogleTasksEstadoResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/integracoes/google-tasks")
public class GoogleTasksController {
    private final GoogleTasksService service;
    public GoogleTasksController(GoogleTasksService service) { this.service = service; }
    @GetMapping
    public ResponseEntity<GoogleTasksEstadoResponse> estado() { return ResponseEntity.ok(service.estado()); }
    @PostMapping("/autorizar")
    public ResponseEntity<Map<String, String>> autorizar() { return ResponseEntity.ok(Map.of("url", service.autorizar())); }
    @PostMapping("/conectar")
    public ResponseEntity<Void> conectar(@Valid @RequestBody GoogleTasksConectarRequest request) {
        service.conectar(request.code(), request.state()); return ResponseEntity.noContent().build();
    }
    @PostMapping("/sincronizar")
    public ResponseEntity<Map<String, Integer>> sincronizar() { return ResponseEntity.ok(Map.of("tarefas", service.sincronizar())); }
    @DeleteMapping
    public ResponseEntity<Void> desconectar() { service.desconectar(); return ResponseEntity.noContent().build(); }
}
