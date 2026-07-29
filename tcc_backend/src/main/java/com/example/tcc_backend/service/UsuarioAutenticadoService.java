package com.example.tcc_backend.service;

import com.example.tcc_backend.domain.Usuario;
import com.example.tcc_backend.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class UsuarioAutenticadoService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioAutenticadoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario obterUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Usuário não autenticado");
        }

        try {
            Long usuarioId = Long.valueOf(jwt.getSubject());
            return usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Usuário não encontrado"));
        } catch (NumberFormatException exception) {
            throw new ResponseStatusException(UNAUTHORIZED, "Token inválido");
        }
    }
}
