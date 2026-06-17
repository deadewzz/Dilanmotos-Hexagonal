package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.Cotizacion;
import com.dilanmotos.domain.model.Producto;
import com.dilanmotos.domain.repository.CotizacionRepository;
import com.dilanmotos.domain.repository.ProductoRepository;
import com.dilanmotos.domain.exception.CotizacionNotFoundException;
import com.dilanmotos.infrastructure.dto.CotizacionRequestDTO;
import com.dilanmotos.infrastructure.dto.CotizacionResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CotizacionUC {
    private final CotizacionRepository cotizacionRepository;
    private final ProductoRepository productoRepository;

    public CotizacionUC(CotizacionRepository cotizacionRepository, ProductoRepository productoRepository) {
        this.cotizacionRepository = cotizacionRepository;
        this.productoRepository = productoRepository;
    }

    public List<CotizacionResponseDTO> listarTodas() {
        return cotizacionRepository.obtenerTodas().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<CotizacionResponseDTO> listarPorUsuario(Integer idUsuario) {
        return cotizacionRepository.findByIdUsuario(idUsuario).stream()
                .map(entity -> {
                    Cotizacion cotizacion = new Cotizacion();
                    cotizacion.setIdCotizacion(entity.getIdCotizacion());
                    cotizacion.setIdUsuario(entity.getIdUsuario());
                    cotizacion.setIdProducto(entity.getIdProducto());
                    cotizacion.setProducto(entity.getProducto());
                    cotizacion.setCantidad(entity.getCantidad());
                    cotizacion.setPrecioUnitario(entity.getPrecioUnitario());
                    cotizacion.setFecha(entity.getFecha() != null
                            ? entity.getFecha().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                            : null);
                    
                    if (entity.getUsuario() != null) {
                        cotizacion.setNombreUsuario(entity.getUsuario().getNombre());
                    }
                    
                    cotizacion.setProducto_agregado(entity.getProducto_agregado());
                    return mapToDTO(cotizacion);
                })
                .collect(Collectors.toList());
    }

    public CotizacionResponseDTO crear(CotizacionRequestDTO request) {
        Cotizacion cotizacion = mapToModel(request);
        enrichFromProducto(cotizacion);
        return mapToDTO(cotizacionRepository.guardar(cotizacion));
    }

    public CotizacionResponseDTO obtenerPorId(Integer id) {
        return cotizacionRepository.buscarPorId(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new CotizacionNotFoundException("Cotización no encontrada con ID: " + id));
    }

    public CotizacionResponseDTO actualizar(Integer id, CotizacionRequestDTO request) {
        cotizacionRepository.buscarPorId(id)
                .orElseThrow(
                        () -> new CotizacionNotFoundException("No se puede actualizar, cotización no existe: " + id));

        Cotizacion cotizacion = mapToModel(request);
        cotizacion.setIdCotizacion(id);
        enrichFromProducto(cotizacion);
        return mapToDTO(cotizacionRepository.actualizar(cotizacion));
    }

    @Transactional
    public CotizacionResponseDTO confirmarCompra(Integer id) {
        Cotizacion cotizacion = cotizacionRepository.buscarPorId(id)
                .orElseThrow(() -> new CotizacionNotFoundException("Cotización no encontrada: " + id));

        if (Boolean.TRUE.equals(cotizacion.getProducto_agregado())) {
            throw new RuntimeException("Esta cotización ya fue procesada como una venta.");
        }

        if (cotizacion.getIdProducto() != null) {
            Producto producto = productoRepository.buscarPorId(cotizacion.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("El producto asociado a esta cotización ya no existe."));

            if (producto.getStock() < cotizacion.getCantidad()) {
                throw new RuntimeException("No hay stock suficiente. Unidades disponibles: " + producto.getStock());
            }

            int nuevoStock = producto.getStock() - cotizacion.getCantidad();
            producto.setStock(nuevoStock);

            if (nuevoStock <= 0) {
                producto.setDisponible(false);
            }

            productoRepository.actualizar(producto.getIdProducto(), producto);
        }

        cotizacion.setProducto_agregado(true);
        return mapToDTO(cotizacionRepository.actualizar(cotizacion));
    }

    public void eliminar(Integer id) {
        cotizacionRepository.buscarPorId(id)
                .orElseThrow(
                        () -> new CotizacionNotFoundException("No se puede eliminar, cotización no encontrada: " + id));
        cotizacionRepository.eliminar(id);
    }

    private void enrichFromProducto(Cotizacion cotizacion) {
        if (cotizacion.getIdProducto() == null) {
            return;
        }

        productoRepository.buscarPorId(cotizacion.getIdProducto()).ifPresent(producto -> {
            if (producto.getNombre() != null && !producto.getNombre().isBlank()) {
                cotizacion.setProducto(producto.getNombre());
            }
            if (producto.getPrecio() != null && cotizacion.getPrecioUnitario() <= 0) {
                cotizacion.setPrecioUnitario(producto.getPrecio());
            }
        });
    }

    private Cotizacion mapToModel(CotizacionRequestDTO dto) {
        Cotizacion c = new Cotizacion();
        c.setIdUsuario(dto.getIdUsuario());
        c.setIdProducto(dto.getIdProducto());
        c.setProducto(dto.getProducto());
        c.setCantidad(dto.getCantidad());
        c.setPrecioUnitario(dto.getPrecioUnitario());
        if (dto.getFecha() != null) {
            c.setFecha(dto.getFecha().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate());
        }
        c.setProducto_agregado(dto.getProducto_agregado());
        return c;
    }

    private CotizacionResponseDTO mapToDTO(Cotizacion c) {
        enrichFromProducto(c);

        CotizacionResponseDTO dto = new CotizacionResponseDTO();
        dto.setIdCotizacion(c.getIdCotizacion());
        dto.setIdUsuario(c.getIdUsuario());
        
        dto.setNombreUsuario(c.getNombreUsuario()); 
        
        dto.setIdProducto(c.getIdProducto());
        dto.setProducto(c.getProducto());
        dto.setCantidad(c.getCantidad());
        dto.setPrecioUnitario(c.getPrecioUnitario());
        if (c.getFecha() != null) {
            dto.setFecha(Date.from(c.getFecha()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()));
        }
        dto.setProducto_agregado(c.getProducto_agregado());
        return dto;
    }
}