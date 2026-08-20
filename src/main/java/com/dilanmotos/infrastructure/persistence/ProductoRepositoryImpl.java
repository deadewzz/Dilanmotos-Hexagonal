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
    public List<Producto> obtenerTodos() {
        return jpaRepository.findAllConRelaciones().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    // Mapeo Entity -> Model (Aquí se enriquece con los nombres para el frontend)
    private Producto toModel(ProductoEntity entity) {
        Producto p = new Producto();
        p.setIdProducto(entity.getIdProducto());
        p.setIdCategoria(entity.getIdCategoria());
        p.setIdMarca(entity.getIdMarca());
        p.setNombre(entity.getNombre());
        p.setDescripcion(entity.getDescripcion());
        p.setPrecio(entity.getPrecio());
        p.setImagen_url(entity.getImagenUrl());
        
        // Mapeo explícito de Stock y Disponible
        p.setStock(entity.getStock() != null ? entity.getStock() : 0);
        p.setDisponible(entity.getDisponible() != null ? entity.getDisponible() : true);

        // Mapeo de Nombres de Categoría y Marca desde los JOINs
        if (entity.getMarca() != null) {
            p.setNombreMarca(entity.getMarca().getNombre());
        } else {
            p.setNombreMarca("Sin Marca");
        }

        if (entity.getCategoria() != null) {
            p.setNombreCategoria(entity.getCategoria().getNombre());
        } else {
            p.setNombreCategoria("Sin Categoría");
        }

        return p;
    }

    // Mapeo Model -> Entity
    private ProductoEntity toEntity(Producto p) {
        ProductoEntity entity = new ProductoEntity();
        if (p.getIdProducto() != null) {
            entity.setIdProducto(p.getIdProducto());
        }
        entity.setIdCategoria(p.getIdCategoria());
        entity.setIdMarca(p.getIdMarca());
        entity.setNombre(p.getNombre());
        entity.setDescripcion(p.getDescripcion());
        entity.setPrecio(p.getPrecio());
        entity.setImagenUrl(p.getImagen_url());
        entity.setStock(p.getStock() != null ? p.getStock() : 0);
        entity.setDisponible(p.getDisponible() != null ? p.getDisponible() : true);
        return entity;
    }

    @Override
    public Producto guardar(Producto producto) {
        ProductoEntity entity = jpaRepository.save(toEntity(producto));
        // Volvemos a consultar para traer los datos con JOINs completos
        return jpaRepository.findById(entity.getIdProducto())
                .map(this::toModel)
                .orElse(toModel(entity));
    }

    @Override
    public Optional<Producto> buscarPorId(Integer id) {
        return jpaRepository.findById(id).map(this::toModel);
    }

    @Override
    public List<Producto> obtenerPorCategoria(Integer idCategoria) {
        return jpaRepository.findByIdCategoria(idCategoria).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Producto actualizar(Integer id, Producto producto) {
        return jpaRepository.findById(id).map(entity -> {
            entity.setIdCategoria(producto.getIdCategoria());
            entity.setIdMarca(producto.getIdMarca());
            entity.setNombre(producto.getNombre());
            entity.setDescripcion(producto.getDescripcion());
            entity.setPrecio(producto.getPrecio());
            entity.setImagenUrl(producto.getImagen_url());
            entity.setStock(producto.getStock());
            entity.setDisponible(producto.getDisponible());
            ProductoEntity guardado = jpaRepository.save(entity);
            return toModel(guardado);
        }).orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
    }

    @Override
    public void eliminar(Integer id) {
        jpaRepository.deleteById(id);
    }
}