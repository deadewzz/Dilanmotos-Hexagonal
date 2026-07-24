package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.MarcaProducto;
import com.dilanmotos.domain.repository.MarcaProductoRepository;
import com.dilanmotos.domain.exception.MarcaProductoNotFoundException;
import com.dilanmotos.infrastructure.dto.MarcaProductoRequestDTO;
import com.dilanmotos.infrastructure.dto.MarcaProductoResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MarcaProductoUC {

    private final MarcaProductoRepository marcaProductoRepository;

    public MarcaProductoUC(MarcaProductoRepository marcaProductoRepository) {
        this.marcaProductoRepository = marcaProductoRepository;
    }

    public List<MarcaProductoResponseDTO> listarTodas() {
        return marcaProductoRepository.obtenerTodos().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<MarcaProductoResponseDTO> listarPorCategoria(Integer idCategoria) {
        return marcaProductoRepository.obtenerPorCategoria(idCategoria).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public MarcaProductoResponseDTO crear(MarcaProductoRequestDTO request) {
        MarcaProducto marcaProducto = mapToModel(request);
        return mapToDTO(marcaProductoRepository.guardar(marcaProducto));
    }

    public MarcaProductoResponseDTO obtenerPorId(Integer id) {
        return marcaProductoRepository.buscarPorId(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new MarcaProductoNotFoundException("Marca de producto no encontrada con ID: " + id));
    }

    public MarcaProductoResponseDTO actualizar(Integer id, MarcaProductoRequestDTO request) {
        marcaProductoRepository.buscarPorId(id)
                .orElseThrow(() -> new MarcaProductoNotFoundException("No se puede actualizar, marca de producto no existe: " + id));

        MarcaProducto marcaProducto = mapToModel(request);
        marcaProducto.setIdMarcaProducto(id);
        return mapToDTO(marcaProductoRepository.actualizar(marcaProducto));
    }

    public void eliminar(Integer id) {
        marcaProductoRepository.buscarPorId(id)
                .orElseThrow(() -> new MarcaProductoNotFoundException("No se puede eliminar, marca de producto no encontrada: " + id));
        marcaProductoRepository.eliminar(id);
    }

    private MarcaProducto mapToModel(MarcaProductoRequestDTO dto) {
        MarcaProducto m = new MarcaProducto();
        m.setIdMarcaProducto(dto.getIdMarcaProducto());
        m.setNombre(dto.getNombre());
        m.setIdCategoria(dto.getIdCategoria());
        return m;
    }

    private MarcaProductoResponseDTO mapToDTO(MarcaProducto m) {
        MarcaProductoResponseDTO dto = new MarcaProductoResponseDTO();
        dto.setIdMarcaProducto(m.getIdMarcaProducto());
        dto.setNombre(m.getNombre());
        dto.setIdCategoria(m.getIdCategoria());
        return dto;
    }

}