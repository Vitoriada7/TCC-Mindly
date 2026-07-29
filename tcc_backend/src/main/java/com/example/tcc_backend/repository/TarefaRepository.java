package com.example.tcc_backend.repository;

import com.example.tcc_backend.domain.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface TarefaRepository extends JpaRepository<Tarefa, Long>, JpaSpecificationExecutor<Tarefa> {

    boolean existsByCategoriaId(Long categoriaId);

    List<Tarefa> findAllByUsuarioIdOrderByDataCriacaoDesc(Long usuarioId);

    Optional<Tarefa> findByIdAndUsuarioId(Long id, Long usuarioId);
}
