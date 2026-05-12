package com.dilanmotos.infrastructure.persistence;

import com.dilanmotos.domain.model.Servicio;
import com.dilanmotos.domain.repository.ServicioRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ServicioRepositoryImpl implements ServicioRepository {

    private final ServicioJpaRepository jpa;

    public ServicioRepositoryImpl(ServicioJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Servicio guardar(Servicio servicio) {
        ServicioEntity entity = toEntity(servicio);
        return toModel(jpa.save(entity));
    }

    @Override
    public List<Servicio> obtenerTodas() {
        return jpa.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Servicio> buscarPorId(Integer id) {
        return jpa.findById(id).map(this::toModel);
    }

    @Override
    public Servicio actualizar(Servicio servicio) {
        Integer id = servicio.getIdServicio();
        return jpa.findById(id).map(entity -> {
            entity.setIdUsuario(servicio.getIdUsuario());
            entity.setIdMecanico(servicio.getIdMecanico());
            entity.setIdTipoDeServicio(servicio.getIdTipoDeServicio());
            entity.setFechaServicio(servicio.getFechaServicio());
            entity.setEstadoServicio(servicio.getEstadoServicio());
            entity.setComentario(servicio.getComentario());
            entity.setPuntuacion(servicio.getPuntuacion());
            entity.setVisibleEnHistorial(servicio.getVisibleEnHistorial());
            return toModel(jpa.save(entity));
        }).orElseThrow(() -> new RuntimeException("Error al actualizar: Servicio no encontrado"));
    }

    @Override
    public void eliminar(Integer id) {
        jpa.deleteById(id);
    }

    // MAPPERS INTERNOS
    private Servicio toModel(ServicioEntity e) {
        Servicio s = new Servicio();
        s.setIdUsuario(e.getIdUsuario());
        s.setIdMecanico(e.getIdMecanico());
        s.setIdTipoDeServicio(e.getIdTipoDeServicio());
        s.setFechaServicio(e.getFechaServicio());
        s.setEstadoServicio(e.getEstadoServicio());
        s.setComentario(e.getComentario());
        s.setPuntuacion(e.getPuntuacion());
        s.setVisibleEnHistorial(e.getVisibleEnHistorial());
        return s;
    }

    private ServicioEntity toEntity(Servicio s) {
        ServicioEntity e = new ServicioEntity();
        if (s.getIdServicio() != null)
            e.setIdServicio(s.getIdServicio());
        e.setIdUsuario(s.getIdUsuario());
        e.setIdMecanico(s.getIdMecanico());
        e.setIdTipoDeServicio(s.getIdTipoDeServicio());
        e.setFechaServicio(s.getFechaServicio());
        e.setEstadoServicio(s.getEstadoServicio());
        e.setComentario(s.getComentario());
        e.setPuntuacion(s.getPuntuacion());
        e.setVisibleEnHistorial(s.getVisibleEnHistorial());
        return e;
    }
}