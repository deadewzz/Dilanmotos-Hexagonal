package com.dilanmotos.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CotizacionJpaRepository extends JpaRepository<CotizacionEntity, Integer> {
    List<CotizacionEntity> findByIdUsuario(Integer idUsuario);
    @Query("select c from CotizacionEntity c left join fetch c.usuario")
    List<CotizacionEntity> findAllWithUsuario();
}
    