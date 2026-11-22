package com.example.tcc_backend.service.googletasks.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GoogleTokenRequest(@JsonProperty("client_id") String clientId,
                                 @JsonProperty("client_secret") String clientSecret,
                                 @JsonProperty("grant_type") String grantType, String code,
                                 @JsonProperty("redirect_uri") String redirectUri,
                                 @JsonProperty("refresh_token") String refreshToken) {
    @Override
    public String toString() {
        return "GoogleTokenRequest[credenciais omitidas]";
    }
}
