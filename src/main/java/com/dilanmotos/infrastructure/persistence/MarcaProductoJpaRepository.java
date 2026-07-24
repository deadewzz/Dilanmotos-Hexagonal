package com.dilanmotos.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarcaProductoJpaRepository extends JpaRepository<MarcaProductoEntity, Integer> {

    // JOIN FETCH para traer la categoría en la misma consulta y evitar "Sin Categoría"
    @Query("SELECT m FROM MarcaProductoEntity m JOIN FETCH m.categoria")
    List<MarcaProductoEntity> findAllConCategoria();

    // Método para buscar por el ID de la categoría (navega a m.categoria.idCategoria)
    List<MarcaProductoEntity> findByCategoriaIdCategoria(Integer idCategoria);
}