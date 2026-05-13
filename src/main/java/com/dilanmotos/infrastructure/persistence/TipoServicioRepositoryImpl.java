package com.dilanmotos.infrastructure.persistence;

import com.dilanmotos.domain.model.TipoServicio;
import com.dilanmotos.domain.repository.TipoServicioRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TipoServicioRepositoryImpl implements TipoServicioRepository {

    private final TipoServicioJpaRepository jpa;

    public TipoServicioRepositoryImpl(TipoServicioJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public TipoServicio guardar(TipoServicio tipoServicio) {
        TipoServicioEntity entity = toEntity(tipoServicio);
        return toModel(jpa.save(entity));
    }

    @Override
    public List<TipoServicio> obtenerTodas() {
        return jpa.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TipoServicio> buscarPorId(Integer id) {
        return jpa.findById(id).map(this::toModel);
    }

    @Override
    public TipoServicio actualizar(TipoServicio tipoServicio) {
        Integer id = tipoServicio.getIdTipoServicio();
        return jpa.findById(id).map(entity -> {
            entity.setNombre(tipoServicio.getNombre());
            entity.setDescripcion(tipoServicio.getDescripcion());
            return toModel(jpa.save(entity));
        }).orElseThrow(() -> new RuntimeException("Error al actualizar: Tipo de servicio no encontrado"));
    }

    @Override
    public void eliminar(Integer id) {
        jpa.deleteById(id);
    }

    // MAPPERS INTERNOS
    private TipoServicioEntity toEntity(TipoServicio ts) {
        TipoServicioEntity e = new TipoServicioEntity();
        e.setIdTipoServicio(ts.getIdTipoServicio());
        e.setNombre(ts.getNombre());
        e.setDescripcion(ts.getDescripcion());
        return e;
    }

    private TipoServicio toModel(TipoServicioEntity e) {
        TipoServicio ts = new TipoServicio();
        ts.setIdTipoServicio(e.getIdTipoServicio());
        ts.setNombre(e.getNombre());
        ts.setDescripcion(e.getDescripcion());
        return ts;
    }
   

    
}
