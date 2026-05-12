package com.dilanmotos.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dilanmotos.domain.model.ReferenciaMoto;

import java.util.*;

// Debe extender de ReferenciaEntity porque así se llama tu clase anotada con @Entity
public interface ReferenciaMotoJpaRepository extends JpaRepository<ReferenciaEntity, Integer> {
    
    ReferenciaMoto guardar(ReferenciaMoto ReferenciaMoto);

    List<ReferenciaMoto> obtenerTodos();

    Optional<ReferenciaMoto> buscarPorId(int id);

    void eliminarPorId(int id);
    
}