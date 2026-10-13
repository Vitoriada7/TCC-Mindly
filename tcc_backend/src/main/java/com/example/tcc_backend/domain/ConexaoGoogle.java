package com.example.tcc_backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "conexoes_google")
@Getter
@Setter
public class ConexaoGoogle {
    @Id
    private Long usuarioId;
    @Version
    private Long versao;
    @Column(length = 2000)
    private String refreshTokenCifrado;
    private String estadoHash;
    private Instant estadoExpira;
    @Column(length = 500)
    private String listaId;
    private Instant ultimaSincronizacao;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tarefas_google", joinColumns = @JoinColumn(name = "usuario_id"))
    @MapKeyColumn(name = "tarefa_id")
    @Column(name = "google_id", length = 500)
    private Map<Long, String> tarefas = new HashMap<>();
}
