package com.example.tcc_backend.service;

import com.example.tcc_backend.validator.CategoriaValidator;
import com.example.tcc_backend.controller.request.CategoriaRequest;
import com.example.tcc_backend.controller.response.CategoriaResponse;
import com.example.tcc_backend.domain.Categoria;
import com.example.tcc_backend.domain.Usuario;
import com.example.tcc_backend.mapper.CategoriaMapper;
import com.example.tcc_backend.repository.CategoriaRepository;
import com.example.tcc_backend.repository.TarefaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    private final TarefaRepository tarefaRepository;

    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public CategoriaService(CategoriaRepository categoriaRepository, TarefaRepository tarefaRepository,
                            UsuarioAutenticadoService usuarioAutenticadoService) {
        this.categoriaRepository = categoriaRepository;
        this.tarefaRepository = tarefaRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @Transactional
    public CategoriaResponse criar(CategoriaRequest request) {
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        Categoria categoria = Categoria.builder()
                .nome(CategoriaValidator.normalizarNome(request.getNome()))
                .usuario(usuario)
                .build();

        return CategoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        return categoriaRepository.findAllByUsuarioIdOrderByNomeAsc(usuario.getId())
                .stream()
                .map(CategoriaMapper::toResponse)
                .toList();
    }

    @Transactional
    public CategoriaResponse atualizar(Long id, CategoriaRequest request) {
        Categoria categoria = buscarCategoriaDoUsuario(id);
        categoria.setNome(CategoriaValidator.normalizarNome(request.getNome()));
        return CategoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public void excluir(Long id) {
        Categoria categoria = buscarCategoriaDoUsuario(id);

        CategoriaValidator.validarExclusao(tarefaRepository.existsByCategoriaId(categoria.getId()));

        categoriaRepository.delete(categoria);
    }

    private Categoria buscarCategoriaDoUsuario(Long id) {
        Usuario usuario = usuarioAutenticadoService.obterUsuarioAutenticado();
        return categoriaRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));
    }

}
