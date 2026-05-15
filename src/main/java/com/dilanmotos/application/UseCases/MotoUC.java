package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.Moto;
import com.dilanmotos.domain.repository.MotoRepository;
import com.dilanmotos.domain.repository.MarcaRepository; // Necesario para el nombre
import com.dilanmotos.domain.exception.MotoNotFoundException;
import com.dilanmotos.infrastructure.dto.MotoRequestDTO;
import com.dilanmotos.infrastructure.dto.MotoResponseDTO;
import com.dilanmotos.infrastructure.dto.MarcaResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MotoUC {

    private final MotoRepository motoRepository;
    private final MarcaRepository marcaRepository;

    public MotoUC(MotoRepository motoRepository, MarcaRepository marcaRepository) {
        this.motoRepository = motoRepository;
        this.marcaRepository = marcaRepository;
    }

    public List<MotoResponseDTO> listarTodas() {
        return motoRepository.obtenerTodas().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public MotoResponseDTO crear(MotoRequestDTO request) {
        Moto moto = mapToModel(request);
        return mapToDTO(motoRepository.guardar(moto));
    }

    public MotoResponseDTO obtenerPorId(Integer id) {
        return motoRepository.buscarPorId(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new MotoNotFoundException("Moto no encontrada: " + id));
    }

    public MotoResponseDTO actualizar(Integer id, MotoRequestDTO request) {
        motoRepository.buscarPorId(id)
                .orElseThrow(() -> new MotoNotFoundException("No existe la moto: " + id));

        Moto moto = mapToModel(request);
        moto.setIdMoto(id); // VITAL para que el repositorio sepa qué ID actualizar
        return mapToDTO(motoRepository.actualizar(moto));
    }

    public void eliminar(Integer id) {
        motoRepository.eliminar(id);
    }

    private Moto mapToModel(MotoRequestDTO dto) {
        Moto m = new Moto();
        m.setIdUsuario(dto.getIdUsuario());
        m.setIdMarca(dto.getIdMarca());
        m.setModelo(dto.getModelo());
        m.setCilindraje(dto.getCilindraje());
        return m;
    }

    private MotoResponseDTO mapToDTO(Moto m) {
        MotoResponseDTO dto = new MotoResponseDTO();
        dto.setIdMoto(m.getIdMoto());
        dto.setIdUsuario(m.getIdUsuario());
        dto.setIdMarca(m.getIdMarca());
        dto.setModelo(m.getModelo());
        dto.setCilindraje(m.getCilindraje());

        // Llenamos el objeto marca dentro del DTO para el Frontend
        marcaRepository.buscarPorId(m.getIdMarca()).ifPresent(marca -> {
            MarcaResponseDTO marcaDTO = new MarcaResponseDTO();
            marcaDTO.setIdMarca(marca.getIdMarca());
            marcaDTO.setNombre(marca.getNombre());
            dto.setMarca(marcaDTO);
        });

        return dto;
    }
}