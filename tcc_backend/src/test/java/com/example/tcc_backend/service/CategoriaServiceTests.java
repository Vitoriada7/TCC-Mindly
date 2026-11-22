package com.example.tcc_backend.service;

import com.example.tcc_backend.controller.request.CategoriaRequest;
import com.example.tcc_backend.domain.Categoria;
import com.example.tcc_backend.domain.Usuario;
import com.example.tcc_backend.repository.CategoriaRepository;
import com.example.tcc_backend.repository.TarefaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTests {

    @Mock
    CategoriaRepository categoriaRepository;

    @Mock
    TarefaRepository tarefaRepository;

    @Mock
    UsuarioAutenticadoService usuarioAutenticadoService;

    @InjectMocks
    CategoriaService service;

    @Test
    void bloqueiaExclusaoDeCategoriaComTarefas() {
        Categoria categoria = Categoria.builder().id(2L).build();
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(Usuario.builder().id(7L).build());
        when(categoriaRepository.findByIdAndUsuarioId(2L, 7L)).thenReturn(Optional.of(categoria));
        when(tarefaRepository.existsByCategoriaId(2L)).thenReturn(true);

        assertThatThrownBy(() -> service.excluir(2L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(erro -> assertThat(((ResponseStatusException) erro).getStatusCode().value()).isEqualTo(409));
        verify(categoriaRepository, never()).delete(any());
    }

    @Test
    void validaTamanhoDoNomeDepoisDeRemoverEspacosSemSalvar() {
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(Usuario.builder().id(7L).build());
        for (String nome : new String[]{null, "   ", " a ", "a".repeat(51)}) {
            CategoriaRequest request = new CategoriaRequest();
            request.setNome(nome);
            assertThatThrownBy(() -> service.criar(request))
                    .isInstanceOf(ResponseStatusException.class).hasMessageContaining("entre 2 e 50");
        }
        verifyNoInteractions(categoriaRepository);
    }

    @Test
    void permiteNomeNoLimiteSemEspacosExternos() {
        Usuario usuario = Usuario.builder().id(7L).build();
        when(usuarioAutenticadoService.obterUsuarioAutenticado()).thenReturn(usuario);
        when(categoriaRepository.save(any())).thenAnswer(invocacao -> invocacao.getArgument(0));
        CategoriaRequest request = new CategoriaRequest();
        request.setNome("  " + "a".repeat(50) + "  ");

        assertThat(service.criar(request).getNome()).isEqualTo("a".repeat(50));
    }

}
