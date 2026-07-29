package com.example.tcc_backend.repository;

import com.example.tcc_backend.domain.Habito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HabitoRepository extends JpaRepository<Habito, Long> {
    List<Habito> findAllByUsuarioIdAndAtivoTrueOrderByDataCriacaoDesc(Long usuarioId);
    Optional<Habito> findByIdAndUsuarioIdAndAtivoTrue(Long id, Long usuarioId);
    long countByUsuarioIdAndAtivoTrue(Long usuarioId);
}
