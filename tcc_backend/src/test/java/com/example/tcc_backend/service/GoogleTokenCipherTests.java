package com.example.tcc_backend.service;

import org.junit.jupiter.api.Test;
import java.util.Base64;
import static org.assertj.core.api.Assertions.*;

class GoogleTokenCipherTests {
    @Test
    void deveCifrarComNonceUnicoERejeitarAlteracao() {
        var cipher = new GoogleTokenCipher(Base64.getEncoder().encodeToString(new byte[32]));
        String cifrado = cipher.cifrar("refresh-token");
        assertThat(cipher.decifrar(cifrado)).isEqualTo("refresh-token");
        assertThat(cipher.cifrar("refresh-token")).isNotEqualTo(cifrado);
        byte[] alterado = Base64.getDecoder().decode(cifrado); alterado[15] ^= 1;
        assertThatThrownBy(() -> cipher.decifrar(Base64.getEncoder().encodeToString(alterado))).isInstanceOf(IllegalStateException.class);
        assertThat(new GoogleTokenCipher("").configurado()).isFalse();
    }
}
