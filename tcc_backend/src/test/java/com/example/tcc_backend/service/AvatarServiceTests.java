package com.example.tcc_backend.service;

import com.example.tcc_backend.controller.request.AvatarRequest;
import com.example.tcc_backend.domain.Usuario;
import com.example.tcc_backend.domain.enums.Avatar;
import com.example.tcc_backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvatarServiceTests {
    @Mock UsuarioRepository usuarioRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock UsuarioAutenticadoService usuarioAutenticadoService;
    @Mock GamificacaoService gamificacaoService;
    @InjectMocks UsuarioService service;

    @Test
    void deveRecusarAvatarBloqueadoSemSalvar() {
        var usuario = Usuario.builder().id(7L).build();
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(usuario);
        assertThatThrownBy(() -> service.atualizarAvatar(new AvatarRequest(Avatar.ARVORE)))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(usuarioRepository);
        assertThat(usuario.getAvatar()).isNull();
    }

    @Test
    void deveSelecionarRecompensaDoUsuarioERestaurarIniciais() {
        var usuario = Usuario.builder().id(7L).build();
        usuario.getAvataresLiberados().add(Avatar.ARVORE);
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(usuario);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        assertThat(service.atualizarAvatar(new AvatarRequest(Avatar.ARVORE)).getAvatar()).isEqualTo(Avatar.ARVORE);
        assertThat(service.atualizarAvatar(new AvatarRequest(null)).getAvatar()).isNull();
        assertThat(usuario.getAvataresLiberados()).containsExactly(Avatar.ARVORE);
    }

    @Test
    void deveSincronizarConquistaAntesDeSelecionarAvatar() {
        var usuario = Usuario.builder().id(7L).build();
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(usuario);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(gamificacaoService.obterResumo()).thenAnswer(invocacao -> {
            usuario.getAvataresLiberados().add(Avatar.ESTRELA);
            return null;
        });
        assertThat(service.atualizarAvatar(new AvatarRequest(Avatar.ESTRELA)).getAvatar()).isEqualTo(Avatar.ESTRELA);
        verify(gamificacaoService).obterResumo();
    }

    @Test
    void deveRetornarBibliotecaSincronizadaNoPerfil() {
        var usuario = Usuario.builder().id(7L).build();
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(usuario);
        when(gamificacaoService.obterResumo()).thenAnswer(invocacao -> {
            usuario.getAvataresLiberados().add(Avatar.CORACAO);
            return null;
        });
        assertThat(service.obterPerfil().getAvataresLiberados()).containsExactly(Avatar.CORACAO);
    }
}
