package com.dilanmotos.infrastructure.persistence;

import com.dilanmotos.domain.model.Historial;
import com.dilanmotos.domain.repository.HistorialRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class HistorialRepositoryImpl implements HistorialRepository {

    private final HistorialJpaRepository jpa;

    public HistorialRepositoryImpl(HistorialJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Historial guardar(Historial historial) {
        HistorialEntity entity = toEntity(historial);
        return toModel(jpa.save(entity));
    }

    @Override
    public List<Historial> obtenerTodas() {
        return jpa.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Historial> buscarPorId(Integer id) {
        return jpa.findById(id).map(this::toModel);
    }

    @Override
    public Historial actualizar(Historial historial) {
        Integer id = historial.getIdHistorial();
        return jpa.findById(id).map(entity -> {
            entity.setIdUsuario(historial.getIdUsuario());
            entity.setIdServicio(historial.getIdServicio());
            entity.setAccion(historial.getAccion());
            entity.setFecha(historial.getFecha());
            entity.setDetalle(historial.getDetalle());
            return toModel(jpa.save(entity));
        }).orElseThrow(() -> new RuntimeException("Error al actualizar: Historial no encontrado"));
    }

    @Override
    public void eliminar(Integer id) {
        jpa.deleteById(id);
    }

    // MAPPERS INTERNOS
    private Historial toModel(HistorialEntity e) {
        Historial h = new Historial();
        h.setIdHistorial(e.getIdHistorial());
        h.setIdUsuario(e.getIdUsuario());
        h.setIdServicio(e.getIdServicio());
        h.setAccion(e.getAccion());
        h.setFecha(e.getFecha());
        h.setDetalle(e.getDetalle());
        return h;
    }

    private HistorialEntity toEntity(Historial h) {
        HistorialEntity e = new HistorialEntity();
        if (h.getIdHistorial() != null)
            e.setIdHistorial(h.getIdHistorial());
        e.setIdUsuario(h.getIdUsuario());
        e.setIdServicio(h.getIdServicio());
        e.setAccion(h.getAccion());
        e.setFecha(h.getFecha());
        e.setDetalle(h.getDetalle());
        return e;
    }
}