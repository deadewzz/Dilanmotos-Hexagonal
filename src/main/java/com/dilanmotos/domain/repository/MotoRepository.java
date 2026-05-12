package com.dilanmotos.domain.repository;

import com.dilanmotos.domain.model.Moto;
import java.util.List;
import java.util.Optional;

public interface MotoRepository {

    Moto guardar(Moto moto);

    List<Moto> obtenerTodas();

    Optional<Moto> buscarPorId(Integer id);

    Moto actualizar(Moto moto);

    void eliminar(Integer id);
}
