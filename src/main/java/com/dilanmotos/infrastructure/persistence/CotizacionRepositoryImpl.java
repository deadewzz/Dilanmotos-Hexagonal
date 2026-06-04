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
    public List<CotizacionEntity> findByIdUsuario(Integer idUsuario) {
        return jpa.findByIdUsuario(idUsuario);
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
    private Cotizacion toModel(CotizacionEntity entity) {
        Cotizacion c = new Cotizacion();
        c.setIdCotizacion(entity.getIdCotizacion());
        c.setIdUsuario(entity.getIdUsuario());
        c.setIdProducto(entity.getIdProducto());
        c.setProducto(entity.getProducto());
        c.setCantidad(entity.getCantidad());
        c.setPrecioUnitario(entity.getPrecioUnitario());
        c.setFecha(entity.getFecha());
        c.setProducto_agregado(entity.getProducto_agregado());
        return c;
    }

    // MAPPERS INTERNOS
    private CotizacionEntity toEntity(Cotizacion c) {
        CotizacionEntity entity = new CotizacionEntity();
        entity.setIdCotizacion(c.getIdCotizacion());
        entity.setIdUsuario(c.getIdUsuario());
        entity.setIdProducto(c.getIdProducto());
        entity.setProducto(c.getProducto());
        entity.setCantidad(c.getCantidad());
        entity.setPrecioUnitario(c.getPrecioUnitario());
        entity.setFecha(c.getFecha());
        entity.setProducto_agregado(c.getProducto_agregado());
        return entity;
    }

}
