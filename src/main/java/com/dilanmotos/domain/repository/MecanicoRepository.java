package com.dilanmotos.domain.repository;

import com.dilanmotos.domain.model.Mecanico;
import java.util.List;
import java.util.Optional;

public interface MecanicoRepository {
    Mecanico guardar(Mecanico mecanico);

    List<Mecanico> obtenerTodos(); // <--- Para el CRUD completo

    List<Mecanico> obtenerPorNombre(String nombre);

    Mecanico actualizar(Mecanico mecanico);

    Optional<Mecanico> buscarPorId(Integer id);

    void eliminar(Integer id);
}
