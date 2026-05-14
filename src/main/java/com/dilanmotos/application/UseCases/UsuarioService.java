package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.Usuario;


import java.util.List;

public interface UsuarioService {
    Usuario registrar(Usuario usuario);
    Usuario obtenerPorCorreo(String correo); // Añadir esta línea
    List<Usuario> listar();
    Usuario obtenerPorId(int id);
    void eliminar(int id);
}