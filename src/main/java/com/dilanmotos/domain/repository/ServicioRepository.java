package com.dilanmotos.domain.repository;

import com.dilanmotos.domain.model.Servicio;
import java.util.List;
import java.util.Optional;

public interface ServicioRepository {

    Servicio guardar(Servicio servicio);

    List<Servicio> obtenerTodas();

    Optional<Servicio> buscarPorId(Integer id);

    Servicio actualizar(Servicio servicio);

    void eliminar(Integer id);
}
