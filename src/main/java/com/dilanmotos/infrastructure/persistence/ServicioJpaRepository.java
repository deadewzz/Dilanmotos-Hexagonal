package com.dilanmotos.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface ServicioJpaRepository extends JpaRepository<ServicioEntity, Integer> {
    List<ServicioEntity> findByIdUsuario(Integer idUsuario);
}