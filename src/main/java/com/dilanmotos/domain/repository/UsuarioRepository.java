package com.dilanmotos.domain.repository;

import com.dilanmotos.domain.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {
    Usuario guardar(Usuario usuario);

    List<Usuario> obtenerTodos();

    Optional<Usuario> buscarPorId(int id);

    void eliminarPorId(int id);

}
