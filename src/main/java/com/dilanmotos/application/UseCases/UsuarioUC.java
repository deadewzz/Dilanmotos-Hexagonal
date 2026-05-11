package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.Usuario;
import com.dilanmotos.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioUC implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // Constructor limpio sin PasswordEncoder
    public UsuarioUC(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario registrar(Usuario usuario) {
        // Guardamos directamente sin encriptar por ahora
        return usuarioRepository.guardar(usuario);
    }

    @Override
    public List<Usuario> listar() {
        return usuarioRepository.obtenerTodos();
    }

    @Override
    public Usuario obtenerPorId(int id) {
        return usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    @Override
    public void eliminar(int id) {
        usuarioRepository.eliminarPorId(id);
    }
}