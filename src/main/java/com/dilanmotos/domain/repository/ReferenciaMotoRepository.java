package com.dilanmotos.domain.repository;

import com.dilanmotos.domain.model.ReferenciaMoto;
import java.util.*;

public interface ReferenciaMotoRepository {
    
    ReferenciaMoto guardar(ReferenciaMoto ReferenciaMoto);

    List<ReferenciaMoto> obtenerTodos();

    Optional<ReferenciaMoto> buscarPorId(int id);

    void eliminarPorId(int id);
    
}
