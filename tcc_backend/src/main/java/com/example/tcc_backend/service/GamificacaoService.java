package com.example.tcc_backend.service;

import com.example.tcc_backend.controller.response.ConquistaResponse;
import com.example.tcc_backend.controller.response.ResumoGamificacaoResponse;
import com.example.tcc_backend.domain.RegistroEmocional;
import com.example.tcc_backend.domain.Tarefa;
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GamificacaoService {
    private static final long META_TAREFAS_SEMANAL = 5;

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

    @Transactional(readOnly = true)
    public ResumoGamificacaoResponse obterResumo() {
        Long usuarioId = usuarioAutenticadoService.obterUsuarioAutenticado().getId();
        LocalDate hoje = LocalDate.now();

        Set<LocalDate> diasHabitos = registroHabitoRepository
                .findAllByHabitoUsuarioIdAndDataLessThanEqualAndConcluidoTrueOrderByDataDesc(usuarioId, hoje).stream()
                .map(registro -> registro.getData())
                .collect(Collectors.toSet());
        long habitosHoje = registroHabitoRepository.countByHabitoUsuarioIdAndDataAndConcluidoTrue(usuarioId, hoje);
        long metaHabitos = habitoRepository.countByUsuarioIdAndAtivoTrue(usuarioId);

        List<Tarefa> tarefas = tarefaRepository.findAllByUsuarioIdOrderByDataCriacaoDesc(usuarioId);
        LocalDate inicioSemana = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        List<Tarefa> concluidas = tarefas.stream().filter(tarefa -> tarefa.getStatus() == StatusTarefa.CONCLUIDA
                && tarefa.getDataConclusao() != null).toList();
        long tarefasSemana = concluidas.stream()
                .filter(tarefa -> !tarefa.getDataConclusao().toLocalDate().isBefore(inicioSemana)).count();
        long tarefasNoPrazoSemana = concluidas.stream()
                .filter(tarefa -> !tarefa.getDataConclusao().toLocalDate().isBefore(inicioSemana))
                .filter(this::foiConcluidaNoPrazo).count();
        long sequenciaTarefas = calcularSequenciaSemanal(concluidas, inicioSemana, tarefasSemana);

        List<RegistroEmocional> registrosEmocionais = registroEmocionalRepository
                .findAllByUsuarioIdOrderByDataRegistroDesc(usuarioId);
        Set<LocalDate> diasEmocionais = registrosEmocionais.stream()
                .map(registro -> registro.getDataRegistro().toLocalDate()).collect(Collectors.toSet());
        RegistroEmocional registroHoje = registrosEmocionais.stream()
                .filter(registro -> registro.getDataRegistro().toLocalDate().equals(hoje)).findFirst().orElse(null);

        long sequenciaHabitos = calcularSequenciaAtual(diasHabitos, hoje);
        long sequenciaEmocional = calcularSequenciaAtual(diasEmocionais, hoje);
        var resumoHabitos = new ResumoGamificacaoResponse.HabitosGamificacaoResponse(
                sequenciaHabitos, calcularMelhorSequencia(diasHabitos), habitosHoje, metaHabitos);
        var resumoTarefas = new ResumoGamificacaoResponse.TarefasGamificacaoResponse(
                sequenciaTarefas, tarefasSemana, META_TAREFAS_SEMANAL, tarefasNoPrazoSemana);
        var resumoEmocional = new ResumoGamificacaoResponse.EmocionalGamificacaoResponse(
                sequenciaEmocional, calcularMelhorSequencia(diasEmocionais), registroHoje != null,
                registroHoje == null ? null : registroHoje.getSentimento());

        return new ResumoGamificacaoResponse(resumoHabitos, resumoTarefas, resumoEmocional,
                criarConquistas(diasHabitos.size(), sequenciaHabitos, concluidas, tarefasSemana,
                        registrosEmocionais, sequenciaEmocional));
    }

    private List<ConquistaResponse> criarConquistas(long diasHabitos, long sequenciaHabitos,
                                                     List<Tarefa> concluidas, long tarefasSemana,
                                                     List<RegistroEmocional> registros, long sequenciaEmocional) {
        long tarefasNoPrazo = concluidas.stream().filter(this::foiConcluidaNoPrazo).count();
        List<ConquistaResponse> conquistas = new ArrayList<>();
        conquistas.add(conquista("PRIMEIRO_HABITO", "Primeiro passo", "Conclua seu primeiro hábito.", "HABITOS", diasHabitos, 1));
        conquistas.add(conquista("ROTINA_SUAVE", "Rotina suave", "Mantenha 7 dias de hábitos em sequência.", "HABITOS", sequenciaHabitos, 7));
        conquistas.add(conquista("PRIMEIRA_TAREFA", "Tirando do papel", "Conclua sua primeira tarefa.", "TAREFAS", concluidas.size(), 1));
        conquistas.add(conquista("SEMANA_ORGANIZADA", "Semana organizada", "Conclua 5 tarefas na mesma semana.", "TAREFAS", tarefasSemana, META_TAREFAS_SEMANAL));
        conquistas.add(conquista("NO_TEMPO_CERTO", "No tempo certo", "Conclua 10 tarefas dentro do prazo.", "TAREFAS", tarefasNoPrazo, 10));
        conquistas.add(conquista("PRIMEIRO_REGISTRO", "Primeiro olhar", "Faça seu primeiro registro emocional.", "EMOCIONAL", registros.size(), 1));
        conquistas.add(conquista("SEMANA_CONSCIENTE", "Semana consciente", "Registre suas emoções por 7 dias seguidos.", "EMOCIONAL", sequenciaEmocional, 7));
        return conquistas;
    }

    private ConquistaResponse conquista(String codigo, String titulo, String descricao, String trilha,
                                         long progresso, long meta) {
        StatusConquista status = progresso >= meta ? StatusConquista.CONCLUIDA
                : progresso > 0 ? StatusConquista.EM_PROGRESSO : StatusConquista.BLOQUEADA;
        return new ConquistaResponse(codigo, titulo, descricao, trilha, status, Math.min(progresso, meta), meta);
    }

    private boolean foiConcluidaNoPrazo(Tarefa tarefa) {
        return tarefa.getDataLimite() != null && !tarefa.getDataConclusao().isAfter(tarefa.getDataLimite());
    }

    private long calcularSequenciaAtual(Set<LocalDate> dias, LocalDate hoje) {
        LocalDate referencia = dias.contains(hoje) ? hoje : hoje.minusDays(1);
        long sequencia = 0;
        while (dias.contains(referencia)) {
            sequencia++;
            referencia = referencia.minusDays(1);
        }
        return sequencia;
    }

    private long calcularMelhorSequencia(Set<LocalDate> dias) {
        long melhor = 0;
        for (LocalDate dia : dias) {
            if (dias.contains(dia.minusDays(1))) continue;
            long tamanho = 1;
            while (dias.contains(dia.plusDays(tamanho))) tamanho++;
            melhor = Math.max(melhor, tamanho);
        }
        return melhor;
    }

    private long calcularSequenciaSemanal(List<Tarefa> concluidas, LocalDate inicioSemana, long tarefasSemana) {
        Map<LocalDate, Long> porSemana = concluidas.stream().collect(Collectors.groupingBy(
                tarefa -> tarefa.getDataConclusao().toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                Collectors.counting()));
        LocalDate semana = tarefasSemana >= META_TAREFAS_SEMANAL ? inicioSemana : inicioSemana.minusWeeks(1);
        long sequencia = 0;
        while (porSemana.getOrDefault(semana, 0L) >= META_TAREFAS_SEMANAL) {
            sequencia++;
            semana = semana.minusWeeks(1);
        }
        return sequencia;
    }
}
