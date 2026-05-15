package com.dilanmotos.domain.repository;

import com.dilanmotos.domain.model.Usuario;
import java.util.Optional;
import java.util.List;

public interface UsuarioRepository {
    Usuario guardar(Usuario usuario);
    Optional<Usuario> buscarPorCorreo(String correo);
    List<Usuario> obtenerTodos(); // Asegúrate que estos nombres coincidan en el Adapter
    Optional<Usuario> buscarPorId(int id);
    void eliminarPorId(int id);
}