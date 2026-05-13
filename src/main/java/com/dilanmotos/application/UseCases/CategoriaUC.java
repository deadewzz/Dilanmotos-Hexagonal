package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.Categoria;
import com.dilanmotos.domain.repository.CategoriaRepository;
import com.dilanmotos.domain.exception.CategoriaNotFoundException;
import com.dilanmotos.infrastructure.dto.CategoriaRequestDTO;
import com.dilanmotos.infrastructure.dto.CategoriaResponseDTO;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaUC {

    private final CategoriaRepository categoriaRepository;

    public CategoriaUC(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<CategoriaResponseDTO> listarTodas() {
        return categoriaRepository.obtenerTodas().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CategoriaResponseDTO crear(CategoriaRequestDTO request) {
        Categoria categoria = mapToModel(request);
        return mapToDTO(categoriaRepository.guardar(categoria));
    }

    public CategoriaResponseDTO obtenerPorId(Integer id) {
        return categoriaRepository.buscarPorId(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new CategoriaNotFoundException("Categoria no encontrada con ID: " + id));
    }

    public CategoriaResponseDTO actualizar(Integer id, CategoriaRequestDTO request) {
        categoriaRepository.buscarPorId(id)
                .orElseThrow(() -> new CategoriaNotFoundException("No se puede actualizar, categoria no existe: " + id));

        Categoria categoria = mapToModel(request);
        categoria.setIdCategoria(id);
        return mapToDTO(categoriaRepository.actualizar(categoria));
    }

    public void eliminar(Integer id) {
        categoriaRepository.buscarPorId(id)
                .orElseThrow(() -> new CategoriaNotFoundException("No se puede eliminar, categoria no encontrada: " + id));
        categoriaRepository.eliminar(id);
    }

    private Categoria mapToModel(CategoriaRequestDTO dto) {
        Categoria c = new Categoria();
        c.setIdCategoria(dto.getIdCategoria());
        c.setNombre(dto.getNombre());
        return c;
    }
    
    private CategoriaResponseDTO mapToDTO(Categoria m) {
        CategoriaResponseDTO dto = new CategoriaResponseDTO();
        dto.setIdCategoria(m.getIdCategoria());
        dto.setNombre(m.getNombre());
        return dto;
    }
    
}
