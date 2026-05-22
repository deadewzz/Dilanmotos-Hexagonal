package com.dilanmotos.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface CotizacionJpaRepository extends JpaRepository<CotizacionEntity, Integer> {
    List<CotizacionEntity> findByIdUsuario(Integer idUsuario);
}
