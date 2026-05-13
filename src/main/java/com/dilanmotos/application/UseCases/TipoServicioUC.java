package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.TipoServicio;
import com.dilanmotos.domain.repository.TipoServicioRepository;
import com.dilanmotos.domain.exception.TipoServicioNotFoundException;
import com.dilanmotos.infrastructure.dto.TipoServicioRequestDTO;
import com.dilanmotos.infrastructure.dto.TipoServicioResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TipoServicioUC {
    private final TipoServicioRepository tipoServicioRepository;

    public TipoServicioUC(TipoServicioRepository tipoServicioRepository) {
        this.tipoServicioRepository = tipoServicioRepository;
    }

    public List<TipoServicioResponseDTO> listarTodas() {
        return tipoServicioRepository.obtenerTodas().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public TipoServicioResponseDTO crear(TipoServicioRequestDTO request) {
        TipoServicio tipoServicio = mapToModel(request);
        return mapToDTO(tipoServicioRepository.guardar(tipoServicio));
    }

    public TipoServicioResponseDTO obtenerPorId(Integer id) {
        return tipoServicioRepository.buscarPorId(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new TipoServicioNotFoundException("Tipo de servicio no encontrado con ID: " + id));
    }

    public TipoServicioResponseDTO actualizar(Integer id, TipoServicioRequestDTO request) {
        tipoServicioRepository.buscarPorId(id)
                .orElseThrow(() -> new TipoServicioNotFoundException("No se puede actualizar, tipo de servicio no existe: " + id));

        TipoServicio tipoServicio = mapToModel(request);
        tipoServicio.setIdTipoServicio(id);
        return mapToDTO(tipoServicioRepository.actualizar(tipoServicio));
    }

    public void eliminar(Integer id) {
        tipoServicioRepository.buscarPorId(id)
                .orElseThrow(() -> new TipoServicioNotFoundException("No se puede eliminar, tipo de servicio no encontrado: " + id));
        tipoServicioRepository.eliminar(id);
    }

    private TipoServicio mapToModel(TipoServicioRequestDTO dto) {
        TipoServicio ts = new TipoServicio();
        ts.setNombre(dto.getNombre());
        ts.setDescripcion(dto.getDescripcion());
        return ts;
    }

    private TipoServicioResponseDTO mapToDTO(TipoServicio ts) {
        TipoServicioResponseDTO dto = new TipoServicioResponseDTO();
        dto.setIdTipo(ts.getIdTipoServicio());
        dto.setNombre(ts.getNombre());
        dto.setDescripcion(ts.getDescripcion());
        return dto;
    }
    
}
