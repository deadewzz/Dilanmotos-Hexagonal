package com.dilanmotos.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JpaUsuarioRepository extends JpaRepository<UsuarioEntity, Integer> {
  
    Optional<UsuarioEntity> findByCorreo(String correo);

    // ← nuevo
    Optional<UsuarioEntity> findByResetToken(String resetToken);
}