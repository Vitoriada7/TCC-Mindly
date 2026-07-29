package com.example.tcc_backend.controller;

import com.example.tcc_backend.controller.request.TarefaRequest;
import com.example.tcc_backend.controller.response.TarefaResponse;
import com.example.tcc_backend.domain.enums.Prioridade;
import com.example.tcc_backend.domain.enums.StatusTarefa;
import com.example.tcc_backend.service.TarefaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tarefas")
public class TarefaController {

    private final TarefaService tarefaService;

    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    @PostMapping
    public ResponseEntity<TarefaResponse> criar(@Valid @RequestBody TarefaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tarefaService.criar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarefaResponse> atualizar(@PathVariable Long id, @Valid @RequestBody TarefaRequest request) {
        return ResponseEntity.ok(tarefaService.atualizar(id, request));
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<TarefaResponse> concluir(@PathVariable Long id) {
        return ResponseEntity.ok(tarefaService.concluir(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        tarefaService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<TarefaResponse>> listar(
            @RequestParam(required = false) StatusTarefa status,
            @RequestParam(required = false) Prioridade prioridade,
            @RequestParam(name = "categoria", required = false) Long categoriaId,
            @RequestParam(required = false) Boolean vencida) {
        return ResponseEntity.ok(tarefaService.listar(status, prioridade, categoriaId, vencida));
    }
}
