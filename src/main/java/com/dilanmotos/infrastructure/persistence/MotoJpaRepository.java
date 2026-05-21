package com.dilanmotos.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MotoJpaRepository extends JpaRepository<MotoEntity, Integer> {
 List<MotoEntity> findByIdUsuario(Integer idUsuario);
}