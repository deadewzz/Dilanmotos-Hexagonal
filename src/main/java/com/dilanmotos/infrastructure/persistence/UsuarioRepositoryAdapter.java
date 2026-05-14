package com.dilanmotos.infrastructure.persistence;

import com.dilanmotos.domain.model.Usuario;
import com.dilanmotos.domain.repository.UsuarioRepository;
import com.dilanmotos.infrastructure.persistence.UsuarioEntity; // Asegúrate de que el import sea correcto
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final JpaUsuarioRepository jpaRepository;

    public UsuarioRepositoryAdapter(JpaUsuarioRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        // 1. Buscamos si ya existe por correo para mantener el ID en caso de actualización
        UsuarioEntity entity = jpaRepository.findByCorreo(usuario.getCorreo())
                .orElse(new UsuarioEntity());

        // 2. Mapeamos los datos del dominio a la entidad
        entity.setNombre(usuario.getNombre());
        entity.setCorreo(usuario.getCorreo());
        entity.setContrasena(usuario.getContrasena());
        entity.setRol(usuario.getRol());

        UsuarioEntity saved = jpaRepository.save(entity);
        
        // 3. Devolvemos el modelo de dominio
        return mapToDomain(saved);
    }

    @Override
    public List<Usuario> obtenerTodos() {
        return jpaRepository.findAll().stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Usuario> buscarPorId(int id) {
        return jpaRepository.findById(id)
                .map(this::mapToDomain);
    }

    // ESTE ES EL MÉTODO QUE NECESITA SECURITY
   @Override
public Optional<Usuario> buscarPorCorreo(String correo) {
    // findByCorreo ahora existe en jpaRepository
    return jpaRepository.findByCorreo(correo)
            .map(entity -> {
                Usuario user = new Usuario();
                user.setIdUsuario(entity.getIdUsuario());
                user.setNombre(entity.getNombre());
                user.setCorreo(entity.getCorreo());
                user.setContrasena(entity.getContrasena());
                user.setRol(entity.getRol());
                return user;
            });
}

    @Override
    public void eliminarPorId(int id) {
        jpaRepository.deleteById(id);
    }

    // Método privado para no repetir código de mapeo
    private Usuario mapToDomain(UsuarioEntity entity) {
        return new Usuario(
            entity.getIdUsuario(), 
            entity.getNombre(), 
            entity.getCorreo(), 
            entity.getContrasena(), 
            entity.getRol()
        );
    }
}