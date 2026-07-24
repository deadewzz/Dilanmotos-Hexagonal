package com.dilanmotos.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoJpaRepository extends JpaRepository<ProductoEntity, Integer> {

    @Query("SELECT p FROM ProductoEntity p LEFT JOIN FETCH p.marca LEFT JOIN FETCH p.categoria")
    List<ProductoEntity> findAllConRelaciones();

    List<ProductoEntity> findByIdCategoria(Integer idCategoria);
}