package com.dilanmotos.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CaracteristicaJpaRepository extends JpaRepository<CaracteristicaEntity, Integer> {
    List<CaracteristicaEntity> findByIdMoto(Integer idMoto);
}