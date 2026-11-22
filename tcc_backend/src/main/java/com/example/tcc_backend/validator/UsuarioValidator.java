package com.example.tcc_backend.validator;

import com.example.tcc_backend.domain.Usuario;
import com.example.tcc_backend.domain.enums.Avatar;

public final class UsuarioValidator {

    private UsuarioValidator() {}

    public static void validarEmailDisponivel(boolean emailCadastrado) {
        if (emailCadastrado) {
            throw new IllegalArgumentException("E-mail ja cadastrado");
        }
    }

    public static void validarAvatarLiberado(Usuario usuario, Avatar avatar) {
        if (avatar != null && !usuario.getAvataresLiberados().contains(avatar)) {
            throw new IllegalArgumentException("Conclua a conquista para liberar este avatar.");
        }
    }

}
