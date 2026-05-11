package com.dilanmotos.domain.repository;

import com.dilanmotos.domain.model.Caracteristica;
import java.util.List;
import java.util.Optional;

public interface CaracteristicaRepository {
    Caracteristica guardar(Caracteristica caracteristica);

    List<Caracteristica> obtenerTodas(); // <--- Para el CRUD completo

    List<Caracteristica> obtenerPorMoto(Integer idMoto);

    Optional<Caracteristica> buscarPorId(Integer id);

    void eliminar(Integer id);
}