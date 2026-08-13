package com.example.tcc_backend.service;

import com.example.tcc_backend.controller.request.ContatoEmergenciaRequest;
import com.example.tcc_backend.domain.ContatoEmergencia;
import com.example.tcc_backend.domain.Usuario;
import com.example.tcc_backend.repository.ContatoEmergenciaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContatoEmergenciaServiceTests {

    @Mock
    private ContatoEmergenciaRepository contatoRepository;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @InjectMocks
    private ContatoEmergenciaService contatoService;

    @Test
    void deveCriarContatoParaUsuarioAutenticado() {
        Usuario usuario = Usuario.builder().id(7L).build();
        ContatoEmergenciaRequest request = criarRequest();
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(usuario);
        when(contatoRepository.countByUsuarioId(7L)).thenReturn(0L);
        when(contatoRepository.findAllByUsuarioIdAndPrincipalTrue(7L)).thenReturn(List.of());
        when(contatoRepository.save(any(ContatoEmergencia.class))).thenAnswer(invocacao -> {
            ContatoEmergencia contato = invocacao.getArgument(0);
            contato.setId(1L);
            return contato;
        });

        var response = contatoService.criar(request);

        assertThat(response.getNome()).isEqualTo("Mãe");
        assertThat(response.getTelefone()).isEqualTo("51999999999");
        assertThat(response.isPrincipal()).isTrue();
    }

    @Test
    void naoDeveExcluirContatoDeOutroUsuario() {
        Usuario usuario = Usuario.builder().id(7L).build();
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(usuario);
        when(contatoRepository.findByIdAndUsuarioId(12L, 7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contatoService.excluir(12L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Contato não encontrado");

        verify(contatoRepository, never()).delete(any());
    }

    private ContatoEmergenciaRequest criarRequest() {
        ContatoEmergenciaRequest request = new ContatoEmergenciaRequest();
        request.setNome(" Mãe ");
        request.setTelefone("(51) 99999-9999");
        request.setRelacionamento("Familiar");
        request.setPrincipal(true);
        return request;
    }
}
