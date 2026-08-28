package com.example.tcc_backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "registros_emocionais",
        uniqueConstraints = @UniqueConstraint(name = "uk_registro_emocional_usuario_data", columnNames = {"usuario_id", "data"}),
        indexes = @Index(name = "idx_registro_emocional_usuario_data", columnList = "usuario_id,data"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroEmocional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String sentimento;

    @Column(length = 60)
    private String sentimentoDetalhado;

    @Column(columnDefinition = "TEXT")
    private String pensamento;

    @Column(columnDefinition = "TEXT")
    private String exploracoes;

    @Column(columnDefinition = "TEXT")
    private String reflexao;

    @Column(name = "data_registro", nullable = false)
    private LocalDateTime dataRegistro;

    @Column(nullable = false, updatable = false)
    private LocalDate data;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Usuario usuario;
}
