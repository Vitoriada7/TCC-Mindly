package com.example.tcc_backend.domain.enums;

import lombok.Getter;

@Getter
public enum Conquista {

    PRIMEIRO_HABITO("Primeiro passo", "Conclua seu primeiro hábito.", "HABITOS", 1),
    ROTINA_SUAVE("Rotina suave", "Mantenha 7 dias de hábitos em sequência.", "HABITOS", 7),
    PRIMEIRA_TAREFA("Tirando do papel", "Conclua sua primeira tarefa.", "TAREFAS", 1),
    SEMANA_ORGANIZADA("Semana organizada", "Conclua 5 tarefas na mesma semana.", "TAREFAS", 5),
    NO_TEMPO_CERTO("No tempo certo", "Conclua 10 tarefas dentro do prazo.", "TAREFAS", 10),
    PRIMEIRO_REGISTRO("Primeiro olhar", "Faça seu primeiro registro emocional.", "EMOCIONAL", 1),
    SEMANA_CONSCIENTE("Semana consciente", "Registre suas emoções por 7 dias seguidos.", "EMOCIONAL", 7),
    HABITOS_30_DIAS("Cultivando sua rotina", "Conclua hábitos em 30 dias diferentes.", "HABITOS", 30),
    HABITOS_30_SEGUIDOS("Uma nova fase", "Mantenha 30 dias de hábitos em sequência.", "HABITOS", 30),
    TAREFAS_25("Colecionando avanços", "Conclua 25 tarefas.", "TAREFAS", 25),
    TAREFAS_50("No seu ritmo", "Conclua 50 tarefas.", "TAREFAS", 50),
    REGISTROS_30("Páginas de você", "Faça 30 registros emocionais.", "EMOCIONAL", 30),
    EMOCOES_30_SEGUIDOS("Tempo para si", "Registre suas emoções por 30 dias seguidos.", "EMOCIONAL", 30);

    private final String titulo;

    private final String descricao;

    private final String trilha;

    private final int meta;

    Conquista(String titulo, String descricao, String trilha, int meta) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.trilha = trilha;
        this.meta = meta;
    }

}
