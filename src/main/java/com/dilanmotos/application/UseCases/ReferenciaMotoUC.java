package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.ReferenciaMoto;
import com.dilanmotos.domain.repository.ReferenciaMotoRepository;
import com.dilanmotos.infrastructure.dto.ReferenciaMotoRequestDTO;
import com.dilanmotos.infrastructure.dto.ReferenciaMotoResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReferenciaMotoUC {

    private final ReferenciaMotoRepository referenciaRepository;

    public ReferenciaMotoUC(ReferenciaMotoRepository referenciaRepository) {
        this.referenciaRepository = referenciaRepository;
    }

    public List<ReferenciaMotoResponseDTO> listarTodas() {
        return referenciaRepository.obtenerTodos().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ReferenciaMotoResponseDTO crear(ReferenciaMotoRequestDTO request) {
        ReferenciaMoto referencia = mapToModel(request);
        return mapToDTO(referenciaRepository.guardar(referencia));
    }

    public ReferenciaMotoResponseDTO obtenerPorId(Integer id) {
        return referenciaRepository.buscarPorId(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Referencia no encontrada con ID: " + id));
    }

    public List<ReferenciaMotoResponseDTO> listarPorMarca(Integer idMarca) {
        return referenciaRepository.obtenerPorMarca(idMarca).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public ReferenciaMotoResponseDTO actualizar(Integer id, ReferenciaMotoRequestDTO request) {
        // Buscamos la existencia en el repositorio
        referenciaRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("No se puede actualizar, la referencia no existe: " + id));

        // Mapeamos los datos del Request al modelo de dominio
        ReferenciaMoto referencia = mapToModel(request);
        referencia.setIdReferencia(id); // Aseguramos que mantenga el ID original para actualizar

        return mapToDTO(referenciaRepository.guardar(referencia));
    }

    public void eliminar(Integer id) {
        referenciaRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("No se puede eliminar, referencia no encontrada: " + id));
        referenciaRepository.eliminarPorId(id);
    }

    // --- MAPPERS MANUALES CORREGIDOS ---
// --- MAPPERS MANUALES CON CONTROL DE NULOS ---

    private ReferenciaMoto mapToModel(ReferenciaMotoRequestDTO dto) {
        ReferenciaMoto r = new ReferenciaMoto();
        r.setNombre(dto.getNombre());
        r.setIdMarca(dto.getIdMarca());
        // Si por alguna razón el frontend manda nulo, le ponemos 0.0
        r.setCilindraje(dto.getCilindraje() != null ? dto.getCilindraje() : 0.0); 
        return r;
    }

    private ReferenciaMotoResponseDTO mapToDTO(ReferenciaMoto r) {
        ReferenciaMotoResponseDTO dto = new ReferenciaMotoResponseDTO();
        dto.setIdReferencia(r.getIdReferencia());
        dto.setNombre(r.getNombre());
        dto.setIdMarca(r.getIdMarca());
        // ¡ESTA ES LA LÍNEA SALVADORA!: Si en la BD es null, mapea 0.0 para que no bote Error 500
        dto.setCilindraje(r.getCilindraje() != null ? r.getCilindraje() : 0.0); 
        return dto;
    }
}