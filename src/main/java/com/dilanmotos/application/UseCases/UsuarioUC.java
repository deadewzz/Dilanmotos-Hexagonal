package com.dilanmotos.application.UseCases;

//Importaciones
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

//Use case de usuario, implementa la interfaz UsuarioService
@Service
public class UsuarioUC implements UsuarioService {

    // Inyección de dependencias
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final MotoRepository motoRepository;
    private final ReferenciaMotoRepository referenciaRepository;
    private final EmailService emailService;

    // Constructor para inyectar las dependencias
    public UsuarioUC(UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            MotoRepository motoRepository,
            ReferenciaMotoRepository referenciaRepository,
            EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.motoRepository = motoRepository;
        this.referenciaRepository = referenciaRepository;
        this.emailService = emailService;
    }

    // Implementación de los métodos de la interfaz UsuarioService
    @Override
    // Registrar un nuevo usuario
    public Usuario registrar(Usuario usuario) {
        if (usuarioRepository.buscarPorCorreo(usuario.getCorreo()).isPresent()) {
            throw new IllegalStateException("El correo ya está registrado");
        }

        // Validación de la contraseña
        if (usuario.getContrasena() == null || usuario.getContrasena().length() < 6
                || usuario.getContrasena().length() > 20) {
            throw new IllegalArgumentException("La contraseña debe tener entre 6 y 20 caracteres");
        }

        // Validación obligatoria por el NOT NULL de la base de datos
        if (usuario.getIdReferencia() != null && (usuario.getPlaca() == null || usuario.getPlaca().trim().isEmpty())) {
            throw new IllegalArgumentException("La placa de la moto es obligatoria");
        }

        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("USER");
        }

        // Guardar el usuario en la base de datos
        Usuario guardado = usuarioRepository.guardar(usuario);

        // Si el usuario tiene una referencia de moto, crear la moto asociada
        if (usuario.getIdReferencia() != null) {
            referenciaRepository.buscarPorId(usuario.getIdReferencia())
                    .ifPresent(ref -> {
                        // Crear una nueva moto asociada al usuario
                        Moto moto = new Moto();
                        moto.setIdUsuario(guardado.getIdUsuario());
                        moto.setIdMarca(ref.getIdMarca());
                        moto.setModelo(ref.getNombre());
                        moto.setCilindraje(ref.getCilindraje() != null ? ref.getCilindraje() : 0.0);
                        moto.setPlaca(usuario.getPlaca().trim().toUpperCase()); // Asignación limpia
                        motoRepository.guardar(moto);
                    });
        }
        // Retornar el usuario guardado
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

        // Actualizar los campos del usuario existente con los datos nuevos
        usuarioExistente.setNombre(datosNuevos.getNombre());
        usuarioExistente.setCorreo(datosNuevos.getCorreo());

        // Actualizar la contraseña solo si se proporciona una nueva
        if (datosNuevos.getContrasena() != null && !datosNuevos.getContrasena().isEmpty()) {
            // Validación de la nueva contraseña
            usuarioExistente.setContrasena(passwordEncoder.encode(datosNuevos.getContrasena()));
        }

        // Actualizar la placa de la moto si se envía en la petición
        if (datosNuevos.getPlaca() != null && !datosNuevos.getPlaca().isEmpty()) {
            List<Moto> motosDelUsuario = motoRepository.obtenerPorUsuario(id);
            if (!motosDelUsuario.isEmpty()) {
                Moto moto = motosDelUsuario.get(0);
                moto.setPlaca(datosNuevos.getPlaca().trim().toUpperCase());
                motoRepository.actualizar(moto);
            }
        }

        return usuarioRepository.guardar(usuarioExistente);
    }

    @Override
    public void cambiarContrasena(Integer idUsuario, String contrasenaActual, String contrasenaNueva) {
        Usuario usuario = obtenerPorId(idUsuario);

        // Validar la contraseña actual y la nueva
        if (!passwordEncoder.matches(contrasenaActual, usuario.getContrasena())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }

        // Validar la nueva contraseña
        if (contrasenaNueva == null || contrasenaNueva.length() < 6) {
            throw new RuntimeException("La nueva contraseña debe tener al menos 6 caracteres");
        }

        // Actualizar la contraseña en la base de datos
        usuarioRepository.actualizarContrasena(idUsuario, passwordEncoder.encode(contrasenaNueva));
    }

    @Override
    public void solicitarRecuperacion(String correo) {
        usuarioRepository.buscarPorCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Correo no registrado"));

        // Generar un token de recuperación único
        String token = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();

        // Guardar el token en la base de datos con una fecha de expiración
        usuarioRepository.guardarToken(correo, token, LocalDateTime.now().plusHours(24));
        emailService.enviarCorreoRecuperacion(correo, token);
    }

    @Override
    public void resetearContrasena(String token, String nuevaContrasena) {
        Usuario usuario = usuarioRepository.buscarPorToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (nuevaContrasena == null || nuevaContrasena.length() < 6) {
            throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
        }

        // Actualizar la contraseña del usuario y limpiar el token
        usuarioRepository.actualizarContrasena(
                usuario.getIdUsuario(),
                passwordEncoder.encode(nuevaContrasena));
        usuarioRepository.limpiarToken(usuario.getIdUsuario());
    }
}