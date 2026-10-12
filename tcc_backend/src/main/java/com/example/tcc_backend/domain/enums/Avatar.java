package com.example.tcc_backend.domain.enums;

import java.util.Arrays;

public enum Avatar {
    ARVORE("PRIMEIRO_HABITO"),
    FLOR("ROTINA_SUAVE"),
    ESTRELA("PRIMEIRA_TAREFA"),
    CORUJA("SEMANA_ORGANIZADA"),
    GATO("NO_TEMPO_CERTO"),
    CORACAO("PRIMEIRO_REGISTRO"),
    CEREBRO("SEMANA_CONSCIENTE"),
    SOL("HABITOS_30_DIAS"),
    BORBOLETA("HABITOS_30_SEGUIDOS"),
    COGUMELO("TAREFAS_25"),
    NUVEM("TAREFAS_50"),
    LUA("REGISTROS_30"),
    XICARA("EMOCOES_30_SEGUIDOS");
    private final String conquista;
    Avatar(String conquista) { this.conquista = conquista; }
    public String getConquista() { return conquista; }
    public static Avatar daConquista(String codigo) {
        return Arrays.stream(values()).filter(avatar -> avatar.conquista.equals(codigo)).findFirst().orElseThrow();
    }
}
