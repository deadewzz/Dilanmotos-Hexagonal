package com.dilanmotos.domain.repository;

import com.dilanmotos.domain.model.*;
import java.util.*;

public interface PqrsRepository {

    PQRS guardar(PQRS Pqrs);

    List<PQRS> obtenerTodos();

    Optional<PQRS> buscarPorId(int id);

    void eliminarPorId(int id);

}