package com.example.tcc_backend.mapper;

import com.example.tcc_backend.controller.response.CategoriaResponse;
import com.example.tcc_backend.domain.Categoria;

public class CategoriaMapper {

    private CategoriaMapper() {
    }

    public static CategoriaResponse toResponse(Categoria categoria) {
        CategoriaResponse response = new CategoriaResponse();
        response.setId(categoria.getId());
        response.setNome(categoria.getNome());
        return response;
    }
}
