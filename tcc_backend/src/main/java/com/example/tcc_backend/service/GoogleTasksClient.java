package com.example.tcc_backend.service;

import com.example.tcc_backend.service.googletasks.dto.GoogleAutorizacaoRequest;
import com.example.tcc_backend.service.googletasks.dto.GoogleItemResponse;
import com.example.tcc_backend.service.googletasks.dto.GoogleListaRequest;
import com.example.tcc_backend.service.googletasks.dto.GoogleRevogarRequest;
import com.example.tcc_backend.service.googletasks.dto.GoogleTarefaRequest;
import com.example.tcc_backend.service.googletasks.dto.GoogleTokenRequest;
import com.example.tcc_backend.service.googletasks.dto.GoogleTokenResponse;
import com.example.tcc_backend.service.integracao.JsonIntegracao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GoogleTasksClient {

    public static final String ESCOPO = "https://www.googleapis.com/auth/tasks";

    private final String clientId;

    private final String secret;

    private final String redirectUri;

    private final ObjectMapper mapper;

    private final HttpClient http;

    @Autowired
    public GoogleTasksClient(@Value("${google.tasks.client-id:}") String clientId,
                             @Value("${google.tasks.client-secret:}") String secret,
                             @Value("${google.tasks.redirect-uri:http://localhost:4200/tarefas}") String redirectUri,
                             ObjectMapper mapper) {
        this(clientId, secret, redirectUri, mapper,
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build());
    }

    GoogleTasksClient(String clientId, String secret, String redirectUri, ObjectMapper mapper, HttpClient http) {
        this.clientId = clientId;
        this.secret = secret;
        this.redirectUri = redirectUri;
        this.mapper = JsonIntegracao.mapperEstrito(mapper);
        this.http = http;
    }

    public boolean configurado() {
        return !clientId.isBlank() && !secret.isBlank();
    }

    public String autorizacao(String estado) {
        GoogleAutorizacaoRequest request = new GoogleAutorizacaoRequest(clientId, redirectUri, "code",
                ESCOPO, "offline", "consent", estado);
        return "https://accounts.google.com/o/oauth2/v2/auth?" + form(request);
    }

    public GoogleTokenResponse trocarCodigo(String codigo) {
        return token(new GoogleTokenRequest(clientId, secret, "authorization_code", codigo, redirectUri, null));
    }

    public GoogleTokenResponse renovar(String refreshToken) {
        return token(new GoogleTokenRequest(clientId, secret, "refresh_token", null, null, refreshToken));
    }

    public void revogar(String token) {
        enviar(HttpRequest.newBuilder(URI.create("https://oauth2.googleapis.com/revoke"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form(new GoogleRevogarRequest(token)))), true, Void.class);
    }

    private GoogleTokenResponse token(GoogleTokenRequest dados) {
        return enviar(HttpRequest.newBuilder(URI.create("https://oauth2.googleapis.com/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form(dados))), false, GoogleTokenResponse.class);
    }

    public GoogleItemResponse criarLista(String token, GoogleListaRequest lista) {
        return enviarJson("POST", "users/@me/lists", token, lista);
    }

    public GoogleItemResponse criarTarefa(String caminho, String token, GoogleTarefaRequest tarefa) {
        return enviarJson("POST", caminho, token, tarefa);
    }

    public GoogleItemResponse atualizarTarefa(String caminho, String token, GoogleTarefaRequest tarefa) {
        return enviarJson("PUT", caminho, token, tarefa);
    }

    public void excluirTarefa(String caminho, String token) {
        enviar(requisicao(caminho, token).DELETE(), false, Void.class);
    }

    private <D> GoogleItemResponse enviarJson(String metodo, String caminho, String token, D dados) {
        HttpRequest.Builder request = requisicao(caminho, token)
                .method(metodo, HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(dados)));
        return enviar(request, false, GoogleItemResponse.class);
    }

    private HttpRequest.Builder requisicao(String caminho, String token) {
        return HttpRequest.newBuilder(URI.create("https://tasks.googleapis.com/tasks/v1/" + caminho))
                .header("Authorization", "Bearer " + token).header("Content-Type", "application/json");
    }

    private <R> R enviar(HttpRequest.Builder request, boolean revogacao, Class<R> tipoResposta) {
        try {
            HttpResponse<String> response = http.send(request.timeout(Duration.ofSeconds(20)).build(),
                    HttpResponse.BodyHandlers.ofString());
            if (revogacao && response.statusCode() == 400)
                return null;

            if (response.statusCode() == 404) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não encontrado no Google");
            }

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "Google indisponível ou permissão expirada. Tente novamente ou reconecte sua conta.");
            }

            if (tipoResposta == Void.class)
                return null;

            R resultado = mapper.readValue(response.body().isBlank() ? "{}" : response.body(), tipoResposta);

            if (resultado == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Resposta inválida do Google");
            }

            return resultado;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Conexão Google interrompida");
        } catch (IOException | JacksonException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Não foi possível acessar o Google. Tente novamente.");
        }
    }

    public static String encode(String texto) {
        return URLEncoder.encode(texto, StandardCharsets.UTF_8);
    }

    private <D> String form(D dados) {
        Map<String, String> campos = mapper.convertValue(dados, new TypeReference<Map<String, String>>() {});
        return campos.entrySet().stream()
                .map(campo -> encode(campo.getKey()) + "=" + encode(campo.getValue()))
                .collect(Collectors.joining("&"));
    }

}
