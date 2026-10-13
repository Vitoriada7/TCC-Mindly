package com.example.tcc_backend.service;

import com.example.tcc_backend.domain.Habito;
import com.example.tcc_backend.domain.Usuario;
import com.example.tcc_backend.repository.HabitoRepository;
import com.example.tcc_backend.repository.RegistroHabitoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitoServiceTests {
    @Mock HabitoRepository habitoRepository;
    @Mock RegistroHabitoRepository registroHabitoRepository;
    @Mock UsuarioAutenticadoService usuarioAutenticadoService;
    @InjectMocks HabitoService service;

    @Test
    void deveBloquearMarcacaoEDesmarcacaoForaDeHoje() {
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(Usuario.builder().id(7L).build());
        when(habitoRepository.findByIdAndUsuarioIdAndAtivoTrue(1L, 7L))
                .thenReturn(Optional.of(Habito.builder().id(1L).build()));
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        for (LocalDate data : new LocalDate[]{hoje.minusDays(1), hoje.plusDays(1)}) {
            assertThatThrownBy(() -> service.marcarConcluido(1L, data))
                    .isInstanceOf(ResponseStatusException.class).hasMessageContaining("Somente o dia atual");
            assertThatThrownBy(() -> service.desmarcarConcluido(1L, data))
                    .isInstanceOf(ResponseStatusException.class).hasMessageContaining("Somente o dia atual");
        }
        verifyNoInteractions(registroHabitoRepository);
    }

    @Test
    void devePermitirDesmarcarHoje() {
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(Usuario.builder().id(7L).build());
        when(habitoRepository.findByIdAndUsuarioIdAndAtivoTrue(1L, 7L))
                .thenReturn(Optional.of(Habito.builder().id(1L).build()));
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        when(registroHabitoRepository.findByHabitoIdAndData(1L, hoje)).thenReturn(Optional.empty());
        service.desmarcarConcluido(1L, hoje);
        verify(registroHabitoRepository).findByHabitoIdAndData(1L, hoje);
    }
}
