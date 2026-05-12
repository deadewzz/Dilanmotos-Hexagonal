package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.Moto;
import com.dilanmotos.domain.repository.MotoRepository;
import com.dilanmotos.domain.exception.MotoNotFoundException;
import com.dilanmotos.infrastructure.dto.MotoRequestDTO;
import com.dilanmotos.infrastructure.dto.MotoResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MotoUC {

    private final MotoRepository motoRepository;

    public MotoUC(MotoRepository motoRepository) {
        this.motoRepository = motoRepository;
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
                .orElseThrow(() -> new MotoNotFoundException("Moto no encontrada con ID: " + id));
    }

    public MotoResponseDTO actualizar(Integer id, MotoRequestDTO request) {
        motoRepository.buscarPorId(id)
                .orElseThrow(() -> new MotoNotFoundException("No se puede actualizar, moto no existe: " + id));

        Moto moto = mapToModel(request);
        moto.setIdMoto(id);
        return mapToDTO(motoRepository.actualizar(moto));
    }

    public void eliminar(Integer id) {
        motoRepository.buscarPorId(id)
                .orElseThrow(() -> new MotoNotFoundException("No se puede eliminar, moto no encontrada: " + id));
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
        return dto;
    }
}
