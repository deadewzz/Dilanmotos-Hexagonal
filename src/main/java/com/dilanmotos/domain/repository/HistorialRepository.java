package com.dilanmotos.domain.repository;

import com.dilanmotos.domain.model.Historial;
import java.util.List;
import java.util.Optional;

public interface HistorialRepository {

    Historial guardar(Historial historial);

    List<Historial> obtenerTodas();

    Optional<Historial> buscarPorId(Integer id);

    Historial actualizar(Historial historial);

    void eliminar(Integer id);
}
