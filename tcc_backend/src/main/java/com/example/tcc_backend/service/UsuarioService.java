package com.example.tcc_backend.service;

import com.example.tcc_backend.controller.request.CadastroRequest;
import com.example.tcc_backend.controller.request.UsuarioUpdateRequest;
import com.example.tcc_backend.controller.response.UsuarioResponse;
import com.example.tcc_backend.domain.Usuario;
import com.example.tcc_backend.mapper.UsuarioMapper;
import com.example.tcc_backend.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                          UsuarioAutenticadoService usuarioAutenticadoService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    public UsuarioResponse cadastrar(CadastroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("E-mail ja cadastrado");
        }

        Usuario usuario = UsuarioMapper.toEntity(request);
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return UsuarioMapper.toResponse(usuarioSalvo);
    }

    public UsuarioResponse obterPerfil() {
        return UsuarioMapper.toResponse(usuarioAutenticadoService.obterUsuarioAutenticado());
    }

    public UsuarioResponse atualizarPerfil(UsuarioUpdateRequest request) {
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        usuario.setNome(request.getNome().trim());
        return UsuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

}
