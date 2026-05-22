package com.dilanmotos.domain.repository;

import com.dilanmotos.domain.model.*;
import com.dilanmotos.infrastructure.persistence.PqrsEntity;

import java.util.*;

public interface PqrsRepository {

    List<PqrsEntity> findByIdUsuario(Integer idUsuario);

    PQRS guardar(PQRS Pqrs);

    List<PQRS> obtenerTodos();

    Optional<PQRS> buscarPorId(int id);

    void eliminarPorId(int id);

}