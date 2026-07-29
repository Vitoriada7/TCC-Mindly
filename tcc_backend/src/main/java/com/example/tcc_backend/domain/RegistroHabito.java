package com.example.tcc_backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "registros_habitos", uniqueConstraints = @UniqueConstraint(columnNames = {"habito_id", "data"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroHabito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Habito habito;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private Boolean concluido;
}
