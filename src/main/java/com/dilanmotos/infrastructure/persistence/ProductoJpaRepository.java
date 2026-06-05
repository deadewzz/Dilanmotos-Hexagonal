package com.dilanmotos.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoJpaRepository extends JpaRepository<ProductoEntity, Integer> {
    List<ProductoEntity> findByIdCategoria(Integer idCategoria);
}