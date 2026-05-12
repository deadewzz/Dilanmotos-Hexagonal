package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.Historial;
import com.dilanmotos.domain.repository.HistorialRepository;
import com.dilanmotos.domain.exception.HistorialNotFoundException;
import com.dilanmotos.infrastructure.dto.HistorialRequestDTO;
import com.dilanmotos.infrastructure.dto.HistorialResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistorialUC {

    private final HistorialRepository historialRepository;

    public HistorialUC(HistorialRepository historialRepository) {
        this.historialRepository = historialRepository;
    }

    public List<HistorialResponseDTO> listarTodas() {
        return historialRepository.obtenerTodas().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public HistorialResponseDTO crear(HistorialRequestDTO request) {
        Historial historial = mapToModel(request);
        return mapToDTO(historialRepository.guardar(historial));
    }

    public HistorialResponseDTO obtenerPorId(Integer id) {
        return historialRepository.buscarPorId(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new HistorialNotFoundException("Historial no encontrado con ID: " + id));
    }

    public HistorialResponseDTO actualizar(Integer id, HistorialRequestDTO request) {
        historialRepository.buscarPorId(id)
                .orElseThrow(() -> new HistorialNotFoundException("No se puede actualizar, historial no existe: " + id));

        Historial historial = mapToModel(request);
        historial.setIdHistorial(id);
        return mapToDTO(historialRepository.actualizar(historial));
    }

    public void eliminar(Integer id) {
        historialRepository.buscarPorId(id)
                .orElseThrow(() -> new HistorialNotFoundException("No se puede eliminar, historial no encontrado: " + id));
        historialRepository.eliminar(id);
    }

    private Historial mapToModel(HistorialRequestDTO dto) {
        Historial h = new Historial();
        h.setIdUsuario(dto.getIdUsuario());
        h.setIdServicio(dto.getIdServicio());
        h.setAccion(dto.getAccion());
        h.setFecha(dto.getFecha());
        h.setDetalle(dto.getDetalle());
        return h;
    }

    private HistorialResponseDTO mapToDTO(Historial h) {
        HistorialResponseDTO dto = new HistorialResponseDTO();
        dto.setIdHistorial(h.getIdHistorial());
        dto.setIdUsuario(h.getIdUsuario());
        dto.setIdServicio(h.getIdServicio());
        dto.setAccion(h.getAccion());
        dto.setFecha(h.getFecha());
        dto.setDetalle(h.getDetalle());
        return dto;
    }
}
