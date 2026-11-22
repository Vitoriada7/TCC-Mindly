package com.example.tcc_backend.service;

import java.time.LocalDateTime;
import com.example.tcc_backend.controller.response.ConquistaResponse;
import com.example.tcc_backend.controller.response.ResumoGamificacaoResponse;
import com.example.tcc_backend.domain.RegistroEmocional;
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

    @Mock
    UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    HabitoRepository habitoRepository;

    @Mock
    RegistroHabitoRepository registroHabitoRepository;

    @Mock
    TarefaRepository tarefaRepository;

    @Mock
    RegistroEmocionalRepository registroEmocionalRepository;

    @InjectMocks
    GamificacaoService service;

    @Test
    void deveContarTodosRegistrosMasSequenciaPorDiasDistintos() {
        Usuario usuario = Usuario.builder().id(7L).build();
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(usuario);
        LocalDate hoje = LocalDate.now();
        RegistroEmocional primeiro = new RegistroEmocional();
        primeiro.setDataRegistro(hoje.atTime(10, 0));
        RegistroEmocional segundo = new RegistroEmocional();
        segundo.setDataRegistro(hoje.atTime(12, 0));
        RegistroEmocional terceiro = new RegistroEmocional();
        terceiro.setDataRegistro(hoje.minusDays(1).atTime(12, 0));
        when(registroEmocionalRepository.findAllByUsuarioIdOrderByDataRegistroDesc(7L))
                .thenReturn(List.of(segundo, primeiro, terceiro));
        ResumoGamificacaoResponse resumo = service.obterResumo();
        assertThat(resumo.getEmocional().getTotalRegistros()).isEqualTo(3);
        assertThat(resumo.getEmocional().getSequenciaAtual()).isEqualTo(2);
        assertThat(usuario.getAvataresLiberados()).contains(Avatar.CORACAO);
    }

    @Test
    void deveManterRecompensaMesmoSemRegistrosAtuais() {
        Usuario usuario = Usuario.builder().id(7L).build();
        usuario.getAvataresLiberados().add(Avatar.FLOR);
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(usuario);
        List<ConquistaResponse> conquistas = service.obterResumo().getConquistas();
        assertThat(conquistas).hasSize(13);
        assertThat(conquistas.stream().filter(c -> c.getCodigo().equals("ROTINA_SUAVE")).findFirst().orElseThrow().getStatus())
                .isEqualTo(StatusConquista.CONCLUIDA);
        assertThat(usuario.getAvataresLiberados()).containsExactly(Avatar.FLOR);
    }

    @Test
    void deveLiberarAvatarPorSemanaOrganizadaPassada() {
        Usuario usuario = Usuario.builder().id(7L).build();
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(usuario);
        LocalDateTime data = LocalDate.now().minusWeeks(3).atTime(12, 0);
        List<Tarefa> tarefas = java.util.stream.IntStream.range(0, 5).mapToObj(i -> {
            Tarefa tarefa = new Tarefa();
            tarefa.setStatus(StatusTarefa.CONCLUIDA);
            tarefa.setDataConclusao(data);
            return tarefa;
        }).toList();
        when(tarefaRepository.findAllByUsuarioIdOrderByDataCriacaoDesc(7L)).thenReturn(tarefas);
        service.obterResumo();
        assertThat(usuario.getAvataresLiberados()).contains(Avatar.CORUJA, Avatar.ESTRELA);
    }

    @Test
    void preservaCodigosOrdemEMetasDoCatalogoDeConquistas() {
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(Usuario.builder().id(7L).build());

        List<ConquistaResponse> conquistas = service.obterResumo().getConquistas();

        assertThat(conquistas).extracting(conquista -> conquista.getCodigo()).containsExactly(
                "PRIMEIRO_HABITO", "ROTINA_SUAVE", "PRIMEIRA_TAREFA", "SEMANA_ORGANIZADA", "NO_TEMPO_CERTO",
                "PRIMEIRO_REGISTRO", "SEMANA_CONSCIENTE", "HABITOS_30_DIAS", "HABITOS_30_SEGUIDOS",
                "TAREFAS_25", "TAREFAS_50", "REGISTROS_30", "EMOCOES_30_SEGUIDOS");
        assertThat(conquistas).extracting(conquista -> conquista.getMeta())
                .containsExactly(1, 7, 1, 5, 10, 1, 7, 30, 30, 25, 50, 30, 30);
        assertThat(conquistas).allSatisfy(conquista -> {
            assertThat(conquista.getStatus()).isEqualTo(StatusConquista.BLOQUEADA);
            assertThat(conquista.getProgresso()).isZero();
            assertThat(Avatar.daConquista(conquista.getCodigo())).isNotNull();
        });
    }

    @Test
    void mantemSequenciaDeOntemMesmoSemConclusaoHoje() {
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(Usuario.builder().id(7L).build());
        LocalDate hoje = LocalDate.now();
        when(registroHabitoRepository.findAllByHabitoUsuarioIdAndDataLessThanEqualAndConcluidoTrueOrderByDataDesc(7L, hoje))
                .thenReturn(List.of(
                        com.example.tcc_backend.domain.RegistroHabito.builder().data(hoje.minusDays(1)).build(),
                        com.example.tcc_backend.domain.RegistroHabito.builder().data(hoje.minusDays(2)).build()));

        ResumoGamificacaoResponse.HabitosGamificacaoResponse habitos = service.obterResumo().getHabitos();

        assertThat(habitos.getSequenciaAtual()).isEqualTo(2);
        assertThat(habitos.getMelhorSequencia()).isEqualTo(2);
    }

    @Test
    void preservaMelhorSequenciaMesmoDepoisDeInterrompida() {
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(Usuario.builder().id(7L).build());
        LocalDate hoje = LocalDate.now();
        when(registroHabitoRepository.findAllByHabitoUsuarioIdAndDataLessThanEqualAndConcluidoTrueOrderByDataDesc(7L, hoje))
                .thenReturn(List.of(
                        com.example.tcc_backend.domain.RegistroHabito.builder().data(hoje.minusDays(3)).build(),
                        com.example.tcc_backend.domain.RegistroHabito.builder().data(hoje.minusDays(4)).build()));

        ResumoGamificacaoResponse.HabitosGamificacaoResponse habitos = service.obterResumo().getHabitos();

        assertThat(habitos.getSequenciaAtual()).isZero();
        assertThat(habitos.getMelhorSequencia()).isEqualTo(2);
    }

    @Test
    void preservaContagensDoBancoQueUltrapassamOLimiteDeInt() {
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(Usuario.builder().id(7L).build());
        LocalDate hoje = LocalDate.now();
        when(registroHabitoRepository.countByHabitoUsuarioIdAndDataAndConcluidoTrue(7L, hoje))
                .thenReturn(3_000_000_000L);
        when(habitoRepository.countByUsuarioIdAndAtivoTrue(7L)).thenReturn(4_000_000_000L);

        ResumoGamificacaoResponse.HabitosGamificacaoResponse habitos = service.obterResumo().getHabitos();

        assertThat(habitos.getConcluidosHoje()).isEqualTo(3_000_000_000L);
        assertThat(habitos.getMetaHoje()).isEqualTo(4_000_000_000L);
    }

    @Test
    void consultasRepetidasNaoDuplicamAvataresLiberados() {
        Usuario usuario = Usuario.builder().id(7L).build();
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(usuario);
        RegistroEmocional registro = new RegistroEmocional();
        registro.setDataRegistro(LocalDate.now().atTime(10, 0));
        when(registroEmocionalRepository.findAllByUsuarioIdOrderByDataRegistroDesc(7L)).thenReturn(List.of(registro));

        service.obterResumo();
        service.obterResumo();

        assertThat(usuario.getAvataresLiberados()).containsExactly(Avatar.CORACAO);
    }

}
