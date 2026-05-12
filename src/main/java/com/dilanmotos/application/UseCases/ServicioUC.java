package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.Servicio;
import com.dilanmotos.domain.repository.ServicioRepository;
import com.dilanmotos.domain.exception.ServicioNotFoundException;
import com.dilanmotos.infrastructure.dto.ServicioRequestDTO;
import com.dilanmotos.infrastructure.dto.ServicioResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicioUC {

    private final ServicioRepository servicioRepository;

    public ServicioUC(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    public List<ServicioResponseDTO> listarTodas() {
        return servicioRepository.obtenerTodas().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ServicioResponseDTO crear(ServicioRequestDTO request) {
        Servicio servicio = mapToModel(request);
        return mapToDTO(servicioRepository.guardar(servicio));
    }

    public ServicioResponseDTO obtenerPorId(Integer id) {
        return servicioRepository.buscarPorId(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ServicioNotFoundException("Servicio no encontrado con ID: " + id));
    }

    public ServicioResponseDTO actualizar(Integer id, ServicioRequestDTO request) {
        servicioRepository.buscarPorId(id)
                .orElseThrow(() -> new ServicioNotFoundException("No se puede actualizar, servicio no existe: " + id));

        Servicio servicio = mapToModel(request);
        servicio.setIdServicio(id);
        return mapToDTO(servicioRepository.actualizar(servicio));
    }

    public void eliminar(Integer id) {
        servicioRepository.buscarPorId(id)
                .orElseThrow(() -> new ServicioNotFoundException("No se puede eliminar, servicio no encontrado: " + id));
        servicioRepository.eliminar(id);
    }

    private Servicio mapToModel(ServicioRequestDTO dto) {
        Servicio s = new Servicio();
        s.setIdUsuario(dto.getIdUsuario());
        s.setIdMecanico(dto.getIdMecanico());
        s.setIdTipoDeServicio(dto.getIdTipoDeServicio());
        s.setFechaServicio(dto.getFechaServicio());
        s.setEstadoServicio(dto.getEstadoServicio());
        s.setComentario(dto.getComentario());
        s.setPuntuacion(dto.getPuntuacion());
        s.setVisibleEnHistorial(dto.getVisibleEnHistorial());
        return s;
    }

    private ServicioResponseDTO mapToDTO(Servicio s) {
        ServicioResponseDTO dto = new ServicioResponseDTO();
        dto.setIdServicio(s.getIdServicio());
        dto.setIdMecanico(s.getIdMecanico());
        dto.setIdTipoDeServicio(s.getIdTipoDeServicio());
        dto.setFechaServicio(s.getFechaServicio());
        dto.setEstadoServicio(s.getEstadoServicio());
        dto.setComentario(s.getComentario());
        dto.setPuntuacion(s.getPuntuacion());
        dto.setVisibleEnHistorial(s.getVisibleEnHistorial());
        return dto;
    }
}
