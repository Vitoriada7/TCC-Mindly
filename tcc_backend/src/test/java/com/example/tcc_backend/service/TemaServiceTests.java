package com.example.tcc_backend.service;

import com.example.tcc_backend.controller.request.TemaRequest;
import com.example.tcc_backend.domain.Usuario;
import com.example.tcc_backend.repository.UsuarioRepository;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TemaServiceTests {
    @Mock UsuarioRepository usuarioRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock UsuarioAutenticadoService usuarioAutenticadoService;
    @Mock GamificacaoService gamificacaoService;
    @InjectMocks UsuarioService service;

    @Test
    void deveSalvarEDevolverTemaApenasDoUsuarioAutenticado() {
        var usuario = Usuario.builder().id(7L).apelido("Ana").build();
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(usuario);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        assertThat(service.atualizarTema(new TemaRequest(true)).isTemaEscuro()).isTrue();
        assertThat(usuario.getTemaEscuro()).isTrue();
        assertThat(service.atualizarTema(new TemaRequest(false)).isTemaEscuro()).isFalse();
        assertThat(usuario.getApelido()).isEqualTo("Ana");
        verify(usuarioRepository, times(2)).save(usuario);
        verifyNoMoreInteractions(usuarioRepository);
    }

    @Test
    void deveUsarTemaClaroParaPerfisAntigosSemPreferencia() {
        when(usuarioAutenticadoService.obterUsuarioAutenticado())
                .thenReturn(Usuario.builder().id(7L).temaEscuro(null).build());
        assertThat(service.obterPerfil().isTemaEscuro()).isFalse();
    }

    @Test
    void deveExigirPreferenciaNoRequest() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            assertThat(validator.validate(new TemaRequest(null))).hasSize(1);
            assertThat(validator.validate(new TemaRequest(false))).isEmpty();
        }
    }
}
