package com.dilanmotos.infrastructure.persistence;

import com.dilanmotos.domain.model.Cotizacion;
import com.dilanmotos.domain.repository.CotizacionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CotizacionRepositoryImpl implements CotizacionRepository {

    private final CotizacionJpaRepository jpa;

    public CotizacionRepositoryImpl(CotizacionJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Cotizacion guardar(Cotizacion cotizacion) {
        CotizacionEntity entity = toEntity(cotizacion);
        return toModel(jpa.save(entity));
    }

    @Override
    public List<Cotizacion> obtenerTodas() {
        return jpa.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Cotizacion> buscarPorId(Integer id) {
        return jpa.findById(id).map(this::toModel);
    }

    @Override
    public Cotizacion actualizar(Cotizacion cotizacion) {
        Integer id = cotizacion.getIdCotizacion();
        return jpa.findById(id).map(entity -> {
            entity.setIdUsuario(cotizacion.getIdUsuario());
            entity.setProducto(cotizacion.getProducto());
            entity.setCantidad(cotizacion.getCantidad());
            entity.setPrecioUnitario(cotizacion.getPrecioUnitario());
            entity.setFecha(cotizacion.getFecha());
            entity.setProducto_agregado(cotizacion.getProducto_agregado());
            return toModel(jpa.save(entity));
        }).orElseThrow(() -> new RuntimeException("Error al actualizar: Cotización no encontrada"));
    }

    @Override
    public void eliminar(Integer id) {
        jpa.deleteById(id);
    
    }

    // MAPPERS INTERNOS
    private Cotizacion toModel(CotizacionEntity e) {
        Cotizacion c = new Cotizacion();
        c.setIdCotizacion(e.getIdCotizacion());
        c.setIdUsuario(e.getIdUsuario());
        c.setProducto(e.getProducto());
        c.setCantidad(e.getCantidad());
        c.setPrecioUnitario(e.getPrecioUnitario());
        c.setFecha(e.getFecha());
        c.setProducto_agregado(e.getProducto_agregado());
        return c;
    }

    private CotizacionEntity toEntity(Cotizacion c) {
        CotizacionEntity e = new CotizacionEntity();
        if (c.getIdCotizacion() != null) {
            e.setIdCotizacion(c.getIdCotizacion());
        }
        e.setIdUsuario(c.getIdUsuario());
        e.setProducto(c.getProducto());
        e.setCantidad(c.getCantidad());
        e.setPrecioUnitario(c.getPrecioUnitario());
        e.setFecha(c.getFecha());
        e.setProducto_agregado(c.getProducto_agregado());
        return e;
    }
}
