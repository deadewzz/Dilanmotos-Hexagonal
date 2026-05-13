package com.dilanmotos.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;


// Debe extender de ReferenciaEntity porque así se llama tu clase anotada con @Entity
public interface ReferenciaMotoJpaRepository extends JpaRepository<ReferenciaEntity, Integer> {

}