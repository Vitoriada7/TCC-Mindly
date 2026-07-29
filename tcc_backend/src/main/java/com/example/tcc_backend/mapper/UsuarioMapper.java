package com.example.tcc_backend.mapper;

import com.example.tcc_backend.controller.request.CadastroRequest;
import com.example.tcc_backend.controller.response.UsuarioResponse;
import com.example.tcc_backend.domain.Usuario;

public class UsuarioMapper {

    private UsuarioMapper() {
    }

    public static Usuario toEntity(CadastroRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setApelido(request.getApelido());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(request.getSenha());
        usuario.setDataNascimento(request.getDataNascimento());
        return usuario;
    }

    public static UsuarioResponse toResponse(Usuario usuario) {
        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuario.getId());
        response.setNome(usuario.getNome());
        response.setApelido(usuario.getApelido());
        response.setEmail(usuario.getEmail());
        response.setDataNascimento(usuario.getDataNascimento());
        response.setDataCriacao(usuario.getDataCriacao());
        return response;
    }

}
