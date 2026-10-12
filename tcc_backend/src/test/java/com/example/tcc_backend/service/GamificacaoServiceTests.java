package com.example.tcc_backend.service;

import com.example.tcc_backend.domain.Usuario;
import com.example.tcc_backend.domain.Tarefa;
import com.example.tcc_backend.domain.enums.Avatar;
import com.example.tcc_backend.domain.enums.StatusConquista;
import com.example.tcc_backend.domain.enums.StatusTarefa;
import com.example.tcc_backend.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GamificacaoServiceTests {
    @Mock UsuarioAutenticadoService usuarioAutenticadoService;
    @Mock HabitoRepository habitoRepository;
    @Mock RegistroHabitoRepository registroHabitoRepository;
    @Mock TarefaRepository tarefaRepository;
    @Mock RegistroEmocionalRepository registroEmocionalRepository;
    @InjectMocks GamificacaoService service;

    @Test
    void deveContarTodosRegistrosMasSequenciaPorDiasDistintos() {
        var usuario = Usuario.builder().id(7L).build();
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(usuario);
        var hoje = LocalDate.now();
        var primeiro = new com.example.tcc_backend.domain.RegistroEmocional();
        primeiro.setDataRegistro(hoje.atTime(10, 0));
        var segundo = new com.example.tcc_backend.domain.RegistroEmocional();
        segundo.setDataRegistro(hoje.atTime(12, 0));
        var terceiro = new com.example.tcc_backend.domain.RegistroEmocional();
        terceiro.setDataRegistro(hoje.minusDays(1).atTime(12, 0));
        when(registroEmocionalRepository.findAllByUsuarioIdOrderByDataRegistroDesc(7L))
                .thenReturn(List.of(segundo, primeiro, terceiro));
        var resumo = service.obterResumo();
        assertThat(resumo.getEmocional().getTotalRegistros()).isEqualTo(3);
        assertThat(resumo.getEmocional().getSequenciaAtual()).isEqualTo(2);
        assertThat(usuario.getAvataresLiberados()).contains(Avatar.CORACAO);
    }

    @Test
    void deveManterRecompensaMesmoSemRegistrosAtuais() {
        var usuario = Usuario.builder().id(7L).build();
        usuario.getAvataresLiberados().add(Avatar.FLOR);
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(usuario);
        var conquistas = service.obterResumo().getConquistas();
        assertThat(conquistas).hasSize(13);
        assertThat(conquistas.stream().filter(c -> c.getCodigo().equals("ROTINA_SUAVE")).findFirst().orElseThrow().getStatus())
                .isEqualTo(StatusConquista.CONCLUIDA);
        assertThat(usuario.getAvataresLiberados()).containsExactly(Avatar.FLOR);
    }

    @Test
    void deveLiberarAvatarPorSemanaOrganizadaPassada() {
        var usuario = Usuario.builder().id(7L).build();
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(usuario);
        var data = LocalDate.now().minusWeeks(3).atTime(12, 0);
        var tarefas = java.util.stream.IntStream.range(0, 5).mapToObj(i -> {
            var tarefa = new Tarefa();
            tarefa.setStatus(StatusTarefa.CONCLUIDA);
            tarefa.setDataConclusao(data);
            return tarefa;
        }).toList();
        when(tarefaRepository.findAllByUsuarioIdOrderByDataCriacaoDesc(7L)).thenReturn(tarefas);
        service.obterResumo();
        assertThat(usuario.getAvataresLiberados()).contains(Avatar.CORUJA, Avatar.ESTRELA);
    }
}
