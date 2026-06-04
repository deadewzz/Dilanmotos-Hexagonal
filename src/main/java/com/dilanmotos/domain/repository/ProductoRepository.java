package com.dilanmotos.domain.repository;

import com.dilanmotos.domain.model.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository {
    Producto guardar(Producto producto);

    List<Producto> obtenerTodos();
    List<Producto> obtenerPorCategoria(Integer idCategoria);

    Optional<Producto> buscarPorId(Integer id);

    Producto actualizar(Integer id, Producto producto);

    void eliminar(Integer id);
}