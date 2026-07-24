package com.dilanmotos.infrastructure.persistence;

import com.dilanmotos.domain.model.MarcaProducto;
import com.dilanmotos.domain.repository.MarcaProductoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MarcaProductoRepositoryImpl implements MarcaProductoRepository {

    private final MarcaProductoJpaRepository jpaRepository;

    public MarcaProductoRepositoryImpl(MarcaProductoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public MarcaProducto guardar(MarcaProducto marcaProducto) {
        // Mapear de Dominio a Entidad, guardar y volver a mapear a Dominio
        MarcaProductoEntity entity = toEntity(marcaProducto);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<MarcaProducto> obtenerTodos() {
        return jpaRepository.findAllConCategoria()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MarcaProducto> obtenerPorCategoria(Integer idCategoria) {
        return jpaRepository.findByCategoriaIdCategoria(idCategoria)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MarcaProducto> buscarPorId(Integer id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public MarcaProducto actualizar(MarcaProducto marcaProducto) {
        MarcaProductoEntity entity = toEntity(marcaProducto);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public void eliminar(Integer id) {
        jpaRepository.deleteById(id);
    }

    // --- Mapeadores aux (o usa MapStruct) ---
    private MarcaProducto toDomain(MarcaProductoEntity entity) {
    if (entity == null) return null;
    
    return new MarcaProducto(
        entity.getIdMarca(), 
        entity.getNombre(), 
        entity.getCategoria() != null ? entity.getCategoria().getIdCategoria() : null
    );
}

    private MarcaProductoEntity toEntity(MarcaProducto domain) {
    if (domain == null) return null;

    MarcaProductoEntity entity = new MarcaProductoEntity();
    entity.setIdMarca(domain.getIdMarcaProducto()); // 👈 Cambiado de getId() a getIdMarca()
    entity.setNombre(domain.getNombre());
    
    // Si necesitas asociar la CategoriaEntity
    if (domain.getIdCategoria() != null) {
        CategoriaEntity categoriaEntity = new CategoriaEntity();
        categoriaEntity.setIdCategoria(domain.getIdCategoria());
        entity.setCategoria(categoriaEntity);
    }

    return entity;
}
}