package com.example.tcc_backend.service;

import com.example.tcc_backend.service.gamificacao.CalculoSequencias;
import com.example.tcc_backend.mapper.ConquistaMapper;
import com.example.tcc_backend.domain.enums.Conquista;
import com.example.tcc_backend.controller.response.ConquistaResponse;
import com.example.tcc_backend.controller.response.ResumoGamificacaoResponse;
import com.example.tcc_backend.domain.RegistroEmocional;
import com.example.tcc_backend.domain.Tarefa;
import com.example.tcc_backend.domain.Usuario;
import com.example.tcc_backend.domain.enums.Avatar;
import com.example.tcc_backend.domain.enums.StatusConquista;
import com.example.tcc_backend.domain.enums.StatusTarefa;
import com.example.tcc_backend.repository.HabitoRepository;
import com.example.tcc_backend.repository.RegistroEmocionalRepository;
import com.example.tcc_backend.repository.RegistroHabitoRepository;
import com.example.tcc_backend.repository.TarefaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GamificacaoService {

    private static final int META_TAREFAS_SEMANAL = Conquista.SEMANA_ORGANIZADA.getMeta();

    private final UsuarioAutenticadoService usuarioAutenticadoService;

    private final HabitoRepository habitoRepository;

    private final RegistroHabitoRepository registroHabitoRepository;

    private final TarefaRepository tarefaRepository;

    private final RegistroEmocionalRepository registroEmocionalRepository;

    public GamificacaoService(UsuarioAutenticadoService usuarioAutenticadoService,
                              HabitoRepository habitoRepository,
                              RegistroHabitoRepository registroHabitoRepository,
                              TarefaRepository tarefaRepository,
                              RegistroEmocionalRepository registroEmocionalRepository) {
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.habitoRepository = habitoRepository;
        this.registroHabitoRepository = registroHabitoRepository;
        this.tarefaRepository = tarefaRepository;
        this.registroEmocionalRepository = registroEmocionalRepository;
    }

    @Transactional
    public ResumoGamificacaoResponse obterResumo() {
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        Long usuarioId = usuario.getId();
        LocalDate hoje = LocalDate.now();

        List<LocalDate> diasHabitos = registroHabitoRepository
                .findAllByHabitoUsuarioIdAndDataLessThanEqualAndConcluidoTrueOrderByDataDesc(usuarioId, hoje).stream()
                .map(registro -> registro.getData())
                .distinct().toList();

        long habitosHoje = registroHabitoRepository.countByHabitoUsuarioIdAndDataAndConcluidoTrue(usuarioId, hoje);
        long metaHabitos = habitoRepository.countByUsuarioIdAndAtivoTrue(usuarioId);

        List<Tarefa> tarefas = tarefaRepository.findAllByUsuarioIdOrderByDataCriacaoDesc(usuarioId);
        LocalDate inicioSemana = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        List<Tarefa> concluidas = tarefas.stream()
                .filter(tarefa -> tarefa.getStatus() == StatusTarefa.CONCLUIDA
                        && tarefa.getDataConclusao() != null)
                .toList();

        int tarefasSemana = concluidas.stream()
                .filter(tarefa -> !tarefa.getDataConclusao().toLocalDate().isBefore(inicioSemana))
                .mapToInt(tarefa -> 1)
                .sum();

        int tarefasNoPrazoSemana = concluidas.stream()
                .filter(tarefa -> !tarefa.getDataConclusao().toLocalDate().isBefore(inicioSemana))
                .filter(this::foiConcluidaNoPrazo)
                .mapToInt(tarefa -> 1)
                .sum();

        int sequenciaTarefas = calcularSequenciaSemanal(concluidas, inicioSemana, tarefasSemana);

        List<RegistroEmocional> registrosEmocionais = registroEmocionalRepository
                .findAllByUsuarioIdOrderByDataRegistroDesc(usuarioId);
        List<LocalDate> diasEmocionais = registrosEmocionais.stream()
                .map(registro -> registro.getDataRegistro().toLocalDate())
                .distinct()
                .toList();
        RegistroEmocional registroHoje = registrosEmocionais.stream()
                .filter(registro -> registro.getDataRegistro().toLocalDate().equals(hoje))
                .findFirst()
                .orElse(null);

        int sequenciaHabitos = CalculoSequencias.calcularSequenciaAtual(diasHabitos, hoje);
        int sequenciaEmocional = CalculoSequencias.calcularSequenciaAtual(diasEmocionais, hoje);

        ResumoGamificacaoResponse.HabitosGamificacaoResponse resumoHabitos = new ResumoGamificacaoResponse.HabitosGamificacaoResponse(
                sequenciaHabitos, CalculoSequencias.calcularMelhorSequencia(diasHabitos), habitosHoje, metaHabitos);

        ResumoGamificacaoResponse.TarefasGamificacaoResponse resumoTarefas = new ResumoGamificacaoResponse.TarefasGamificacaoResponse(
                sequenciaTarefas, tarefasSemana, META_TAREFAS_SEMANAL, tarefasNoPrazoSemana);

        ResumoGamificacaoResponse.EmocionalGamificacaoResponse resumoEmocional = new ResumoGamificacaoResponse.EmocionalGamificacaoResponse(
                sequenciaEmocional, CalculoSequencias.calcularMelhorSequencia(diasEmocionais), registroHoje != null,
                registroHoje == null ? null : registroHoje.getSentimento(), registrosEmocionais.size());

        List<ConquistaResponse> conquistas = criarConquistas(diasHabitos.size(), CalculoSequencias.calcularMelhorSequencia(diasHabitos), concluidas,
                melhorSemana(concluidas), registrosEmocionais, CalculoSequencias.calcularMelhorSequencia(diasEmocionais));

        for (ConquistaResponse conquista : conquistas) {
            Avatar avatar = Avatar.daConquista(conquista.getCodigo());

            if (conquista.getStatus() == StatusConquista.CONCLUIDA)
                usuario.liberarAvatar(avatar);

            if (usuario.getAvataresLiberados().contains(avatar))
                conquista.concluir();
        }

        return new ResumoGamificacaoResponse(resumoHabitos, resumoTarefas, resumoEmocional, conquistas);
    }

    private List<ConquistaResponse> criarConquistas(int diasHabitos, int sequenciaHabitos,
                                                   List<Tarefa> concluidas, int tarefasSemana,
                                                   List<RegistroEmocional> registros, int sequenciaEmocional) {
        int tarefasNoPrazo = concluidas.stream()
                .filter(this::foiConcluidaNoPrazo)
                .mapToInt(tarefa -> 1)
                .sum();
        List<ConquistaResponse> conquistas = new ArrayList<>();
        conquistas.add(ConquistaMapper.toResponse(Conquista.PRIMEIRO_HABITO, diasHabitos));
        conquistas.add(ConquistaMapper.toResponse(Conquista.ROTINA_SUAVE, sequenciaHabitos));
        conquistas.add(ConquistaMapper.toResponse(Conquista.PRIMEIRA_TAREFA, concluidas.size()));
        conquistas.add(ConquistaMapper.toResponse(Conquista.SEMANA_ORGANIZADA, tarefasSemana));
        conquistas.add(ConquistaMapper.toResponse(Conquista.NO_TEMPO_CERTO, tarefasNoPrazo));
        conquistas.add(ConquistaMapper.toResponse(Conquista.PRIMEIRO_REGISTRO, registros.size()));
        conquistas.add(ConquistaMapper.toResponse(Conquista.SEMANA_CONSCIENTE, sequenciaEmocional));
        conquistas.add(ConquistaMapper.toResponse(Conquista.HABITOS_30_DIAS, diasHabitos));
        conquistas.add(ConquistaMapper.toResponse(Conquista.HABITOS_30_SEGUIDOS, sequenciaHabitos));
        conquistas.add(ConquistaMapper.toResponse(Conquista.TAREFAS_25, concluidas.size()));
        conquistas.add(ConquistaMapper.toResponse(Conquista.TAREFAS_50, concluidas.size()));
        conquistas.add(ConquistaMapper.toResponse(Conquista.REGISTROS_30, registros.size()));
        conquistas.add(ConquistaMapper.toResponse(Conquista.EMOCOES_30_SEGUIDOS, sequenciaEmocional));
        return conquistas;
    }

    private int melhorSemana(List<Tarefa> tarefas) {
        return tarefas.stream()
                .collect(Collectors.groupingBy(
                        tarefa -> tarefa.getDataConclusao().toLocalDate()
                                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                        Collectors.summingInt(tarefa -> 1)))
                .values()
                .stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
    }

    private boolean foiConcluidaNoPrazo(Tarefa tarefa) {
        return tarefa.getDataLimite() != null && !tarefa.getDataConclusao().isAfter(tarefa.getDataLimite());
    }

    private int calcularSequenciaSemanal(List<Tarefa> concluidas, LocalDate inicioSemana, int tarefasSemana) {
        Map<LocalDate, Integer> porSemana = concluidas.stream()
                .collect(Collectors.groupingBy(
                        tarefa -> tarefa.getDataConclusao().toLocalDate()
                                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                        Collectors.summingInt(tarefa -> 1)));
        LocalDate semana = tarefasSemana >= META_TAREFAS_SEMANAL ? inicioSemana : inicioSemana.minusWeeks(1);
        int sequencia = 0;

        while (porSemana.getOrDefault(semana, 0) >= META_TAREFAS_SEMANAL) {
            sequencia++;
            semana = semana.minusWeeks(1);
        }

        return sequencia;
    }

}
