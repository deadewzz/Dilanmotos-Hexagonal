package com.dilanmotos.domain.repository;

import com.dilanmotos.domain.model.Servicio;
import com.dilanmotos.infrastructure.persistence.ServicioEntity;

import java.util.List;
import java.util.Optional;

public interface ServicioRepository {

     List<ServicioEntity> findByIdUsuario(Integer idUsuario);

    Servicio guardar(Servicio servicio);

    List<Servicio> obtenerTodas();

    Optional<Servicio> buscarPorId(Integer id);

    Servicio actualizar(Servicio servicio);

    void eliminar(Integer id);
}
