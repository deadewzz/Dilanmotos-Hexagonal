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

    // --- MAPPERS MANUALES ---

    private ReferenciaMoto mapToModel(ReferenciaMotoRequestDTO dto) {
        ReferenciaMoto r = new ReferenciaMoto();
        r.setNombre(dto.getNombre());
        r.setIdMarca(dto.getIdMarca());
        return r;
    }

    private ReferenciaMotoResponseDTO mapToDTO(ReferenciaMoto r) {
        ReferenciaMotoResponseDTO dto = new ReferenciaMotoResponseDTO();
        dto.setIdReferencia(r.getIdReferencia());
        dto.setNombre(r.getNombre());
        dto.setIdMarca(r.getIdMarca());
        return dto;
    }
}