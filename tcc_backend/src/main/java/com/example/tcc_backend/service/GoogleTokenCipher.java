package com.example.tcc_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class GoogleTokenCipher {
    private final String chave;
    public GoogleTokenCipher(@Value("${google.tasks.encryption-key:}") String chave) { this.chave = chave; }
    public boolean configurado() {
        try { return Base64.getDecoder().decode(chave).length == 32; }
        catch (IllegalArgumentException e) { return false; }
    }
    public String cifrar(String texto) {
        try {
            byte[] iv = new byte[12];
            new SecureRandom().nextBytes(iv);
            byte[] resultado = cipher(Cipher.ENCRYPT_MODE, iv).doFinal(texto.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(ByteBuffer.allocate(iv.length + resultado.length).put(iv).put(resultado).array());
        } catch (Exception e) { throw new IllegalStateException("Não foi possível proteger a conexão Google"); }
    }
    public String decifrar(String texto) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(Base64.getDecoder().decode(texto));
            byte[] iv = new byte[12]; buffer.get(iv);
            byte[] dados = new byte[buffer.remaining()]; buffer.get(dados);
            return new String(cipher(Cipher.DECRYPT_MODE, iv).doFinal(dados), StandardCharsets.UTF_8);
        } catch (Exception e) { throw new IllegalStateException("Não foi possível recuperar a conexão Google"); }
    }
    private Cipher cipher(int modo, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(modo, new SecretKeySpec(Base64.getDecoder().decode(chave), "AES"), new GCMParameterSpec(128, iv));
        return cipher;
    }
}
