package com.example.tcc_backend.service;

import com.example.tcc_backend.domain.ConexaoGoogle;
import com.example.tcc_backend.controller.response.GoogleTasksEstadoResponse;
import com.example.tcc_backend.domain.Tarefa;
import com.example.tcc_backend.domain.enums.StatusTarefa;
import com.example.tcc_backend.repository.ConexaoGoogleRepository;
import com.example.tcc_backend.repository.TarefaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JsonNode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

@Service
public class GoogleTasksService {
    private final ConexaoGoogleRepository conexoes;
    private final TarefaRepository tarefas;
    private final UsuarioAutenticadoService usuarios;
    private final GoogleTasksClient google;
    private final GoogleTokenCipher cipher;
    public GoogleTasksService(ConexaoGoogleRepository conexoes, TarefaRepository tarefas,
                              UsuarioAutenticadoService usuarios, GoogleTasksClient google, GoogleTokenCipher cipher) {
        this.conexoes = conexoes; this.tarefas = tarefas; this.usuarios = usuarios; this.google = google; this.cipher = cipher;
    }
    public GoogleTasksEstadoResponse estado() {
        var conexao = conexoes.findById(usuarioId()).orElse(null);
        return new GoogleTasksEstadoResponse(google.configurado() && cipher.configurado(), conexao != null && conexao.getRefreshTokenCifrado() != null,
                conexao == null ? null : conexao.getUltimaSincronizacao());
    }
    public synchronized String autorizar() {
        verificarConfiguracao();
        Long usuarioId = usuarioId();
        var conexao = conexoes.findById(usuarioId).orElseGet(() -> {
            var nova = new ConexaoGoogle(); nova.setUsuarioId(usuarioId); return nova;
        });
        byte[] bytes = new byte[32]; new SecureRandom().nextBytes(bytes);
        String estado = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        conexao.setEstadoHash(hash(estado));
        conexao.setEstadoExpira(Instant.now().plusSeconds(600));
        conexoes.saveAndFlush(conexao);
        return google.autorizacao(estado);
    }
    public synchronized void conectar(String codigo, String estado) {
        verificarConfiguracao();
        var conexao = conexoes.findById(usuarioId()).orElseThrow(() -> invalido());
        if (conexao.getEstadoHash() == null || conexao.getEstadoExpira() == null ||
                !conexao.getEstadoExpira().isAfter(Instant.now()) ||
                !MessageDigest.isEqual(conexao.getEstadoHash().getBytes(StandardCharsets.UTF_8), hash(estado).getBytes(StandardCharsets.UTF_8)))
            throw invalido();
        conexao.setEstadoHash(null); conexao.setEstadoExpira(null);
        conexao = conexoes.saveAndFlush(conexao);
        var tokens = google.trocarCodigo(codigo);
        if (!Arrays.asList(tokens.path("scope").asText().split(" ")).contains(GoogleTasksClient.ESCOPO))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Autorize o acesso às tarefas para conectar sua conta");
        String refresh = tokens.path("refresh_token").asText();
        if (refresh.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reconecte sua conta para autorizar o acesso offline");
        conexao.setRefreshTokenCifrado(cipher.cifrar(refresh));
        conexao.setListaId(null); conexao.getTarefas().clear(); conexao.setUltimaSincronizacao(null);
        conexoes.saveAndFlush(conexao);
    }
    public synchronized int sincronizar() {
        verificarConfiguracao();
        Long usuarioId = usuarioId();
        var conexao = conexoes.findById(usuarioId).filter(c -> c.getRefreshTokenCifrado() != null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Conecte sua conta Google primeiro"));
        var token = google.renovar(cipher.decifrar(conexao.getRefreshTokenCifrado()));
        String acesso = textoObrigatorio(token, "access_token");
        if (conexao.getListaId() == null) {
            var lista = google.tarefas("POST", "users/@me/lists", acesso, Map.of("title", "Mindly"));
            conexao.setListaId(textoObrigatorio(lista, "id"));
            conexao = conexoes.saveAndFlush(conexao);
        }
        String caminho = "lists/" + GoogleTasksClient.encode(conexao.getListaId()) + "/tasks";
        List<Tarefa> locais = tarefas.findAllByUsuarioIdOrderByDataCriacaoDesc(usuarioId);
        Set<Long> ids = new HashSet<>();
        for (Tarefa tarefa : locais) {
            ids.add(tarefa.getId());
            Map<String, Object> corpo = corpoTarefa(tarefa);
            String googleId = conexao.getTarefas().get(tarefa.getId());
            JsonNode resultado;
            if (googleId == null) {
                resultado = google.tarefas("POST", caminho, acesso, corpo);
            } else {
                corpo.put("id", googleId);
                try { resultado = google.tarefas("PUT", caminho + "/" + GoogleTasksClient.encode(googleId), acesso, corpo); }
                catch (ResponseStatusException e) {
                    if (e.getStatusCode() != HttpStatus.NOT_FOUND) throw e;
                    corpo.remove("id");
                    resultado = google.tarefas("POST", caminho, acesso, corpo);
                }
            }
            conexao.getTarefas().put(tarefa.getId(), textoObrigatorio(resultado, "id"));
            conexao = conexoes.saveAndFlush(conexao);
        }
        for (Long id : new HashSet<>(conexao.getTarefas().keySet())) {
            if (ids.contains(id)) continue;
            try { google.tarefas("DELETE", caminho + "/" + GoogleTasksClient.encode(conexao.getTarefas().get(id)), acesso, null); }
            catch (ResponseStatusException e) { if (e.getStatusCode() != HttpStatus.NOT_FOUND) throw e; }
            conexao.getTarefas().remove(id);
            conexao = conexoes.saveAndFlush(conexao);
        }
        conexao.setUltimaSincronizacao(Instant.now());
        conexoes.saveAndFlush(conexao);
        return locais.size();
    }
    static Map<String, Object> corpoTarefa(Tarefa tarefa) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("title", tarefa.getTitulo());
        corpo.put("notes", tarefa.getDescricao() == null ? "" : tarefa.getDescricao());
        corpo.put("status", tarefa.getStatus() == StatusTarefa.CONCLUIDA ? "completed" : "needsAction");
        corpo.put("due", tarefa.getDataLimite() == null ? null : tarefa.getDataLimite().toLocalDate() + "T00:00:00.000Z");
        corpo.put("completed", tarefa.getDataConclusao() == null ? null : tarefa.getDataConclusao().atZone(ZoneId.of("America/Sao_Paulo")).toInstant().toString());
        return corpo;
    }
    public synchronized void desconectar() {
        var conexao = conexoes.findById(usuarioId()).orElse(null);
        if (conexao == null) return;
        if (conexao.getRefreshTokenCifrado() != null) google.revogar(cipher.decifrar(conexao.getRefreshTokenCifrado()));
        conexoes.delete(conexao);
    }
    private Long usuarioId() { return usuarios.obterUsuarioAutenticado().getId(); }
    private void verificarConfiguracao() {
        if (!google.configurado() || !cipher.configurado())
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Integração Google ainda não configurada");
    }
    private static ResponseStatusException invalido() {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Conexão expirada ou inválida. Inicie novamente pelo Mindly.");
    }
    private static String textoObrigatorio(JsonNode node, String campo) {
        String valor = node.path(campo).asText();
        if (valor.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Resposta inválida do Google");
        return valor;
    }
    private static String hash(String texto) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(texto.getBytes(StandardCharsets.UTF_8))); }
        catch (java.security.NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
    }
}
