package com.dilanmotos.domain.repository;

import com.dilanmotos.domain.model.Usuario;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

public interface UsuarioRepository {

    Usuario guardar(Usuario usuario);
    Optional<Usuario> buscarPorCorreo(String correo);
    List<Usuario> obtenerTodos(); // Asegúrate que estos nombres coincidan en el Adapter
    Optional<Usuario> buscarPorId(int id);
    void eliminarPorId(int id);
    void actualizarContrasena(Integer idUsuario, String contrasenaNueva);
    void guardarToken(String correo, String token, LocalDateTime expiracion);
    Optional<Usuario> buscarPorToken(String token);
    void limpiarToken(Integer idUsuario);
}