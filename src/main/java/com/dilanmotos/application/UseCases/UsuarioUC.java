package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.Moto;
import com.dilanmotos.domain.model.Usuario;
import com.dilanmotos.domain.repository.MotoRepository;
import com.dilanmotos.domain.repository.ReferenciaMotoRepository;
import com.dilanmotos.domain.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UsuarioUC implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final MotoRepository motoRepository;
    private final ReferenciaMotoRepository referenciaRepository;
    private final EmailService emailService; // ← nuevo

    public UsuarioUC(UsuarioRepository usuarioRepository,
                     PasswordEncoder passwordEncoder,
                     MotoRepository motoRepository,
                     ReferenciaMotoRepository referenciaRepository,
                     EmailService emailService) { // ← nuevo
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.motoRepository = motoRepository;
        this.referenciaRepository = referenciaRepository;
        this.emailService = emailService; // ← nuevo
    }

    // ... todos tus métodos existentes sin cambios ...

    @Override
    public Usuario registrar(Usuario usuario) {
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("USER");
        }
        Usuario guardado = usuarioRepository.guardar(usuario);
        if (usuario.getIdReferencia() != null) {
            referenciaRepository.buscarPorId(usuario.getIdReferencia())
                .ifPresent(ref -> {
                    Moto moto = new Moto();
                    moto.setIdUsuario(guardado.getIdUsuario());
                    moto.setIdMarca(ref.getIdMarca());
                    moto.setModelo(ref.getNombre());
                    moto.setCilindraje(ref.getCilindraje() != null ? ref.getCilindraje() : 0.0);
                    motoRepository.guardar(moto);
                });
        }
        return guardado;
    }

    @Override
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
        Usuario usuarioExistente = usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        usuarioExistente.setNombre(datosNuevos.getNombre());
        usuarioExistente.setCorreo(datosNuevos.getCorreo());
        if (datosNuevos.getContrasena() != null && !datosNuevos.getContrasena().isEmpty()) {
            usuarioExistente.setContrasena(passwordEncoder.encode(datosNuevos.getContrasena()));
        }
        return usuarioRepository.guardar(usuarioExistente);
    }

    // ── Métodos nuevos ─────────────────────────────────────────

    @Override
    public void cambiarContrasena(Integer idUsuario, String contrasenaActual, String contrasenaNueva) {
        Usuario usuario = obtenerPorId(idUsuario);

        if (!passwordEncoder.matches(contrasenaActual, usuario.getContrasena())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }
        if (contrasenaNueva == null || contrasenaNueva.length() < 6) {
            throw new RuntimeException("La nueva contraseña debe tener al menos 6 caracteres");
        }

        usuarioRepository.actualizarContrasena(idUsuario, passwordEncoder.encode(contrasenaNueva));
    }

    @Override
    public void solicitarRecuperacion(String correo) {
        usuarioRepository.buscarPorCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Correo no registrado"));

        String token = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();

        // Cambia .plusMinutes(15) por .plusHours(24)
        usuarioRepository.guardarToken(correo, token, LocalDateTime.now().plusHours(24));
        emailService.enviarCorreoRecuperacion(correo, token);
    }

    @Override
    public void resetearContrasena(String token, String nuevaContrasena) {
        Usuario usuario = usuarioRepository.buscarPorToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        /* Verifica si el token ha expirado
        if (usuario.getTokenExpiracion() == null ||
            LocalDateTime.now().isAfter(usuario.getTokenExpiracion())) {
            throw new RuntimeException("El token ha expirado");
        }
        */

        if (nuevaContrasena == null || nuevaContrasena.length() < 6) {
            throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
        }

        usuarioRepository.actualizarContrasena(
            usuario.getIdUsuario(),
            passwordEncoder.encode(nuevaContrasena)
        );
        usuarioRepository.limpiarToken(usuario.getIdUsuario());
    }
}