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
        Producto producto = new Producto();
        producto.setIdCategoria(request.getIdCategoria());
        producto.setIdMarca(request.getIdMarca());
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());

        Producto guardado = productoRepository.guardar(producto);
        return mapToDTO(guardado);
    }

    public List<ProductoResponseDTO> listarTodos() {
        return productoRepository.obtenerTodos()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
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