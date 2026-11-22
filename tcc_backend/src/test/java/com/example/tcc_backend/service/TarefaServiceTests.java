package com.example.tcc_backend.service;

import com.example.tcc_backend.controller.response.TarefaResponse;
import com.example.tcc_backend.controller.request.TarefaRequest;
import com.example.tcc_backend.domain.Categoria;
import com.example.tcc_backend.domain.Tarefa;
import com.example.tcc_backend.domain.Usuario;
import com.example.tcc_backend.domain.enums.StatusTarefa;
import com.example.tcc_backend.repository.CategoriaRepository;
import com.example.tcc_backend.repository.TarefaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTests {

    @Mock
    TarefaRepository tarefaRepository;

    @Mock
    CategoriaRepository categoriaRepository;

    @Mock
    UsuarioAutenticadoService usuarioAutenticadoService;

    @InjectMocks
    TarefaService service;

    @Test
    void bloqueiaConclusaoRepetidaSemAlterarDatasOuSalvar() {
        Tarefa tarefa = new Tarefa();
        tarefa.setStatus(StatusTarefa.CONCLUIDA);
        LocalDateTime conclusao = LocalDateTime.of(2026, 10, 1, 12, 0);
        tarefa.setDataConclusao(conclusao);
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(Usuario.builder().id(7L).build());
        when(tarefaRepository.findByIdAndUsuarioId(1L, 7L)).thenReturn(Optional.of(tarefa));

        assertThatThrownBy(() -> service.concluir(1L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(erro -> assertThat(((ResponseStatusException) erro).getStatusCode().value()).isEqualTo(409));
        assertThat(tarefa.getDataConclusao()).isEqualTo(conclusao);
        verify(tarefaRepository, never()).save(any());
    }

    @Test
    void concluiTarefaPendenteComDatasConsistentes() {
        Tarefa tarefa = new Tarefa();
        tarefa.setStatus(StatusTarefa.PENDENTE);
        tarefa.setCategoria(Categoria.builder().id(2L).build());
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(Usuario.builder().id(7L).build());
        when(tarefaRepository.findByIdAndUsuarioId(1L, 7L)).thenReturn(Optional.of(tarefa));
        when(tarefaRepository.save(tarefa)).thenReturn(tarefa);

        TarefaResponse resposta = service.concluir(1L);

        assertThat(resposta.getStatus()).isEqualTo(StatusTarefa.CONCLUIDA);
        assertThat(resposta.getDataConclusao()).isNotNull().isEqualTo(resposta.getDataAtualizacao());
        assertThat(resposta.getCategoriaId()).isEqualTo(2L);
    }

    @Test
    void bloqueiaConclusaoDeTarefaDeOutroUsuario() {
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(Usuario.builder().id(7L).build());
        when(tarefaRepository.findByIdAndUsuarioId(1L, 7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.concluir(1L))
                .isInstanceOf(ResponseStatusException.class).hasMessageContaining("Tarefa não encontrada");
        verify(tarefaRepository, never()).save(any());
    }

    @Test
    void bloqueiaCategoriaDeOutroUsuarioAntesDeAlterarTarefa() {
        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo("Título original");
        TarefaRequest request = new TarefaRequest();
        request.setCategoriaId(2L);
        request.setTitulo("Outro título");
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(Usuario.builder().id(7L).build());
        when(tarefaRepository.findByIdAndUsuarioId(1L, 7L)).thenReturn(Optional.of(tarefa));
        when(categoriaRepository.findByIdAndUsuarioId(2L, 7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizar(1L, request))
                .isInstanceOf(ResponseStatusException.class).hasMessageContaining("Categoria não encontrada");
        assertThat(tarefa.getTitulo()).isEqualTo("Título original");
        verify(tarefaRepository, never()).save(any());
    }

}
