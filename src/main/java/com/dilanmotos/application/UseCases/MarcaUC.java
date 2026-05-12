package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.Marca;
import com.dilanmotos.domain.repository.MarcaRepository;
import com.dilanmotos.domain.exception.MarcaNotFoundException;
import com.dilanmotos.infrastructure.dto.MarcaRequestDTO;
import com.dilanmotos.infrastructure.dto.MarcaResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MarcaUC {

    private final MarcaRepository marcaRepository;

    public MarcaUC(MarcaRepository marcaRepository) {
        this.marcaRepository = marcaRepository;
    }

    public List<MarcaResponseDTO> listarTodas() {
        return marcaRepository.obtenerTodos().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public MarcaResponseDTO crear(MarcaRequestDTO request) {
        Marca marca = mapToModel(request);
        return mapToDTO(marcaRepository.guardar(marca));
    }

    public MarcaResponseDTO obtenerPorId(Integer id) {
        return marcaRepository.buscarPorId(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new MarcaNotFoundException("Marca no encontrada con ID: " + id));
    }

    public MarcaResponseDTO actualizar(Integer id, MarcaRequestDTO request) {
        marcaRepository.buscarPorId(id)
                .orElseThrow(() -> new MarcaNotFoundException("No se puede actualizar, marca no existe: " + id));

        Marca marca = mapToModel(request);
        marca.setIdMarca(id);
        return mapToDTO(marcaRepository.actualizar(marca));
    }

    public void eliminar(Integer id) {
        marcaRepository.buscarPorId(id)
                .orElseThrow(() -> new MarcaNotFoundException("No se puede eliminar, marca no encontrada: " + id));
        marcaRepository.eliminar(id);
    }

    private Marca mapToModel(MarcaRequestDTO dto) {
        Marca m = new Marca();
        m.setIdMarca(dto.getIdMarca());
        m.setNombre(dto.getNombre());
        return m;
    }

    private MarcaResponseDTO mapToDTO(Marca m) {
        MarcaResponseDTO dto = new MarcaResponseDTO();
        dto.setIdMarca(m.getIdMarca());
        dto.setNombre(m.getNombre());
        return dto;
    }
    
}
