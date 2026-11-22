package com.example.tcc_backend.service.googletasks.dto;

public record GoogleRevogarRequest(String token) {
    @Override
    public String toString() {
        return "GoogleRevogarRequest[credenciais omitidas]";
    }
}
