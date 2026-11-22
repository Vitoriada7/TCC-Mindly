package com.example.tcc_backend.service;

import com.example.tcc_backend.domain.ConexaoGoogle;
import com.example.tcc_backend.controller.response.GoogleTasksEstadoResponse;
import com.example.tcc_backend.domain.Tarefa;
import com.example.tcc_backend.repository.ConexaoGoogleRepository;
import com.example.tcc_backend.repository.TarefaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.tcc_backend.mapper.GoogleTarefaMapper;
import com.example.tcc_backend.service.googletasks.dto.GoogleItemResponse;
import com.example.tcc_backend.service.googletasks.dto.GoogleListaRequest;
import com.example.tcc_backend.service.googletasks.dto.GoogleTarefaRequest;
import com.example.tcc_backend.service.googletasks.dto.GoogleTokenResponse;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
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
        this.conexoes = conexoes;
        this.tarefas = tarefas;
        this.usuarios = usuarios;
        this.google = google;
        this.cipher = cipher;
    }

    public GoogleTasksEstadoResponse estado() {
        ConexaoGoogle conexao = conexoes.findById(usuarioId()).orElse(null);
        return new GoogleTasksEstadoResponse(google.configurado() && cipher.configurado(),
                conexao != null && conexao.getRefreshTokenCifrado() != null,
                conexao == null ? null : conexao.getUltimaSincronizacao());
    }

    public synchronized String autorizar() {
        verificarConfiguracao();

        Long usuarioId = usuarioId();
        ConexaoGoogle conexao = conexoes.findById(usuarioId).orElseGet(() -> {
            ConexaoGoogle nova = new ConexaoGoogle();
            nova.setUsuarioId(usuarioId);
            return nova;
        });

        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);

        String estado = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        conexao.setEstadoHash(hash(estado));
        conexao.setEstadoExpira(Instant.now().plusSeconds(600));
        conexoes.saveAndFlush(conexao);

        return google.autorizacao(estado);
    }

    public synchronized void conectar(String codigo, String estado) {
        verificarConfiguracao();

        ConexaoGoogle conexao = conexoes.findById(usuarioId()).orElseThrow(() -> invalido());

        if (conexao.getEstadoHash() == null || conexao.getEstadoExpira() == null ||
                !conexao.getEstadoExpira().isAfter(Instant.now()) ||
                !MessageDigest.isEqual(conexao.getEstadoHash().getBytes(StandardCharsets.UTF_8), hash(estado).getBytes(StandardCharsets.UTF_8)))
            throw invalido();

        conexao.setEstadoHash(null);
        conexao.setEstadoExpira(null);
        conexao = conexoes.saveAndFlush(conexao);

        GoogleTokenResponse tokens = google.trocarCodigo(codigo);

        if (!Arrays.asList(Objects.toString(tokens.scope(), "").split(" ")).contains(GoogleTasksClient.ESCOPO))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Autorize o acesso às tarefas para conectar sua conta");

        String refresh = Objects.toString(tokens.refreshToken(), "");

        if (refresh.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reconecte sua conta para autorizar o acesso offline");

        conexao.setRefreshTokenCifrado(cipher.cifrar(refresh));
        conexao.setListaId(null);
        conexao.getTarefas().clear();
        conexao.setUltimaSincronizacao(null);
        conexoes.saveAndFlush(conexao);
    }

    public synchronized int sincronizar() {
        verificarConfiguracao();

        Long usuarioId = usuarioId();
        ConexaoGoogle conexao = conexoes.findById(usuarioId).filter(c -> c.getRefreshTokenCifrado() != null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Conecte sua conta Google primeiro"));
        GoogleTokenResponse token = google.renovar(cipher.decifrar(conexao.getRefreshTokenCifrado()));
        String acesso = textoObrigatorio(token.accessToken());

        if (conexao.getListaId() == null) {
            GoogleItemResponse lista = google.criarLista(acesso, new GoogleListaRequest("Mindly"));
            conexao.setListaId(textoObrigatorio(lista.id()));
            conexao = conexoes.saveAndFlush(conexao);
        }

        String caminho = "lists/" + GoogleTasksClient.encode(conexao.getListaId()) + "/tasks";
        List<Tarefa> locais = tarefas.findAllByUsuarioIdOrderByDataCriacaoDesc(usuarioId);
        List<Long> ids = locais.stream().map(Tarefa::getId).sorted().toList();

        for (Tarefa tarefa : locais) {
            GoogleTarefaRequest corpo = GoogleTarefaMapper.toRequest(tarefa);
            String googleId = conexao.getTarefas().get(tarefa.getId());
            GoogleItemResponse resultado;

            if (googleId == null) {
                resultado = google.criarTarefa(caminho, acesso, corpo);
            } else {
                corpo = corpo.comId(googleId);

                try {
                    resultado = google.atualizarTarefa(caminho + "/" + GoogleTasksClient.encode(googleId), acesso, corpo);
                } catch (ResponseStatusException e) {
                    if (e.getStatusCode() != HttpStatus.NOT_FOUND)
                        throw e;

                    corpo = corpo.comId(null);
                    resultado = google.criarTarefa(caminho, acesso, corpo);
                }
            }

            conexao.getTarefas().put(tarefa.getId(), textoObrigatorio(resultado.id()));
            conexao = conexoes.saveAndFlush(conexao);
        }

        for (Long id : new ArrayList<>(conexao.getTarefas().keySet())) {
            if (Collections.binarySearch(ids, id) >= 0)
                continue;

            try {
                google.excluirTarefa(caminho + "/" + GoogleTasksClient.encode(conexao.getTarefas().get(id)), acesso);
            } catch (ResponseStatusException e) {
                if (e.getStatusCode() != HttpStatus.NOT_FOUND)
                    throw e;
            }

            conexao.getTarefas().remove(id);
            conexao = conexoes.saveAndFlush(conexao);
        }

        conexao.setUltimaSincronizacao(Instant.now());
        conexoes.saveAndFlush(conexao);

        return locais.size();
    }

    public synchronized void desconectar() {
        ConexaoGoogle conexao = conexoes.findById(usuarioId()).orElse(null);

        if (conexao == null)
            return;
        if (conexao.getRefreshTokenCifrado() != null)
            google.revogar(cipher.decifrar(conexao.getRefreshTokenCifrado()));
        conexoes.delete(conexao);
    }

    private Long usuarioId() {
        return usuarios.obterUsuarioAutenticado().getId();
    }

    private void verificarConfiguracao() {
        if (!google.configurado() || !cipher.configurado())
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Integração Google ainda não configurada");
    }

    private static ResponseStatusException invalido() {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Conexão expirada ou inválida. Inicie novamente pelo Mindly.");
    }

    private static String textoObrigatorio(String valor) {
        if (valor == null || valor.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Resposta inválida do Google");

        return valor;
    }

    private static String hash(String texto) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(texto.getBytes(StandardCharsets.UTF_8)));
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

}
