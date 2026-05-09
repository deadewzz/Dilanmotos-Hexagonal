package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.Usuario;
import java.util.List;

public interface UsuarioService {
    Usuario registrar(Usuario usuario);

    List<Usuario> listar();

    Usuario obtenerPorId(int id);

    void eliminar(int id);
}