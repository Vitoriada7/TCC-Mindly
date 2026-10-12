package com.example.tcc_backend.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GoogleTasksConectarRequest(@NotBlank @Size(max = 4096) String code,
                                        @NotBlank @Size(max = 200) String state) { }
