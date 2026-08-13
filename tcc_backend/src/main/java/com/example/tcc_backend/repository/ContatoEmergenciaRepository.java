package com.example.tcc_backend.repository;

import com.example.tcc_backend.domain.ContatoEmergencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContatoEmergenciaRepository extends JpaRepository<ContatoEmergencia, Long> {
    List<ContatoEmergencia> findAllByUsuarioIdOrderByPrincipalDescNomeAsc(Long usuarioId);
    Optional<ContatoEmergencia> findByIdAndUsuarioId(Long id, Long usuarioId);
    long countByUsuarioId(Long usuarioId);
    List<ContatoEmergencia> findAllByUsuarioIdAndPrincipalTrue(Long usuarioId);
}
