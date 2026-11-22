package com.example.tcc_backend.service.googletasks.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleTokenResponse(@JsonProperty("access_token") String accessToken,
                                  @JsonProperty("refresh_token") String refreshToken, String scope) {
    @Override
    public String toString() {
        return "GoogleTokenResponse[credenciais omitidas]";
    }
}
