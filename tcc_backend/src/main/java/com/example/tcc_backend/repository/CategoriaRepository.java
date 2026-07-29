package com.example.tcc_backend.repository;

import com.example.tcc_backend.domain.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findAllByUsuarioIdOrderByNomeAsc(Long usuarioId);

    Optional<Categoria> findByIdAndUsuarioId(Long id, Long usuarioId);
}
