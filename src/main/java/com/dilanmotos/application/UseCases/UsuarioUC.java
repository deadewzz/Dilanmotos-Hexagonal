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

    @Override // se añade esto para que el IDE te confirme si el nombre es correcto
    public Usuario buscarPorCorreo(String correo) {
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

    @Override
    public Usuario actualizar(int id, Usuario datosNuevos) {
        // 1. Buscamos el usuario REAL que ya existe en la BD
        Usuario usuarioExistente = usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // 2. IMPORTANTE: Solo modificamos los campos permitidos
        // sobre el objeto que ya tiene el ID correcto de la BD
        usuarioExistente.setNombre(datosNuevos.getNombre());
        usuarioExistente.setCorreo(datosNuevos.getCorreo());

        // 3. Manejo de contraseña
        if (datosNuevos.getContrasena() != null && !datosNuevos.getContrasena().isEmpty()) {
            usuarioExistente.setContrasena(passwordEncoder.encode(datosNuevos.getContrasena()));
        }

        // 4. Al guardar 'usuarioExistente', JPA reconoce que el ID ya existe y hace un
        // UPDATE
        return usuarioRepository.guardar(usuarioExistente);
    }
}