package com.dilanmotos.infrastructure.persistence;

import com.dilanmotos.domain.model.PQRS;
import com.dilanmotos.domain.repository.PqrsRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PqrsJpaAdapter implements PqrsRepository {

    // 1. Debe llamar a la INTERFAZ que extiende JpaRepository
    private final PqrsJpaRepository jpaRepository;

    // 2. El constructor debe llamarse IGUAL que la clase (PqrsJpaAdapter)
    public PqrsJpaAdapter(PqrsJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<PQRS> obtenerTodos() {
        return jpaRepository.findAll().stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    public PQRS guardar(PQRS pqrs) {
        PqrsEntity entity = toEntity(pqrs);
        return toModel(jpaRepository.save(entity));
    }

    @Override
    public Optional<PQRS> buscarPorId(int id) {
        return jpaRepository.findById(id).map(this::toModel);
    }

    @Override
    public void eliminarPorId(int id) {
        jpaRepository.deleteById(id);
    }

    // MÉTODOS DE MAPEO (Entity <-> Model)
    private PQRS toModel(PqrsEntity entity) {
        return new PQRS(
            entity.getId_pqrs(), 
            entity.getId_usuario(), 
            entity.getTipo(),
            entity.getAsunto(), 
            entity.getDescripcion(), 
            entity.getFecha(),
            entity.getRespuesta_admin(), 
            entity.getFecha_respuesta(),
            entity.getCalificacion_servicio(), 
            entity.getComentario_servicio()
        );
    }

    private PqrsEntity toEntity(PQRS model) {
        PqrsEntity entity = new PqrsEntity();
        entity.setId_pqrs(model.getId_pqrs());
        entity.setId_usuario(model.getId_usuario());
        entity.setTipo(model.getTipo());
        entity.setAsunto(model.getAsunto());
        entity.setDescripcion(model.getDescripcion());
        entity.setFecha(model.getFecha());
        entity.setRespuesta_admin(model.getRespuesta_admin());
        entity.setFecha_respuesta(model.getFecha_respuesta());
        entity.setCalificacion_servicio(model.getCalificacion_servicio());
        entity.setComentario_servicio(model.getComentario_servicio());
        return entity;
    }
}