package com.dilanmotos.infrastructure.persistence;

import com.dilanmotos.domain.model.Usuario;
import com.dilanmotos.domain.repository.UsuarioRepository;
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
        UsuarioEntity entity = new UsuarioEntity();
        entity.setNombre(usuario.getNombre());
        entity.setCorreo(usuario.getCorreo());
        entity.setContrasena(usuario.getContrasena());

        UsuarioEntity saved = jpaRepository.save(entity);
        return new Usuario(saved.getIdUsuario(), saved.getNombre(), saved.getCorreo(), saved.getContrasena());
    }

    @Override
    public List<Usuario> obtenerTodos() {
        return jpaRepository.findAll().stream()
                .map(e -> new Usuario(e.getIdUsuario(), e.getNombre(), e.getCorreo(), e.getContrasena()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Usuario> buscarPorId(int id) {
        return jpaRepository.findById(id)
                .map(e -> new Usuario(e.getIdUsuario(), e.getNombre(), e.getCorreo(), e.getContrasena()));
    }

    @Override
    public void eliminarPorId(int id) {
        jpaRepository.deleteById(id);
    }
}