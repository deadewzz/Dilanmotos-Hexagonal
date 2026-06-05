package com.dilanmotos.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CotizacionJpaRepository extends JpaRepository<CotizacionEntity, Integer> {
    List<CotizacionEntity> findByIdUsuario(Integer idUsuario);
}
