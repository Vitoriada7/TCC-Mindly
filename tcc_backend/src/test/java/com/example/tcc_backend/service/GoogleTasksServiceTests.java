package com.example.tcc_backend.service;

import com.example.tcc_backend.domain.ConexaoGoogle;
import com.example.tcc_backend.domain.Tarefa;
import com.example.tcc_backend.domain.Usuario;
import com.example.tcc_backend.domain.enums.StatusTarefa;
import com.example.tcc_backend.repository.ConexaoGoogleRepository;
import com.example.tcc_backend.repository.TarefaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleTasksServiceTests {
    @Mock ConexaoGoogleRepository conexoes;
    @Mock TarefaRepository tarefas;
    @Mock UsuarioAutenticadoService usuarios;
    @Mock GoogleTasksClient google;
    @Mock GoogleTokenCipher cipher;
    @InjectMocks GoogleTasksService service;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void usuario() { lenient().when(usuarios.obterUsuarioAutenticado()).thenReturn(Usuario.builder().id(7L).build()); }
    private void configurado() { when(google.configurado()).thenReturn(true); when(cipher.configurado()).thenReturn(true); }
    private ConexaoGoogle conexao() {
        var conexao = new ConexaoGoogle(); conexao.setUsuarioId(7L); return conexao;
    }
    @Test
    void deveRejeitarCallbackSemEstadoValido() {
        configurado();
        when(conexoes.findById(7L)).thenReturn(Optional.of(conexao()));
        assertThatThrownBy(() -> service.conectar("codigo", "forjado")).isInstanceOf(ResponseStatusException.class);
        verify(google, never()).trocarCodigo(anyString());
        verify(conexoes, never()).saveAndFlush(any());
    }
    @Test
    void deveConsumirEstadoAntesDeTrocarCodigoEImpedirReuso() {
        configurado();
        var conexao = conexao();
        when(conexoes.findById(7L)).thenReturn(Optional.of(conexao));
        when(conexoes.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        when(google.autorizacao(anyString())).thenAnswer(i -> i.getArgument(0));
        String estado = service.autorizar();
        assertThat(conexao.getEstadoHash()).isNotEqualTo(estado);
        when(google.trocarCodigo("codigo")).thenReturn(mapper.readTree("{\"scope\":\"https://www.googleapis.com/auth/tasks\",\"refresh_token\":\"token\"}"));
        when(cipher.cifrar("token")).thenReturn("cifrado");
        service.conectar("codigo", estado);
        assertThat(conexao.getEstadoHash()).isNull();
        assertThat(conexao.getRefreshTokenCifrado()).isEqualTo("cifrado");
        assertThatThrownBy(() -> service.conectar("codigo", estado)).isInstanceOf(ResponseStatusException.class);
        verify(google, times(1)).trocarCodigo("codigo");
    }
    @Test
    void deveRejeitarEstadoExpirado() {
        configurado();
        var conexao = conexao();
        when(conexoes.findById(7L)).thenReturn(Optional.of(conexao));
        when(conexoes.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        when(google.autorizacao(anyString())).thenAnswer(i -> i.getArgument(0));
        String estado = service.autorizar();
        conexao.setEstadoExpira(Instant.now().minusSeconds(1));
        assertThatThrownBy(() -> service.conectar("codigo", estado)).isInstanceOf(ResponseStatusException.class);
        verify(google, never()).trocarCodigo(anyString());
    }
    @Test
    void deveSincronizarSomenteTarefasDoUsuarioEExcluirSomenteVinculosConhecidos() {
        configurado();
        var conexao = conexao(); conexao.setRefreshTokenCifrado("cifrado"); conexao.setListaId("lista");
        conexao.getTarefas().put(10L, "existente"); conexao.getTarefas().put(11L, "excluida");
        when(conexoes.findById(7L)).thenReturn(Optional.of(conexao));
        when(conexoes.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        when(cipher.decifrar("cifrado")).thenReturn("refresh");
        when(google.renovar("refresh")).thenReturn(mapper.readTree("{\"access_token\":\"acesso\"}"));
        when(tarefas.findAllByUsuarioIdOrderByDataCriacaoDesc(7L)).thenReturn(List.of(
                Tarefa.builder().id(10L).titulo("Estudar").status(StatusTarefa.PENDENTE).build()));
        when(google.tarefas(eq("PUT"), eq("lists/lista/tasks/existente"), eq("acesso"), any()))
                .thenReturn(mapper.readTree("{\"id\":\"existente\"}"));
        assertThat(service.sincronizar()).isEqualTo(1);
        assertThat(conexao.getTarefas()).containsOnlyKeys(10L);
        verify(google).tarefas("DELETE", "lists/lista/tasks/excluida", "acesso", null);
        verify(tarefas, never()).findAll();
    }
    @Test
    void deveRevogarAntesDeRemoverConexao() {
        var conexao = conexao(); conexao.setRefreshTokenCifrado("cifrado");
        when(conexoes.findById(7L)).thenReturn(Optional.of(conexao));
        when(cipher.decifrar("cifrado")).thenReturn("refresh");
        service.desconectar();
        var ordem = inOrder(google, conexoes);
        ordem.verify(google).revogar("refresh"); ordem.verify(conexoes).delete(conexao);
    }
    @Test
    void devePreservarDataDoPrazoSemConverterParaOutroDia() {
        var tarefa = Tarefa.builder().titulo("Estudar").status(StatusTarefa.PENDENTE)
                .dataLimite(LocalDateTime.of(2026, 10, 12, 23, 30)).build();
        assertThat(GoogleTasksService.corpoTarefa(tarefa)).containsEntry("due", "2026-10-12T00:00:00.000Z")
                .containsEntry("status", "needsAction").containsEntry("completed", null);
    }
}
