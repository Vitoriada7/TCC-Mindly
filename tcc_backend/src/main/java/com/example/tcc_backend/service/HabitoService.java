package com.example.tcc_backend.service;

import com.example.tcc_backend.controller.request.HabitoRequest;
import com.example.tcc_backend.controller.response.HabitoResponse;
import com.example.tcc_backend.controller.response.ResumoHabitosResponse;
import com.example.tcc_backend.domain.Habito;
import com.example.tcc_backend.domain.RegistroHabito;
import com.example.tcc_backend.domain.Usuario;
import com.example.tcc_backend.mapper.HabitoMapper;
import com.example.tcc_backend.repository.HabitoRepository;
import com.example.tcc_backend.repository.RegistroHabitoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HabitoService {

    private final HabitoRepository habitoRepository;
    private final RegistroHabitoRepository registroHabitoRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public HabitoService(HabitoRepository habitoRepository, RegistroHabitoRepository registroHabitoRepository,
                         UsuarioAutenticadoService usuarioAutenticadoService) {
        this.habitoRepository = habitoRepository;
        this.registroHabitoRepository = registroHabitoRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @Transactional
    public HabitoResponse criar(HabitoRequest request) {
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        Habito habito = HabitoMapper.toEntity(request);
        habito.setUsuario(usuario);
        habito.setAtivo(true);
        habito.setDataCriacao(LocalDateTime.now());
        return HabitoMapper.toResponse(habitoRepository.save(habito), List.of());
    }

    @Transactional(readOnly = true)
    public List<HabitoResponse> listar(LocalDate inicio, LocalDate fim) {
        Periodo periodo = resolverPeriodo(inicio, fim);
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        List<Habito> habitos = habitoRepository.findAllByUsuarioIdAndAtivoTrueOrderByDataCriacaoDesc(usuario.getId());
        if (habitos.isEmpty()) {
            return List.of();
        }

        Collection<Long> ids = habitos.stream().map(Habito::getId).toList();
        Map<Long, List<LocalDate>> diasPorHabito = registroHabitoRepository
                .findAllByHabitoIdInAndDataBetweenAndConcluidoTrue(ids, periodo.inicio(), periodo.fim())
                .stream()
                .collect(Collectors.groupingBy(registro -> registro.getHabito().getId(),
                        Collectors.mapping(RegistroHabito::getData, Collectors.toList())));

        return habitos.stream()
                .map(habito -> HabitoMapper.toResponse(habito, diasPorHabito.getOrDefault(habito.getId(), List.of())))
                .toList();
    }

    @Transactional
    public HabitoResponse atualizar(Long id, HabitoRequest request) {
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        Habito habito = obterDoUsuario(id, usuario.getId());
        HabitoMapper.atualizarEntidade(habito, request);
        return HabitoMapper.toResponse(habitoRepository.save(habito), List.of());
    }

    @Transactional
    public void excluir(Long id) {
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        Habito habito = obterDoUsuario(id, usuario.getId());
        registroHabitoRepository.deleteAllByHabitoId(habito.getId());
        habitoRepository.delete(habito);
    }

    @Transactional
    public void marcarConcluido(Long id, LocalDate data) {
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        Habito habito = obterDoUsuario(id, usuario.getId());
        RegistroHabito registro = registroHabitoRepository.findByHabitoIdAndData(habito.getId(), data)
                .orElseGet(() -> RegistroHabito.builder().habito(habito).data(data).build());
        registro.setConcluido(true);
        registroHabitoRepository.save(registro);
    }

    @Transactional
    public void desmarcarConcluido(Long id, LocalDate data) {
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        Habito habito = obterDoUsuario(id, usuario.getId());
        registroHabitoRepository.findByHabitoIdAndData(habito.getId(), data)
                .ifPresent(registroHabitoRepository::delete);
    }

    @Transactional(readOnly = true)
    public ResumoHabitosResponse obterResumo() {
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        LocalDate hoje = LocalDate.now();
        long concluidosHoje = registroHabitoRepository.countByHabitoUsuarioIdAndDataAndConcluidoTrue(usuario.getId(), hoje);
        long totalAtivos = habitoRepository.countByUsuarioIdAndAtivoTrue(usuario.getId());
        Set<LocalDate> diasComConclusao = registroHabitoRepository
                .findAllByHabitoUsuarioIdAndDataLessThanEqualAndConcluidoTrueOrderByDataDesc(usuario.getId(), hoje)
                .stream()
                .map(RegistroHabito::getData)
                .collect(Collectors.toSet());

        long sequencia = 0;
        for (LocalDate dia = hoje; diasComConclusao.contains(dia); dia = dia.minusDays(1)) {
            sequencia++;
        }
        return new ResumoHabitosResponse(sequencia, concluidosHoje, totalAtivos);
    }

    private Habito obterDoUsuario(Long id, Long usuarioId) {
        return habitoRepository.findByIdAndUsuarioIdAndAtivoTrue(id, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hábito não encontrado"));
    }

    private Periodo resolverPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio != null && fim != null && fim.isBefore(inicio)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A data final não pode ser anterior à data inicial");
        }
        if (inicio != null && fim != null) {
            return new Periodo(inicio, fim);
        }
        LocalDate referencia = inicio != null ? inicio : fim != null ? fim : LocalDate.now();
        LocalDate inicioSemana = referencia.with(DayOfWeek.MONDAY);
        LocalDate fimSemana = inicioSemana.plusDays(6);
        return new Periodo(inicio != null ? inicio : inicioSemana, fim != null ? fim : fimSemana);
    }

    private record Periodo(LocalDate inicio, LocalDate fim) { }
}
