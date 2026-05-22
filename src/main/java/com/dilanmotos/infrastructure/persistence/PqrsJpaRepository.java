package com.dilanmotos.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PqrsJpaRepository extends JpaRepository<PqrsEntity, Integer> {
    
    @Query("SELECT p FROM PqrsEntity p WHERE p.id_usuario = :idUsuario")
    List<PqrsEntity> findByIdUsuario(@Param("idUsuario") Integer idUsuario);
}