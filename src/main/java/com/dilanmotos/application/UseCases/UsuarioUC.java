package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.Moto;
import com.dilanmotos.domain.model.Usuario;
import com.dilanmotos.domain.repository.MotoRepository;
import com.dilanmotos.domain.repository.ReferenciaMotoRepository;
import com.dilanmotos.domain.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioUC implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final MotoRepository motoRepository;
    private final ReferenciaMotoRepository referenciaRepository;

    public UsuarioUC(UsuarioRepository usuarioRepository,
                     PasswordEncoder passwordEncoder,
                     MotoRepository motoRepository,
                     ReferenciaMotoRepository referenciaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.motoRepository = motoRepository;
        this.referenciaRepository = referenciaRepository;
    }

    @Override
    public Usuario registrar(Usuario usuario) {
        // 1. Encriptar contraseña y asignar rol por defecto
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("USER");
        }

        // 2. Guardar usuario y obtener el ID generado por la BD
        Usuario guardado = usuarioRepository.guardar(usuario);

        // 3. Si viene con referencia, crear la moto asociada
        if (usuario.getIdReferencia() != null) {
            referenciaRepository.buscarPorId(usuario.getIdReferencia())
                .ifPresent(ref -> {
                    Moto moto = new Moto();
                    moto.setIdUsuario(guardado.getIdUsuario()); // ✅ ID real de la BD
                    moto.setIdMarca(ref.getIdMarca());
                    moto.setModelo(ref.getNombre());
                    moto.setCilindraje(ref.getCilindraje() != null ? ref.getCilindraje() : 0.0); // ReferenciaMoto no tiene cilindraje, se deja en 0
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
}