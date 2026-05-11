package com.dilanmotos.domain.repository;

import com.dilanmotos.domain.model.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository {
    Producto guardar(Producto producto);

    List<Producto> obtenerTodos();

    Optional<Producto> buscarPorId(Integer id);
}