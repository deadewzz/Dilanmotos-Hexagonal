package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.Producto;
import com.dilanmotos.domain.repository.ProductoRepository;
import com.dilanmotos.infrastructure.dto.ProductoRequestDTO;
import com.dilanmotos.infrastructure.dto.ProductoResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoUC {

    private final ProductoRepository productoRepository;

    public ProductoUC(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<ProductoResponseDTO> listarTodos() {
        return productoRepository.obtenerTodos().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public ProductoResponseDTO buscarPorId(Integer id) {
        return productoRepository.buscarPorId(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    public ProductoResponseDTO crear(ProductoRequestDTO request) {
        Producto p = toModel(request);
        Producto guardado = productoRepository.guardar(p);
        return toResponseDTO(guardado);
    }

    public ProductoResponseDTO actualizar(Integer id, ProductoRequestDTO request) {
        Producto p = toModel(request);
        Producto actualizado = productoRepository.actualizar(id, p);
        return toResponseDTO(actualizado);
    }

    public void eliminar(Integer id) {
        productoRepository.eliminar(id);
    }

    // Mapeador RequestDTO -> Model
    private Producto toModel(ProductoRequestDTO dto) {
        Producto p = new Producto();
        p.setIdCategoria(dto.getIdCategoria());
        p.setIdMarca(dto.getIdMarca());
        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setPrecio(dto.getPrecio());
        p.setImagen_url(dto.getImagenUrl());
        p.setStock(dto.getStock() != null ? dto.getStock() : 0);
        p.setDisponible(dto.getDisponible() != null ? dto.getDisponible() : true);
        return p;
    }

    // Mapeador Model -> ResponseDTO (AQUÍ SE PASAN LOS NOMBRES)
    private ProductoResponseDTO toResponseDTO(Producto p) {
    ProductoResponseDTO dto = new ProductoResponseDTO();
    dto.setIdProducto(p.getIdProducto());
    dto.setIdCategoria(p.getIdCategoria());
    dto.setIdMarca(p.getIdMarca()); // ← CORREGIDO (setIdMarca)
    dto.setNombre(p.getNombre());
    dto.setDescripcion(p.getDescripcion());
    dto.setPrecio(p.getPrecio());
    dto.setImagenUrl(p.getImagen_url());
    dto.setStock(p.getStock());
    dto.setDisponible(p.getDisponible());

    // Nombres traídos desde el repositorio
    dto.setNombreCategoria(p.getNombreCategoria());
    dto.setNombreMarca(p.getNombreMarca());

    return dto;
}
    
}