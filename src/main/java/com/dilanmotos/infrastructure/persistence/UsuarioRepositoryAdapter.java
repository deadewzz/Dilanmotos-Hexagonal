package com.dilanmotos.infrastructure.persistence;

import com.dilanmotos.domain.model.MotoResumen;
import com.dilanmotos.domain.model.Usuario;
import com.dilanmotos.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final JpaUsuarioRepository jpaRepository;
    private final MarcaJpaRepository marcaJpaRepository;

    public UsuarioRepositoryAdapter(JpaUsuarioRepository jpaRepository, MarcaJpaRepository marcaJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.marcaJpaRepository = marcaJpaRepository;
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioEntity entity = new UsuarioEntity();
        if (usuario.getIdUsuario() != null) {
            entity.setId_usuario(usuario.getIdUsuario());
        }
        entity.setNombre(usuario.getNombre());
        entity.setCorreo(usuario.getCorreo());
        entity.setContrasena(usuario.getContrasena());
        entity.setRol(usuario.getRol());
        entity.setHabilitado(usuario.getHabilitado() != null ? usuario.getHabilitado() : 1);

        UsuarioEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void actualizarContrasena(Integer idUsuario, String contrasenaNueva) {
    jpaRepository.findById(idUsuario).ifPresent(entity -> {
        entity.setContrasena(contrasenaNueva);
        jpaRepository.save(entity);
    });
}

    @Override
    public void guardarToken(String correo, String token, LocalDateTime expiracion) {
        jpaRepository.findByCorreo(correo).ifPresent(entity -> {
            entity.setResetToken(token);
            entity.setTokenExpiracion(expiracion);
            jpaRepository.save(entity);
        });
    }

    @Override
    public Optional<Usuario> buscarPorToken(String token) {
        return jpaRepository.findByResetToken(token).map(this::toDomain);
    }

    @Override
    public void limpiarToken(Integer idUsuario) {
        jpaRepository.findById(idUsuario).ifPresent(entity -> {
            entity.setResetToken(null);
            entity.setTokenExpiracion(null);
            jpaRepository.save(entity);
        });
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return jpaRepository.findByCorreo(correo).map(this::toDomain);
    }

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
        if (entity == null) return null;

        Usuario user = new Usuario();
        user.setIdUsuario(entity.getId_usuario());
        user.setNombre(entity.getNombre());
        user.setCorreo(entity.getCorreo());
        user.setContrasena(entity.getContrasena());
        user.setRol(entity.getRol());
        user.setHabilitado(entity.getHabilitado());

        if (entity.getMotos() != null) {
            List<MotoResumen> motos = entity.getMotos().stream().map(m -> {
                MotoResumen mr = new MotoResumen();
                mr.setIdMoto(m.getIdMoto());
                mr.setModelo(m.getModelo());
                mr.setCilindraje(m.getCilindraje());
                mr.setIdMarca(m.getIdMarca());

                if (m.getIdMarca() != null) {
                    marcaJpaRepository.findById(m.getIdMarca())
                    .ifPresent(marca -> mr.setNombreMarca(marca.getNombre()));
                }

                return mr;
            }).collect(Collectors.toList());
            user.setMotos(motos);
        }

        return user;
    }
}