package com.example.tcc_backend.service;

import com.example.tcc_backend.repository.specification.TarefaSpecifications;
import com.example.tcc_backend.validator.TarefaValidator;
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
        Categoria categoria = buscarCategoriaDoUsuario(request.getCategoriaId(), usuario.getId());

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
        Tarefa tarefa = buscarTarefaDoUsuario(id, usuario.getId());
        Categoria categoria = buscarCategoriaDoUsuario(request.getCategoriaId(), usuario.getId());

        TarefaMapper.atualizarEntidade(tarefa, request);
        tarefa.setCategoria(categoria);
        tarefa.setDataAtualizacao(LocalDateTime.now());

        return TarefaMapper.toResponse(tarefaRepository.save(tarefa));
    }

    @Transactional
    public TarefaResponse concluir(Long id) {
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        Tarefa tarefa = buscarTarefaDoUsuario(id, usuario.getId());

        TarefaValidator.validarConclusao(tarefa);

        LocalDateTime agora = LocalDateTime.now();
        tarefa.setStatus(StatusTarefa.CONCLUIDA);
        tarefa.setDataConclusao(agora);
        tarefa.setDataAtualizacao(agora);

        return TarefaMapper.toResponse(tarefaRepository.save(tarefa));
    }

    @Transactional
    public void excluir(Long id) {
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        Tarefa tarefa = buscarTarefaDoUsuario(id, usuario.getId());

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
        Specification<Tarefa> especificacao = TarefaSpecifications.comFiltros(usuario.getId(), status, prioridade, categoriaId, vencida);
        return tarefaRepository.findAll(especificacao, Sort.by(Sort.Direction.DESC, "dataCriacao"))
                .stream()
                .map(TarefaMapper::toResponse)
                .toList();
    }

    private Tarefa buscarTarefaDoUsuario(Long id, Long usuarioId) {
        return tarefaRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada"));
    }

    private Categoria buscarCategoriaDoUsuario(Long id, Long usuarioId) {
        return categoriaRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));
    }

}
