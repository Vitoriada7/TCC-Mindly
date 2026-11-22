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

        RegistroEmocionalMapper.atualizarEntidade(registro, request);
        return RegistroEmocionalMapper.toResponse(registroEmocionalRepository.save(registro));
    }

    @Transactional(readOnly = true)
    public List<RegistroEmocionalResponse> listar() {
        Long usuarioId = usuarioAutenticadoService.obterUsuarioAutenticado().getId();
        return registroEmocionalRepository.findAllByUsuarioIdOrderByDataRegistroDesc(usuarioId).stream()
                .map(RegistroEmocionalMapper::toResponse)
                .toList();
    }

}
