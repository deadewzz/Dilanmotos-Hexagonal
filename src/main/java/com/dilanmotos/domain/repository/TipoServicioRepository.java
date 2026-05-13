package com.dilanmotos.domain.repository;

import com.dilanmotos.domain.model.TipoServicio;
import java.util.List;
import java.util.Optional;

public interface TipoServicioRepository {

    TipoServicio guardar(TipoServicio tipoServicio);

    List<TipoServicio> obtenerTodas();

    Optional<TipoServicio> buscarPorId(Integer id);

    TipoServicio actualizar(TipoServicio tipoServicio);

    void eliminar(Integer id);
}