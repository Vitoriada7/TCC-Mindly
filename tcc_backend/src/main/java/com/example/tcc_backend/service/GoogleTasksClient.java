package com.example.tcc_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
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
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    public GoogleTasksClient(@Value("${google.tasks.client-id:}") String clientId,
                             @Value("${google.tasks.client-secret:}") String secret,
                             @Value("${google.tasks.redirect-uri:http://localhost:4200/tarefas}") String redirectUri,
                             ObjectMapper mapper) {
        this.clientId = clientId; this.secret = secret; this.redirectUri = redirectUri; this.mapper = mapper;
    }
    public boolean configurado() { return !clientId.isBlank() && !secret.isBlank(); }
    public String autorizacao(String estado) {
        return "https://accounts.google.com/o/oauth2/v2/auth?" + form(Map.of(
                "client_id", clientId, "redirect_uri", redirectUri, "response_type", "code",
                "scope", ESCOPO, "access_type", "offline", "prompt", "consent", "state", estado));
    }
    public JsonNode trocarCodigo(String codigo) {
        return token(Map.of("client_id", clientId, "client_secret", secret, "code", codigo,
                "redirect_uri", redirectUri, "grant_type", "authorization_code"));
    }
    public JsonNode renovar(String refreshToken) {
        return token(Map.of("client_id", clientId, "client_secret", secret,
                "refresh_token", refreshToken, "grant_type", "refresh_token"));
    }
    public void revogar(String token) {
        enviar(HttpRequest.newBuilder(URI.create("https://oauth2.googleapis.com/revoke"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form(Map.of("token", token)))), true);
    }
    private JsonNode token(Map<String, String> dados) {
        return enviar(HttpRequest.newBuilder(URI.create("https://oauth2.googleapis.com/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form(dados))), false);
    }
    public JsonNode tarefas(String metodo, String caminho, String token, Object corpo) {
        var request = HttpRequest.newBuilder(URI.create("https://tasks.googleapis.com/tasks/v1/" + caminho))
                .header("Authorization", "Bearer " + token).header("Content-Type", "application/json");
        request.method(metodo, corpo == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(corpo)));
        return enviar(request, false);
    }
    private JsonNode enviar(HttpRequest.Builder request, boolean revogacao) {
        try {
            var response = http.send(request.timeout(Duration.ofSeconds(20)).build(), HttpResponse.BodyHandlers.ofString());
            if (revogacao && response.statusCode() == 400) return mapper.createObjectNode();
            if (response.statusCode() == 404) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não encontrado no Google");
            if (response.statusCode() < 200 || response.statusCode() >= 300)
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Google indisponível ou permissão expirada. Tente novamente ou reconecte sua conta.");
            return response.body().isBlank() ? mapper.createObjectNode() : mapper.readTree(response.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Conexão Google interrompida");
        } catch (java.io.IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Não foi possível acessar o Google. Tente novamente.");
        }
    }
    public static String encode(String texto) { return URLEncoder.encode(texto, StandardCharsets.UTF_8); }
    private static String form(Map<String, String> dados) {
        return dados.entrySet().stream().map(e -> encode(e.getKey()) + "=" + encode(e.getValue())).collect(Collectors.joining("&"));
    }
}
