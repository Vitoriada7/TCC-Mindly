package com.example.tcc_backend.service;

import com.example.tcc_backend.controller.request.RegistroEmocionalRequest;
import com.example.tcc_backend.domain.RegistroEmocional;
import com.example.tcc_backend.domain.Usuario;
import com.example.tcc_backend.repository.RegistroEmocionalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistroEmocionalServiceTests {

    @Mock
    private RegistroEmocionalRepository registroEmocionalRepository;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @InjectMocks
    private RegistroEmocionalService registroEmocionalService;

    @Test
    void deveCriarRegistroParaUsuarioAutenticado() {
        Usuario usuario = Usuario.builder().id(7L).build();
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(usuario);
        when(registroEmocionalRepository.findByUsuarioIdAndData(7L, LocalDate.now())).thenReturn(Optional.empty());
        when(registroEmocionalRepository.save(any(RegistroEmocional.class))).thenAnswer(invocacao -> {
            RegistroEmocional registro = invocacao.getArgument(0);
            registro.setId(1L);
            return registro;
        });

        var response = registroEmocionalService.registrar(criarRequest());

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getSentimento()).isEqualTo("bem");
        ArgumentCaptor<RegistroEmocional> captor = ArgumentCaptor.forClass(RegistroEmocional.class);
        verify(registroEmocionalRepository).save(captor.capture());
        assertThat(captor.getValue().getUsuario()).isSameAs(usuario);
        assertThat(captor.getValue().getData()).isEqualTo(LocalDate.now());
    }

    @Test
    void deveAtualizarRegistroQuandoUsuarioJaRegistrouNoDia() {
        Usuario usuario = Usuario.builder().id(7L).build();
        RegistroEmocional existente = RegistroEmocional.builder().id(3L).usuario(usuario).data(LocalDate.now())
                .dataRegistro(LocalDateTime.now()).sentimento("neutra").build();
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(usuario);
        when(registroEmocionalRepository.findByUsuarioIdAndData(7L, LocalDate.now())).thenReturn(Optional.of(existente));
        when(registroEmocionalRepository.save(existente)).thenReturn(existente);

        var response = registroEmocionalService.registrar(criarRequest());

        assertThat(response.getId()).isEqualTo(3L);
        assertThat(existente.getSentimento()).isEqualTo("bem");
        assertThat(existente.getPensamento()).isEqualTo("Um dia tranquilo");
    }

    private RegistroEmocionalRequest criarRequest() {
        RegistroEmocionalRequest request = new RegistroEmocionalRequest();
        request.setSentimento(" bem ");
        request.setPensamento(" Um dia tranquilo ");
        request.setExploracoes(List.of("", "Com mais calma", ""));
        request.setReflexao("Continuar");
        return request;
    }
}
