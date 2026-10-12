package com.example.tcc_backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 30)
    private String apelido;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @Column(nullable = false)
    private Boolean ativo;

    @Enumerated(EnumType.STRING)
    private com.example.tcc_backend.domain.enums.Avatar avatar;

    @ElementCollection
    @CollectionTable(name = "usuario_avatar", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "avatar", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private java.util.Set<com.example.tcc_backend.domain.enums.Avatar> avataresLiberados = new java.util.HashSet<>();

    @PrePersist
    public void prePersist() {
        dataCriacao = LocalDateTime.now();
        ativo = true;
    }

}
