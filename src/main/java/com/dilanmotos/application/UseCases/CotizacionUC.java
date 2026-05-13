package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.Cotizacion;
import com.dilanmotos.domain.repository.CotizacionRepository;
import com.dilanmotos.domain.exception.CotizacionNotFoundException;
import com.dilanmotos.infrastructure.dto.CotizacionRequestDTO;
import com.dilanmotos.infrastructure.dto.CotizacionResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CotizacionUC {
    private final CotizacionRepository cotizacionRepository;

    public CotizacionUC(CotizacionRepository cotizacionRepository) {
        this.cotizacionRepository = cotizacionRepository;
    }

    public List<CotizacionResponseDTO> listarTodas() {
        return cotizacionRepository.obtenerTodas().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CotizacionResponseDTO crear(CotizacionRequestDTO request) {
        Cotizacion cotizacion = mapToModel(request);
        return mapToDTO(cotizacionRepository.guardar(cotizacion));
    }

    public CotizacionResponseDTO obtenerPorId(Integer id) {
        return cotizacionRepository.buscarPorId(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new CotizacionNotFoundException("Cotización no encontrada con ID: " + id));
    }

    public CotizacionResponseDTO actualizar(Integer id, CotizacionRequestDTO request) {
        cotizacionRepository.buscarPorId(id)
                .orElseThrow(() -> new CotizacionNotFoundException("No se puede actualizar, cotización no existe: " + id));

        Cotizacion cotizacion = mapToModel(request);
        cotizacion.setIdCotizacion(id);
        return mapToDTO(cotizacionRepository.actualizar(cotizacion));
    }

    public void eliminar(Integer id) {
        cotizacionRepository.buscarPorId(id)
                .orElseThrow(() -> new CotizacionNotFoundException("No se puede eliminar, cotización no encontrada: " + id));
        cotizacionRepository.eliminar(id);
    }

    private Cotizacion mapToModel(CotizacionRequestDTO dto) {
        Cotizacion c = new Cotizacion();
        c.setIdUsuario(dto.getIdUsuario());
        c.setProducto(dto.getProducto());
        c.setCantidad(dto.getCantidad());
        c.setPrecioUnitario(dto.getPrecioUnitario());
        c.setFecha(dto.getFecha());
        c.setProducto_agregado(dto.getProducto_agregado());
        return c;
    }

    private CotizacionResponseDTO mapToDTO(Cotizacion c) {
        CotizacionResponseDTO dto = new CotizacionResponseDTO();
        dto.setIdCotizacion(c.getIdCotizacion());
        dto.setIdUsuario(c.getIdUsuario());
        dto.setProducto(c.getProducto());
        dto.setCantidad(c.getCantidad());
        dto.setPrecioUnitario(c.getPrecioUnitario());
        dto.setFecha(c.getFecha());
        dto.setProducto_agregado(c.getProducto_agregado());
        return dto;
    }
}
