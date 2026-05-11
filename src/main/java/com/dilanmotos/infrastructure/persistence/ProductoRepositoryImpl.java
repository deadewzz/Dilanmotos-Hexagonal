package com.dilanmotos.infrastructure.persistence;

import com.dilanmotos.domain.model.Producto;
import com.dilanmotos.domain.repository.ProductoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProductoRepositoryImpl implements ProductoRepository {

    private final ProductoJpaRepository jpaRepository;

    public ProductoRepositoryImpl(ProductoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Producto guardar(Producto producto) {
        ProductoEntity entity = toEntity(producto);
        return toModel(jpaRepository.save(entity));
    }

    @Override
    public List<Producto> obtenerTodos() {
        return jpaRepository.findAll().stream().map(this::toModel).collect(Collectors.toList());
    }

    @Override
    public Optional<Producto> buscarPorId(Integer id) {
        return jpaRepository.findById(id).map(this::toModel);
    }

    @Override
    public Producto actualizar(Integer id, Producto producto) {
        return jpaRepository.findById(id).map(entity -> {
            entity.setIdCategoria(producto.getIdCategoria());
            entity.setIdMarca(producto.getIdMarca());
            entity.setNombre(producto.getNombre());
            entity.setDescripcion(producto.getDescripcion());
            entity.setPrecio(producto.getPrecio());
            return toModel(jpaRepository.save(entity));
        }).orElseThrow(() -> new RuntimeException("ID no encontrado"));
    }

    @Override
    public void eliminar(Integer id) {
        jpaRepository.deleteById(id);
    }

    private Producto toModel(ProductoEntity entity) {
        Producto p = new Producto();
        p.setIdProducto(entity.getIdProducto());
        p.setIdCategoria(entity.getIdCategoria());
        p.setIdMarca(entity.getIdMarca());
        p.setNombre(entity.getNombre());
        p.setDescripcion(entity.getDescripcion());
        p.setPrecio(entity.getPrecio());
        return p;
    }

    private ProductoEntity toEntity(Producto p) {
        ProductoEntity entity = new ProductoEntity();
        entity.setIdCategoria(p.getIdCategoria());
        entity.setIdMarca(p.getIdMarca());
        entity.setNombre(p.getNombre());
        entity.setDescripcion(p.getDescripcion());
        entity.setPrecio(p.getPrecio());
        return entity;
    }
}