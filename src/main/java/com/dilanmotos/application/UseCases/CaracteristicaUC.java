package com.dilanmotos.application.UseCases;

import com.dilanmotos.infrastructure.dto.CaracteristicaRequestDTO;
import com.dilanmotos.infrastructure.dto.CaracteristicaResponseDTO;
import com.dilanmotos.domain.model.Caracteristica;
import com.dilanmotos.domain.repository.CaracteristicaRepository;
import com.dilanmotos.domain.exception.CaracteristicaNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CaracteristicaUC {

    private final CaracteristicaRepository repository;

    public CaracteristicaUC(CaracteristicaRepository repository) {
        this.repository = repository;
    }

    // LISTAR TODO (CRUD completo)
    public List<CaracteristicaResponseDTO> listarTodas() {
        return repository.obtenerTodas().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CaracteristicaResponseDTO agregar(CaracteristicaRequestDTO request) {
        Caracteristica c = new Caracteristica();
        c.setIdMoto(request.getIdMoto());
        c.setDescripcion(request.getDescripcion());
        return mapToDTO(repository.guardar(c));
    }

    public List<CaracteristicaResponseDTO> listarPorMoto(Integer idMoto) {
        return repository.obtenerPorMoto(idMoto).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CaracteristicaResponseDTO obtenerPorId(Integer id) {
        return repository.buscarPorId(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new CaracteristicaNotFoundException("Característica no encontrada con ID: " + id));
    }

    public void eliminar(Integer id) {
        repository.buscarPorId(id)
                .orElseThrow(
                        () -> new CaracteristicaNotFoundException("No se puede eliminar, ID no encontrado: " + id));
        repository.eliminar(id);
    }

    private CaracteristicaResponseDTO mapToDTO(Caracteristica c) {
        CaracteristicaResponseDTO dto = new CaracteristicaResponseDTO();
        dto.setIdCaracteristica(c.getIdCaracteristica());
        dto.setIdMoto(c.getIdMoto());
        dto.setDescripcion(c.getDescripcion());
        return dto;
    }
}