package com.example.tcc_backend.service;

import com.example.tcc_backend.controller.request.RegistroEmocionalRequest;
import com.example.tcc_backend.controller.response.RegistroEmocionalResponse;
import com.example.tcc_backend.domain.RegistroEmocional;
import com.example.tcc_backend.domain.Usuario;
import com.example.tcc_backend.mapper.RegistroEmocionalMapper;
import com.example.tcc_backend.repository.RegistroEmocionalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistroEmocionalService {
    private static final String SEPARADOR_EXPLORACOES = "\n\u001e\n";

    private final RegistroEmocionalRepository registroEmocionalRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public RegistroEmocionalService(RegistroEmocionalRepository registroEmocionalRepository,
                                    UsuarioAutenticadoService usuarioAutenticadoService) {
        this.registroEmocionalRepository = registroEmocionalRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @Transactional
    public RegistroEmocionalResponse registrar(RegistroEmocionalRequest request) {
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        LocalDateTime agora = LocalDateTime.now();
        LocalDate hoje = agora.toLocalDate();
        RegistroEmocional registro = registroEmocionalRepository
                .findByUsuarioIdAndData(usuario.getId(), hoje)
                .orElseGet(() -> RegistroEmocional.builder().usuario(usuario).data(hoje).dataRegistro(agora).build());

        registro.setSentimento(request.getSentimento().trim());
        registro.setSentimentoDetalhado(normalizar(request.getSentimentoDetalhado()));
        registro.setPensamento(normalizar(request.getPensamento()));
        registro.setReflexao(normalizar(request.getReflexao()));
        registro.setExploracoes(request.getExploracoes() == null ? null
                : String.join(SEPARADOR_EXPLORACOES, request.getExploracoes()));
        return RegistroEmocionalMapper.toResponse(registroEmocionalRepository.save(registro));
    }

    @Transactional(readOnly = true)
    public List<RegistroEmocionalResponse> listar() {
        Long usuarioId = usuarioAutenticadoService.obterUsuarioAutenticado().getId();
        return registroEmocionalRepository.findAllByUsuarioIdOrderByDataRegistroDesc(usuarioId).stream()
                .map(RegistroEmocionalMapper::toResponse)
                .toList();
    }

    private String normalizar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
