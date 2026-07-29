package com.example.tcc_backend.service;

import com.example.tcc_backend.controller.request.TarefaRequest;
import com.example.tcc_backend.controller.response.TarefaResponse;
import com.example.tcc_backend.domain.Categoria;
import com.example.tcc_backend.domain.Tarefa;
import com.example.tcc_backend.domain.Usuario;
import com.example.tcc_backend.domain.enums.Prioridade;
import com.example.tcc_backend.domain.enums.StatusTarefa;
import com.example.tcc_backend.mapper.TarefaMapper;
import com.example.tcc_backend.repository.CategoriaRepository;
import com.example.tcc_backend.repository.TarefaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public TarefaService(TarefaRepository tarefaRepository, CategoriaRepository categoriaRepository,
                         UsuarioAutenticadoService usuarioAutenticadoService) {
        this.tarefaRepository = tarefaRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @Transactional
    public TarefaResponse criar(TarefaRequest request) {
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        Categoria categoria = categoriaRepository.findByIdAndUsuarioId(request.getCategoriaId(), usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));

        LocalDateTime agora = LocalDateTime.now();
        Tarefa tarefa = TarefaMapper.toEntity(request);
        tarefa.setUsuario(usuario);
        tarefa.setCategoria(categoria);
        tarefa.setStatus(StatusTarefa.PENDENTE);
        tarefa.setDataCriacao(agora);
        tarefa.setDataAtualizacao(agora);

        return TarefaMapper.toResponse(tarefaRepository.save(tarefa));
    }

    @Transactional
    public TarefaResponse atualizar(Long id, TarefaRequest request) {
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        Tarefa tarefa = tarefaRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada"));
        Categoria categoria = categoriaRepository.findByIdAndUsuarioId(request.getCategoriaId(), usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));

        TarefaMapper.atualizarEntidade(tarefa, request);
        tarefa.setCategoria(categoria);
        tarefa.setDataAtualizacao(LocalDateTime.now());

        return TarefaMapper.toResponse(tarefaRepository.save(tarefa));
    }

    @Transactional
    public TarefaResponse concluir(Long id) {
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        Tarefa tarefa = tarefaRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada"));

        if (tarefa.getStatus() == StatusTarefa.CONCLUIDA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A tarefa já está concluída");
        }

        LocalDateTime agora = LocalDateTime.now();
        tarefa.setStatus(StatusTarefa.CONCLUIDA);
        tarefa.setDataConclusao(agora);
        tarefa.setDataAtualizacao(agora);

        return TarefaMapper.toResponse(tarefaRepository.save(tarefa));
    }

    @Transactional
    public void excluir(Long id) {
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        Tarefa tarefa = tarefaRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada"));

        tarefaRepository.delete(tarefa);
    }

    @Transactional(readOnly = true)
    public List<TarefaResponse> listar() {
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        return tarefaRepository.findAllByUsuarioIdOrderByDataCriacaoDesc(usuario.getId())
                .stream()
                .map(TarefaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TarefaResponse> listar(StatusTarefa status, Prioridade prioridade, Long categoriaId, Boolean vencida) {
        if (status == null && prioridade == null && categoriaId == null && vencida == null) {
            return listar();
        }

        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        Specification<Tarefa> especificacao = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("usuario").get("id"), usuario.getId());

        if (status != null) {
            especificacao = especificacao.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), status));
        }

        if (prioridade != null) {
            especificacao = especificacao.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("prioridade"), prioridade));
        }

        if (categoriaId != null) {
            especificacao = especificacao.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("categoria").get("id"), categoriaId));
        }

        if (Boolean.TRUE.equals(vencida)) {
            LocalDateTime agora = LocalDateTime.now();
            especificacao = especificacao.and((root, query, criteriaBuilder) -> criteriaBuilder.and(
                    criteriaBuilder.notEqual(root.get("status"), StatusTarefa.CONCLUIDA),
                    criteriaBuilder.isNotNull(root.get("dataLimite")),
                    criteriaBuilder.lessThan(root.get("dataLimite"), agora)));
        }

        return tarefaRepository.findAll(especificacao, Sort.by(Sort.Direction.DESC, "dataCriacao"))
                .stream()
                .map(TarefaMapper::toResponse)
                .toList();
    }
}
