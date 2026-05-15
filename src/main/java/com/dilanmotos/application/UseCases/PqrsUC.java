package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.PQRS;
import com.dilanmotos.domain.repository.PqrsRepository;
import com.dilanmotos.domain.exception.PqrsNotFoundException;
import com.dilanmotos.infrastructure.dto.PqrsRequestDTO;
import com.dilanmotos.infrastructure.dto.PqrsResponseDTO;
import com.dilanmotos.infrastructure.dto.PqrsUpdateDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    public PqrsResponseDTO obtenerPorId(Integer id) {
        return pqrsRepository.buscarPorId(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new PqrsNotFoundException("PQRS no encontrada: " + id));
    }

    @Transactional
    public PqrsResponseDTO crear(PqrsRequestDTO request) {
        PQRS pqrs = new PQRS();
        pqrs.setId_usuario(request.getIdUsuario());
        pqrs.setTipo(request.getTipo());
        pqrs.setAsunto(request.getAsunto());
        pqrs.setDescripcion(request.getDescripcion());
        pqrs.setFecha(LocalDateTime.now());
        pqrs.setEstado("PENDIENTE");
        pqrs.setRespuesta_admin("");
        
        return mapToDTO(pqrsRepository.guardar(pqrs));
    }

    @Transactional
    public PqrsResponseDTO actualizar(Integer id, PqrsRequestDTO request) {
        PQRS pqrsExistente = pqrsRepository.buscarPorId(id)
                .orElseThrow(() -> new PqrsNotFoundException("PQRS no encontrada: " + id));
        
        // Actualizar solo si los campos no son null
        if (request.getEstado() != null) {
            pqrsExistente.setEstado(request.getEstado());
        }
        
        if (request.getRespuesta_admin() != null) {
            pqrsExistente.setRespuesta_admin(request.getRespuesta_admin());
            if (!request.getRespuesta_admin().isEmpty()) {
                pqrsExistente.setFecha_respuesta(LocalDateTime.now());
            }
        }
        
        if (request.getTipo() != null) {
            pqrsExistente.setTipo(request.getTipo());
        }
        
        if (request.getAsunto() != null) {
            pqrsExistente.setAsunto(request.getAsunto());
        }
        
        if (request.getDescripcion() != null) {
            pqrsExistente.setDescripcion(request.getDescripcion());
        }
        
        return mapToDTO(pqrsRepository.guardar(pqrsExistente));
    }
    
    // NUEVO MÉTODO: Para actualización solo de admin
    @Transactional
    public PqrsResponseDTO actualizarAdmin(Integer id, PqrsUpdateDTO request) {
        System.out.println("=== ACTUALIZACIÓN ADMIN PQRS ID: " + id + " ===");
        
        PQRS pqrsExistente = pqrsRepository.buscarPorId(id)
                .orElseThrow(() -> new PqrsNotFoundException("PQRS no encontrada: " + id));
        
        System.out.println("Estado anterior: " + pqrsExistente.getEstado());
        System.out.println("Respuesta anterior: " + pqrsExistente.getRespuesta_admin());
        
        // Actualizar estado
        if (request.getEstado() != null) {
            pqrsExistente.setEstado(request.getEstado());
            System.out.println("Nuevo estado: " + request.getEstado());
        }
        
        // Actualizar respuesta_admin
        if (request.getRespuesta_admin() != null) {
            pqrsExistente.setRespuesta_admin(request.getRespuesta_admin());
            System.out.println("Nueva respuesta: " + request.getRespuesta_admin());
            
            // Si hay respuesta, actualizar fecha_respuesta
            if (!request.getRespuesta_admin().isEmpty()) {
                pqrsExistente.setFecha_respuesta(LocalDateTime.now());
                System.out.println("Fecha respuesta: " + LocalDateTime.now());
            }
        }
        
        PQRS guardado = pqrsRepository.guardar(pqrsExistente);
        System.out.println("Estado final: " + guardado.getEstado());
        
        return mapToDTO(guardado);
    }

    @Transactional
    public void eliminar(Integer id) {
        pqrsRepository.eliminarPorId(id);
    }

    private PqrsResponseDTO mapToDTO(PQRS p) {
        PqrsResponseDTO dto = new PqrsResponseDTO();
        dto.setId_pqrs(p.getId_pqrs());
        dto.setId_usuario(p.getId_usuario());
        dto.setTipo(p.getTipo());
        dto.setAsunto(p.getAsunto());
        dto.setDescripcion(p.getDescripcion());
        dto.setFecha(p.getFecha());
        dto.setEstado(p.getEstado() != null ? p.getEstado() : "PENDIENTE");
        dto.setRespuesta_admin(p.getRespuesta_admin());
        dto.setFecha_respuesta(p.getFecha_respuesta());
        return dto;
    }
}