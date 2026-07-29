package com.example.tcc_backend.controller;

import com.example.tcc_backend.controller.request.HabitoRequest;
import com.example.tcc_backend.controller.response.HabitoResponse;
import com.example.tcc_backend.controller.response.ResumoHabitosResponse;
import com.example.tcc_backend.service.HabitoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/habitos")
public class HabitoController {

    private final HabitoService habitoService;

    public HabitoController(HabitoService habitoService) {
        this.habitoService = habitoService;
    }

    @PostMapping
    public ResponseEntity<HabitoResponse> criar(@Valid @RequestBody HabitoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(habitoService.criar(request));
    }

    /**
     * Sem período informado, retorna os registros da semana corrente, de segunda-feira a domingo.
     */
    @GetMapping
    public ResponseEntity<List<HabitoResponse>> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(habitoService.listar(inicio, fim));
    }

    @GetMapping("/resumo")
    public ResponseEntity<ResumoHabitosResponse> resumo() {
        return ResponseEntity.ok(habitoService.obterResumo());
    }

    @PutMapping("/{id}")
    public ResponseEntity<HabitoResponse> atualizar(@PathVariable Long id, @Valid @RequestBody HabitoRequest request) {
        return ResponseEntity.ok(habitoService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        habitoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/registros/{data}")
    public ResponseEntity<Void> marcarConcluido(@PathVariable Long id,
                                                 @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        habitoService.marcarConcluido(id, data);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/registros/{data}")
    public ResponseEntity<Void> desmarcarConcluido(@PathVariable Long id,
                                                    @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        habitoService.desmarcarConcluido(id, data);
        return ResponseEntity.noContent().build();
    }
}
