package com.dilanmotos.domain.repository;

import com.dilanmotos.domain.model.Marca;
import java.util.List;
import java.util.Optional;

public interface MarcaRepository {
    Marca guardar(Marca marca);

    List<Marca> obtenerTodos();

    Optional<Marca> buscarPorId(Integer id);

    Marca actualizar(Marca marca);

    void eliminar(Integer id);
    
}
