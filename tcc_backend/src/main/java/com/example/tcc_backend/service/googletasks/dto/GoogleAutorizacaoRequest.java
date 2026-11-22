package com.example.tcc_backend.service.googletasks.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleAutorizacaoRequest(@JsonProperty("client_id") String clientId,
                                       @JsonProperty("redirect_uri") String redirectUri,
                                       @JsonProperty("response_type") String responseType, String scope,
                                       @JsonProperty("access_type") String accessType, String prompt, String state) {}
