package com.example.tcc_backend.service;

import com.example.tcc_backend.validator.UsuarioValidator;
import com.example.tcc_backend.controller.request.CadastroRequest;
import com.example.tcc_backend.controller.request.UsuarioUpdateRequest;
import com.example.tcc_backend.controller.request.TemaRequest;
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
        UsuarioValidator.validarEmailDisponivel(usuarioRepository.existsByEmail(request.getEmail()));

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
        UsuarioValidator.validarAvatarLiberado(usuario, request.avatar());
        usuario.setAvatar(request.avatar());
        return UsuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @org.springframework.transaction.annotation.Transactional
    public UsuarioResponse atualizarPerfil(UsuarioUpdateRequest request) {
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        usuario.setApelido(request.getApelido().trim());
        return UsuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @org.springframework.transaction.annotation.Transactional
    public UsuarioResponse atualizarTema(TemaRequest request) {
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        usuario.setTemaEscuro(request.temaEscuro());
        return UsuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

}
