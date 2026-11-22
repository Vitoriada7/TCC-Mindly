package com.example.tcc_backend.repository.specification;

import com.example.tcc_backend.domain.Tarefa;
import com.example.tcc_backend.domain.enums.Prioridade;
import com.example.tcc_backend.domain.enums.StatusTarefa;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class TarefaSpecifications {

    private TarefaSpecifications() {}

    public static Specification<Tarefa> comFiltros(Long usuarioId, StatusTarefa status, Prioridade prioridade,
                                                  Long categoriaId, Boolean vencida) {
        Specification<Tarefa> especificacao = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("usuario").get("id"), usuarioId);

        if (status != null) {
            especificacao = especificacao.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), status));
        }

        if (prioridade != null) {
            especificacao = especificacao.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("prioridade"), prioridade));
        }

        if (categoriaId != null) {
            especificacao = especificacao.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("categoria").get("id"), categoriaId));
        }

        if (Boolean.TRUE.equals(vencida)) {
            LocalDateTime agora = LocalDateTime.now();
            especificacao = especificacao.and((root, query, criteriaBuilder) -> criteriaBuilder.and(
                    criteriaBuilder.notEqual(root.get("status"), StatusTarefa.CONCLUIDA),
                    criteriaBuilder.isNotNull(root.get("dataLimite")),
                    criteriaBuilder.lessThan(root.get("dataLimite"), agora)));
        }

        return especificacao;
    }

}
