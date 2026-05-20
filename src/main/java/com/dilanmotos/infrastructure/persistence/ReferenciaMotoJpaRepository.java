package com.dilanmotos.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// Debe extender de ReferenciaEntity porque así se llama tu clase anotada con @Entity
public interface ReferenciaMotoJpaRepository extends JpaRepository<ReferenciaEntity, Integer> {
List<ReferenciaEntity> findByMarca_IdMarca(Integer idMarca);
}