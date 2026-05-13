package com.dilanmotos.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PqrsJpaRepository extends JpaRepository<PqrsEntity, Integer> {
}