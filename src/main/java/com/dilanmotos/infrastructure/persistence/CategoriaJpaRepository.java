package com.dilanmotos.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaJpaRepository extends JpaRepository<CategoriaEntity, Integer> {

    
}
