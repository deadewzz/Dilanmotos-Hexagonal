package com.dilanmotos.infrastructure.persistence;

import com.dilanmotos.domain.model.Usuario;
import com.dilanmotos.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final JpaUsuarioRepository jpaRepository;

    // El constructor recibe el JPA Repository, que es el que realmente hace las
    // consultas a la BD
    public UsuarioRepositoryAdapter(JpaUsuarioRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioEntity entity = new UsuarioEntity();

        // Si el usuario ya tiene un ID, úsalo. Si no, lo generará la BD
        if (usuario.getIdUsuario() != null) {
            entity.setId_usuario(usuario.getIdUsuario());
        }
        // Mapear los campos del dominio a la entidad
        entity.setNombre(usuario.getNombre());
        entity.setCorreo(usuario.getCorreo());
        entity.setContrasena(usuario.getContrasena());
        entity.setRol(usuario.getRol());

        // Si ya tiene habilitado úsalo, si no, pon 1
        entity.setHabilitado(usuario.getHabilitado() != null ? usuario.getHabilitado() : 1);

        UsuarioEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return jpaRepository.findByCorreo(correo).map(this::toDomain);
    }

    // El método obtenerTodos es el que se encarga de traer todos los usuarios de la
    // BD, convertirlos a dominio y devolverlos como una lista
    @Override
    public List<Usuario> obtenerTodos() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Usuario> buscarPorId(int id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public void eliminarPorId(int id) {
        jpaRepository.deleteById(id);
    }

    private Usuario toDomain(UsuarioEntity entity) {
        if (entity == null)
            return null;
        Usuario user = new Usuario();
        user.setIdUsuario(entity.getId_usuario()); // Mapeo de id_usuario a idUsuario
        user.setNombre(entity.getNombre());
        user.setCorreo(entity.getCorreo());
        user.setContrasena(entity.getContrasena());
        user.setRol(entity.getRol());
        user.setHabilitado(entity.getHabilitado());
        return user;
    }
}