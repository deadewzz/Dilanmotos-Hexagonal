package com.dilanmotos.application.UseCases;

import com.dilanmotos.infrastructure.dto.CaracteristicaRequestDTO;
import com.dilanmotos.infrastructure.dto.CaracteristicaResponseDTO;
import com.dilanmotos.infrastructure.dto.MotoResponseDTO;
import com.dilanmotos.domain.model.Caracteristica;
import com.dilanmotos.domain.repository.CaracteristicaRepository;
import com.dilanmotos.domain.repository.MotoRepository;
import com.dilanmotos.domain.exception.CaracteristicaNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CaracteristicaUC {

    private final CaracteristicaRepository repository;
    private final MotoRepository motoRepository;

    public CaracteristicaUC(CaracteristicaRepository repository, MotoRepository motoRepository) {
        this.repository = repository;
        this.motoRepository = motoRepository;
    }

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

    public CaracteristicaResponseDTO actualizar(Integer id, CaracteristicaRequestDTO request) {
        repository.buscarPorId(id)
                .orElseThrow(() -> new CaracteristicaNotFoundException("ID no encontrado: " + id));

        Caracteristica c = new Caracteristica();
        c.setIdCaracteristica(id);
        c.setIdMoto(request.getIdMoto());
        c.setDescripcion(request.getDescripcion());
        return mapToDTO(repository.guardar(c));
    }

    public CaracteristicaResponseDTO obtenerPorId(Integer id) {
        return repository.buscarPorId(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new CaracteristicaNotFoundException("No encontrado: " + id));
    }

    public List<CaracteristicaResponseDTO> listarPorMoto(Integer idMoto) {
        return repository.obtenerPorMoto(idMoto).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void eliminar(Integer id) {
        repository.buscarPorId(id).orElseThrow(() -> new CaracteristicaNotFoundException("No encontrado"));
        repository.eliminar(id);
    }

    private CaracteristicaResponseDTO mapToDTO(Caracteristica c) {
        CaracteristicaResponseDTO dto = new CaracteristicaResponseDTO();
        dto.setIdCaracteristica(c.getIdCaracteristica());
        dto.setIdMoto(c.getIdMoto());
        dto.setDescripcion(c.getDescripcion());

        // BUSCAR LA MOTO PARA QUITAR EL N/A
        motoRepository.buscarPorId(c.getIdMoto()).ifPresent(m -> {
            MotoResponseDTO motoDTO = new MotoResponseDTO();
            motoDTO.setIdMoto(m.getIdMoto());
            motoDTO.setModelo(m.getModelo());
            dto.setMoto(motoDTO);
        });

        return dto;
    }
}