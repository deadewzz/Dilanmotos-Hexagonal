package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.Usuario;
import com.dilanmotos.domain.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioUC implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioUC(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario registrar(Usuario usuario) {
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("USER");
        }
        return usuarioRepository.guardar(usuario);
    }

    @Override
    public Usuario findByCorreo(String correo) { // Nombre corregido según la interfaz
        return usuarioRepository.buscarPorCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Override
    public List<Usuario> listar() { 
        return usuarioRepository.obtenerTodos(); 
    }

    @Override
    public Usuario obtenerPorId(int id) {
        return usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("ID no encontrado"));
    }

    @Override
    public void eliminar(int id) { 
        usuarioRepository.eliminarPorId(id); 
    }
}