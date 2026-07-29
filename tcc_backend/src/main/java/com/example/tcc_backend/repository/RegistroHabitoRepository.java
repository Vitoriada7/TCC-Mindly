package com.example.tcc_backend.repository;

import com.example.tcc_backend.domain.RegistroHabito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RegistroHabitoRepository extends JpaRepository<RegistroHabito, Long> {
    List<RegistroHabito> findAllByHabitoIdInAndDataBetweenAndConcluidoTrue(Collection<Long> habitosIds, LocalDate inicio, LocalDate fim);
    Optional<RegistroHabito> findByHabitoIdAndData(Long habitoId, LocalDate data);
    long deleteAllByHabitoId(Long habitoId);
    long countByHabitoUsuarioIdAndDataAndConcluidoTrue(Long usuarioId, LocalDate data);
    List<RegistroHabito> findAllByHabitoUsuarioIdAndDataLessThanEqualAndConcluidoTrueOrderByDataDesc(Long usuarioId, LocalDate data);
}
