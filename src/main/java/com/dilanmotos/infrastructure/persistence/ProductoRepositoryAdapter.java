package com.dilanmotos.infrastructure.persistence;

import com.dilanmotos.domain.model.Producto;
import com.dilanmotos.domain.repository.ProductoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProductoRepositoryAdapter implements ProductoRepository {

    private final ProductoJpaRepository jpaRepository;

    public ProductoRepositoryAdapter(ProductoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Producto guardar(Producto producto) {
        ProductoEntity entity = new ProductoEntity();
        entity.setIdCategoria(producto.getIdCategoria());
        entity.setIdMarca(producto.getIdMarca());
        entity.setNombre(producto.getNombre());
        entity.setDescripcion(producto.getDescripcion());
        entity.setPrecio(producto.getPrecio());

        ProductoEntity saved = jpaRepository.save(entity);
        producto.setIdProducto(saved.getIdProducto());
        return producto;
    }

    @Override
    public List<Producto> obtenerTodos() {
        return jpaRepository.findAll().stream().map(this::toModel).collect(Collectors.toList());
    }

    @Override
    public Optional<Producto> buscarPorId(Integer id) {
        return jpaRepository.findById(id).map(this::toModel);
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
}