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
    private final GamificacaoService gamificacaoService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                          UsuarioAutenticadoService usuarioAutenticadoService, GamificacaoService gamificacaoService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.gamificacaoService = gamificacaoService;
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

    @org.springframework.transaction.annotation.Transactional
    public UsuarioResponse obterPerfil() {
        gamificacaoService.obterResumo();
        return UsuarioMapper.toResponse(usuarioAutenticadoService.obterUsuarioAutenticado());
    }

    @org.springframework.transaction.annotation.Transactional
    public UsuarioResponse atualizarAvatar(com.example.tcc_backend.controller.request.AvatarRequest request) {
        gamificacaoService.obterResumo();
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        if (request.avatar() != null && !usuario.getAvataresLiberados().contains(request.avatar())) {
            throw new IllegalArgumentException("Conclua a conquista para liberar este avatar.");
        }
        usuario.setAvatar(request.avatar());
        return UsuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @org.springframework.transaction.annotation.Transactional
    public UsuarioResponse atualizarPerfil(UsuarioUpdateRequest request) {
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        usuario.setApelido(request.getApelido().trim());
        return UsuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

}
