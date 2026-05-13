package com.dilanmotos.infrastructure.persistence;

import com.dilanmotos.domain.model.ReferenciaMoto;
import com.dilanmotos.domain.repository.ReferenciaMotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReferenciaMotoRepositoryImpl implements ReferenciaMotoRepository {

    private final ReferenciaMotoJpaRepository jpaRepository;

    @Override
    public ReferenciaMoto guardar(ReferenciaMoto referencia) {
        ReferenciaEntity entity = mapToEntity(referencia);
        ReferenciaEntity savedEntity = jpaRepository.save(entity);
        return mapToDomain(savedEntity);
    }

    @Override
    public List<ReferenciaMoto> obtenerTodos() {
        return jpaRepository.findAll().stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public Optional<ReferenciaMoto> buscarPorId(int id) {
        return jpaRepository.findById(id).map(this::mapToDomain);
    }

    @Override
    public void eliminarPorId(int id) {
        jpaRepository.deleteById(id);
    }

    // --- MAPPERS ---

    private ReferenciaMoto mapToDomain(ReferenciaEntity entity) {
        if (entity == null) return null;
        ReferenciaMoto domain = new ReferenciaMoto();
        domain.setIdReferencia(entity.getIdReferencia());
        domain.setNombre(entity.getNombre());
        
        if (entity.getMarca() != null) {
            domain.setIdMarca(entity.getMarca().getIdMarca());
        }
        return domain;
    }

    private ReferenciaEntity mapToEntity(ReferenciaMoto domain) {
        if (domain == null) return null;
        ReferenciaEntity entity = new ReferenciaEntity();
        entity.setIdReferencia(domain.getIdReferencia());
        entity.setNombre(domain.getNombre());

        // IMPORTANTE: Para que no cree una marca nueva, le pasamos una entidad con el ID
        if (domain.getIdMarca() != null) {
            MarcaEntity marca = new MarcaEntity();
            marca.setIdMarca(domain.getIdMarca());
            entity.setMarca(marca);
        }

        return entity;
    }
}