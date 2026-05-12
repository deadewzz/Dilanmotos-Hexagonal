package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.PQRS;
import com.dilanmotos.domain.repository.PqrsRepository;
import com.dilanmotos.domain.exception.PqrsNotFoundException;
import com.dilanmotos.infrastructure.dto.PqrsRequestDTO;
import com.dilanmotos.infrastructure.dto.PqrsResponseDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PqrsUC {

    private final PqrsRepository pqrsRepository;

    public PqrsUC(PqrsRepository pqrsRepository) {
        this.pqrsRepository = pqrsRepository;
    }

    public List<PqrsResponseDTO> listarTodas() {
        return pqrsRepository.obtenerTodos().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PqrsResponseDTO crear(PqrsRequestDTO request) {
        PQRS pqrs = mapToModel(request);
        pqrs.setFecha(LocalDateTime.now());
        return mapToDTO(pqrsRepository.guardar(pqrs));
    }

    public PqrsResponseDTO obtenerPorId(Integer id) {
        return pqrsRepository.buscarPorId(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new PqrsNotFoundException("PQRS no encontrada con ID: " + id));
    }

    public PqrsResponseDTO actualizar(Integer id, PqrsRequestDTO request) {
        // 1. Verificar que el registro exista antes de intentar actualizar
        PQRS pqrsExistente = pqrsRepository.buscarPorId(id)
                .orElseThrow(() -> new PqrsNotFoundException("No se puede actualizar, PQRS no existe: " + id));

        // 2. Mapear los nuevos datos al modelo
        PQRS pqrs = mapToModel(request);
        pqrs.setId_pqrs(id);
        
        // 3. Mantener la fecha original (para que no se pierda el registro de creación)
        pqrs.setFecha(pqrsExistente.getFecha());
        
        // 4. Guardar cambios y devolver el DTO
        return mapToDTO(pqrsRepository.guardar(pqrs)); // En JPA, 'guardar' sirve para crear y actualizar
    }

    public void eliminar(Integer id) {
        pqrsRepository.buscarPorId(id)
                .orElseThrow(() -> new PqrsNotFoundException("No se puede eliminar, PQRS no encontrada: " + id));
        pqrsRepository.eliminarPorId(id);
    }

    private PQRS mapToModel(PqrsRequestDTO dto) {
        PQRS p = new PQRS();
        p.setId_usuario(dto.getId_usuario());
        p.setTipo(dto.getTipo());
        p.setAsunto(dto.getAsunto());
        p.setDescripcion(dto.getDescripcion());
        return p;
    }

    private PqrsResponseDTO mapToDTO(PQRS p) {
        PqrsResponseDTO dto = new PqrsResponseDTO();
        dto.setId_pqrs(p.getId_pqrs());
        dto.setId_usuario(p.getId_usuario());
        dto.setTipo(p.getTipo());
        dto.setAsunto(p.getAsunto());
        dto.setDescripcion(p.getDescripcion());
        dto.setFecha(p.getFecha());
        dto.setRespuesta_admin(p.getRespuesta_admin());
        dto.setFecha_respuesta(p.getFecha_respuesta());
        dto.setCalificacion_servicio(p.getCalificacion_servicio());
        dto.setComentario_servicio(p.getComentario_servicio());
        return dto;
    }
}