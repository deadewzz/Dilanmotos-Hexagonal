package com.dilanmotos.domain.repository;

import com.dilanmotos.domain.model.Categoria;
import java.util.List;
import java.util.Optional;

public interface CategoriaRepository {
    List<Categoria> obtenerTodas();
    Optional<Categoria> buscarPorId(Integer id);
    Categoria guardar(Categoria categoria);
    Categoria actualizar(Categoria categoria);
    void eliminar(Integer id);
}
