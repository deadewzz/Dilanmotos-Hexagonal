package com.dilanmotos.infrastructure.persistence;

import com.dilanmotos.domain.model.PQRS;
import com.dilanmotos.domain.repository.PqrsRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository // <--- CRITICO: Sin esto, Spring no la encuentra
public class PqrsJpaAdapter implements PqrsRepository {

    // Aquí inyectarías tu JpaRepository real (el de Spring Data)
    // private final PqrsJpaRepository jpaRepository;

    @Override
    public PQRS guardar(PQRS pqrs) {
        // lógica para guardar
        return null; 
    }

    @Override
    public List<PQRS> obtenerTodos() {
        return List.of();
    }

    @Override
    public Optional<PQRS> buscarPorId(int id) {
        return Optional.empty();
    }

    @Override
    public void eliminarPorId(int id) {
        // lógica para eliminar
    }
}