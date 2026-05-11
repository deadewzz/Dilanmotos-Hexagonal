package com.dilanmotos.application.UseCases;

import com.dilanmotos.infrastructure.dto.ProductoRequestDTO;
import com.dilanmotos.infrastructure.dto.ProductoResponseDTO;
import com.dilanmotos.domain.model.Producto;
import com.dilanmotos.domain.repository.ProductoRepository;
import com.dilanmotos.domain.exception.ProductoNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoUC {

    private final ProductoRepository productoRepository;

    public ProductoUC(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public ProductoResponseDTO crear(ProductoRequestDTO request) {
        Producto producto = mapToModel(request);
        return mapToDTO(productoRepository.guardar(producto));
    }

    public List<ProductoResponseDTO> listarTodos() {
        return productoRepository.obtenerTodos().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public ProductoResponseDTO actualizar(Integer id, ProductoRequestDTO request) {
        productoRepository.buscarPorId(id)
                .orElseThrow(() -> new ProductoNotFoundException("No existe el producto con ID: " + id));

        Producto producto = mapToModel(request);
        return mapToDTO(productoRepository.actualizar(id, producto));
    }

    public void eliminar(Integer id) {
        productoRepository.buscarPorId(id)
                .orElseThrow(() -> new ProductoNotFoundException("No se puede eliminar, ID no encontrado: " + id));
        productoRepository.eliminar(id);
    }

    private Producto mapToModel(ProductoRequestDTO dto) {
        Producto p = new Producto();
        p.setIdCategoria(dto.getIdCategoria());
        p.setIdMarca(dto.getIdMarca());
        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setPrecio(dto.getPrecio());
        return p;
    }

    private ProductoResponseDTO mapToDTO(Producto p) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setIdProducto(p.getIdProducto());
        dto.setNombre(p.getNombre());
        dto.setPrecio(p.getPrecio());
        dto.setDescripcion(p.getDescripcion());
        return dto;
    }
}