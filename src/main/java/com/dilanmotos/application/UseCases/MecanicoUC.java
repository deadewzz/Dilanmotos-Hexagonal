package com.dilanmotos.application.UseCases;

import com.dilanmotos.infrastructure.dto.MecanicoRequestDTO;
import com.dilanmotos.infrastructure.dto.MecanicoResponseDTO;
import com.dilanmotos.domain.model.Mecanico;
import com.dilanmotos.domain.repository.MecanicoRepository;
import com.dilanmotos.domain.exception.MecanicoNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MecanicoUC {

    private final MecanicoRepository mecanicoRepository;

    public MecanicoUC(MecanicoRepository mecanicoRepository) {
        this.mecanicoRepository = mecanicoRepository;
    }

    public MecanicoResponseDTO crear(MecanicoRequestDTO request) {
        Mecanico mecanico = mapToModel(request);
        return mapToDTO(mecanicoRepository.guardar(mecanico));
    }

    public List<MecanicoResponseDTO> listarTodos() {
        return mecanicoRepository.obtenerTodos().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public MecanicoResponseDTO obtenerPorId(Integer id) {
        return mapToDTO(mecanicoRepository.buscarPorId(id)
                .orElseThrow(() -> new MecanicoNotFoundException("No existe el mecánico con ID: " + id)));
    }

    public MecanicoResponseDTO actualizar(Integer id, MecanicoRequestDTO request) {
        mecanicoRepository.buscarPorId(id)
                .orElseThrow(() -> new MecanicoNotFoundException("No existe el mecánico con ID: " + id));

        Mecanico mecanico = mapToModel(request);
        return mapToDTO(mecanicoRepository.actualizar(mecanico));
    }

    public void eliminar(Integer id) {
        mecanicoRepository.buscarPorId(id)
                .orElseThrow(() -> new MecanicoNotFoundException("No se puede eliminar, ID no encontrado: " + id));
        mecanicoRepository.eliminar(id);
    }

    private Mecanico mapToModel(MecanicoRequestDTO dto) {
        Mecanico m = new Mecanico();
        m.setIdMecanico(dto.getIdMecanico());
        m.setNombre(dto.getNombre());
        m.setEspecialidad(dto.getEspecialidad());
        m.setTelefono(dto.getTelefono());
        return m;
    }

    private MecanicoResponseDTO mapToDTO(Mecanico m) {
        MecanicoResponseDTO dto = new MecanicoResponseDTO();
        dto.setIdMecanico(m.getIdMecanico());
        dto.setNombre(m.getNombre());
        dto.setEspecialidad(m.getEspecialidad());
        dto.setTelefono(m.getTelefono());
        return dto;
    }
}