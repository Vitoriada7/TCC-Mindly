package com.example.tcc_backend.service;

import com.example.tcc_backend.controller.request.LoginRequest;
import com.example.tcc_backend.controller.response.LoginResponse;
import com.example.tcc_backend.domain.Usuario;
import com.example.tcc_backend.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtEncoder jwtEncoder;

    public AutenticacaoService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    public LoginResponse autenticar(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("E-mail ou senha invalidos"));

        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            throw new IllegalArgumentException("E-mail ou senha invalidos");
        }

        String token = gerarToken(usuario);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        return response;
    }

    private String gerarToken(Usuario usuario) {
        Instant agora = Instant.now();
        Instant expiracao = agora.plusSeconds(7200);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(usuario.getId().toString())
                .claim("email", usuario.getEmail())
                .issuedAt(agora)
                .expiresAt(expiracao)
                .build();

        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

}
