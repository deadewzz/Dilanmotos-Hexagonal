package com.dilanmotos.infrastructure.persistence;

import com.dilanmotos.domain.model.Marca;
import com.dilanmotos.domain.repository.MarcaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MarcaRepositoryImpl implements MarcaRepository {

    private final MarcaJpaRepository jpa;

    public MarcaRepositoryImpl(MarcaJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Marca guardar(Marca marca) {
        MarcaEntity entity = toEntity(marca);
        return toModel(jpa.save(entity));
    }

    @Override
    public List<Marca> obtenerTodos() {
        return jpa.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Marca> buscarPorId(Integer id) {
        return jpa.findById(id).map(this::toModel);
    }

    @Override
    public Marca actualizar(Marca marca) {
        Integer id = marca.getIdMarca();
        return jpa.findById(id).map(entity -> {
            entity.setNombre(marca.getNombre());
            return toModel(jpa.save(entity));
        }).orElseThrow(() -> new RuntimeException("Error al actualizar: Marca no encontrada"));
    }

    @Override
    public void eliminar(Integer id) {
        jpa.deleteById(id);
    }

    // MAPPERS INTERNOS
    private Marca toModel(MarcaEntity e) {
        Marca m = new Marca();
        m.setIdMarca(e.getIdMarca());
        m.setNombre(e.getNombre());
        return m;
    }

    private MarcaEntity toEntity(Marca m) {
        MarcaEntity e = new MarcaEntity();
        if (m.getIdMarca() != null) {
            e.setIdMarca(m.getIdMarca());
        }
        e.setNombre(m.getNombre());
        return e;
    }
}