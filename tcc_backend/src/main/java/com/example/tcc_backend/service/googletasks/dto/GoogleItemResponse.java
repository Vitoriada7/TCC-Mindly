package com.example.tcc_backend.service.googletasks.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleItemResponse(String id) {}
