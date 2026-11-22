package com.example.tcc_backend.domain;

import jakarta.persistence.*;
import lombok.*;

import com.example.tcc_backend.domain.enums.Avatar;

import java.util.ArrayList;
import java.util.List;
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

    @Builder.Default
    private Boolean temaEscuro = false;

    @Enumerated(EnumType.STRING)
    private Avatar avatar;

    @ElementCollection
    @CollectionTable(name = "usuario_avatar", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "avatar", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private List<Avatar> avataresLiberados = new ArrayList<>();

    public void liberarAvatar(Avatar avatar) {
        if (!avataresLiberados.contains(avatar)) {
            avataresLiberados.add(avatar);
        }
    }

    @PrePersist
    public void prePersist() {
        dataCriacao = LocalDateTime.now();
        ativo = true;
    }

}
