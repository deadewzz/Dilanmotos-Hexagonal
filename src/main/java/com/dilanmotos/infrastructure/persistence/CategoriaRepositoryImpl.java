package com.dilanmotos.infrastructure.persistence;

import com.dilanmotos.domain.model.Categoria;
import com.dilanmotos.domain.repository.CategoriaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CategoriaRepositoryImpl implements CategoriaRepository {

    private final CategoriaJpaRepository jpa;

    public CategoriaRepositoryImpl(CategoriaJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Categoria guardar(Categoria categoria) {
        CategoriaEntity entity = toEntity(categoria);
        return toModel(jpa.save(entity));
    }

    @Override
    public List<Categoria> obtenerTodas() {
        return jpa.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Categoria> buscarPorId(Integer id) {
        return jpa.findById(id).map(this::toModel);
    }

    @Override
    public Categoria actualizar(Categoria categoria) { 
        Integer id = categoria.getIdCategoria();
        return jpa.findById(id).map(entity -> {
            entity.setNombre(categoria.getNombre());
            return toModel(jpa.save(entity));
        }).orElseThrow(() -> new RuntimeException("Error al actualizar: Categoria no encontrada"));
    }

    @Override
    public void eliminar(Integer id) {
        jpa.deleteById(id);
    }

    private Categoria toModel(CategoriaEntity e) {
        Categoria c = new Categoria();
        c.setIdCategoria(e.getIdCategoria());
        c.setNombre(e.getNombre());
        return c;
    }

    private CategoriaEntity toEntity(Categoria c) {
        CategoriaEntity e = new CategoriaEntity();
        e.setIdCategoria(c.getIdCategoria());
        e.setNombre(c.getNombre());
        return e;
    }
    
}
