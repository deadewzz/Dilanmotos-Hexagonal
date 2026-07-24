package com.dilanmotos.domain.repository;

import com.dilanmotos.domain.model.MarcaProducto;
import java.util.List;
import java.util.Optional;

public interface MarcaProductoRepository {
    MarcaProducto guardar(MarcaProducto marcaProducto);

    List<MarcaProducto> obtenerTodos();

    List<MarcaProducto> obtenerPorCategoria(Integer idCategoria);

    Optional<MarcaProducto> buscarPorId(Integer id);

    MarcaProducto actualizar(MarcaProducto marcaProducto);

    void eliminar(Integer id);

}