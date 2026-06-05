package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.Usuario;

import java.util.List;

public interface UsuarioService {
    Usuario registrar(Usuario usuario);

    Usuario buscarPorCorreo(String correo);

    List<Usuario> listar();

    Usuario obtenerPorId(int id);

    void eliminar(int id);

    Usuario actualizar(int id, Usuario usuario);

    void cambiarContrasena(Integer idUsuario, String contrasenaActual, String contrasenaNueva);

    void solicitarRecuperacion(String correo);
    
    void resetearContrasena(String token, String nuevaContrasena);
}