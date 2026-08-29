package com.example.tcc_backend.repository;

import com.example.tcc_backend.domain.RegistroEmocional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RegistroEmocionalRepository extends JpaRepository<RegistroEmocional, Long> {
    Optional<RegistroEmocional> findByUsuarioIdAndData(Long usuarioId, LocalDate data);
    List<RegistroEmocional> findAllByUsuarioIdOrderByDataRegistroDesc(Long usuarioId);
}
