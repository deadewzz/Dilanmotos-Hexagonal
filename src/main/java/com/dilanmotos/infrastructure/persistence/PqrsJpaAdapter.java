package com.dilanmotos.infrastructure.persistence;

import com.dilanmotos.domain.model.PQRS;
import com.dilanmotos.domain.repository.PqrsRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PqrsJpaAdapter implements PqrsRepository {

    private final PqrsJpaRepository jpaRepository;

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
        System.out.println("=== GUARDANDO PQRS ===");
        System.out.println("Model a guardar - ID: " + pqrs.getId_pqrs());
        System.out.println("Model a guardar - Estado: " + pqrs.getEstado());
        System.out.println("Model a guardar - Respuesta: " + pqrs.getRespuesta_admin());
        
        PqrsEntity entity = toEntity(pqrs);
        System.out.println("Entity antes de guardar - Estado: " + entity.getEstado());
        System.out.println("Entity antes de guardar - Respuesta: " + entity.getRespuesta_admin());
        
        PqrsEntity savedEntity = jpaRepository.save(entity);
        System.out.println("Entity después de guardar - Estado: " + savedEntity.getEstado());
        System.out.println("Entity después de guardar - Respuesta: " + savedEntity.getRespuesta_admin());
        
        return toModel(savedEntity);
    }

    @Override
    public Optional<PQRS> buscarPorId(int id) {
        return jpaRepository.findById(id).map(this::toModel);
    }

    @Override
    public void eliminarPorId(int id) {
        jpaRepository.deleteById(id);
    }

    // MÉTODO toModel CORREGIDO - Asegurar que estado no sea null
    private PQRS toModel(PqrsEntity entity) {
        if (entity == null) return null;
        
        PQRS model = new PQRS();
        model.setId_pqrs(entity.getId_pqrs());
        model.setId_usuario(entity.getId_usuario());
        model.setTipo(entity.getTipo());
        model.setAsunto(entity.getAsunto());
        model.setDescripcion(entity.getDescripcion());
        model.setFecha(entity.getFecha());
        model.setRespuesta_admin(entity.getRespuesta_admin());
        model.setFecha_respuesta(entity.getFecha_respuesta());
        model.setCalificacion_servicio(entity.getCalificacion_servicio());
        model.setComentario_servicio(entity.getComentario_servicio());
        model.setEstado(entity.getEstado() != null ? entity.getEstado() : "PENDIENTE");
        
        return model;
    }

    // MÉTODO toEntity CORREGIDO - Asegurar que el estado se copie correctamente
    private PqrsEntity toEntity(PQRS model) {
        if (model == null) return null;
        
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
        entity.setEstado(model.getEstado()); // CRUCIAL: Asegurar que el estado se asigna
        
        // Log para debug
        System.out.println("toEntity - Estado asignado: " + entity.getEstado());
        
        return entity;
    }
}