package com.dilanmotos.application.UseCases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dilanmotos.domain.model.Mecanico;
import com.dilanmotos.domain.repository.MecanicoRepository;
import com.dilanmotos.infrastructure.dto.MecanicoRequestDTO;
import com.dilanmotos.infrastructure.dto.MecanicoResponseDTO;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MecanicoUCTest {

    @Mock
    private MecanicoRepository mecanicoRepository;

    @InjectMocks
    private MecanicoUC mecanicoUC;

    @Test
    @DisplayName("Debe obtener un mecánico por ID correctamente")
    public void obtenerPorId_CuandoMecanicoExiste_DebeRetornarMecanico() {
        // Arrange
        int idMecanico = 1;
        Mecanico mecanicoSimulado = new Mecanico();
        mecanicoSimulado.setIdMecanico(idMecanico);
        when(mecanicoRepository.buscarPorId(idMecanico)).thenReturn(Optional.of(mecanicoSimulado));

        // Act
        MecanicoResponseDTO resultado = mecanicoUC.obtenerPorId(idMecanico);

        // Assert
        assertNotNull(resultado);
        verify(mecanicoRepository, times(1)).buscarPorId(idMecanico);
    }

    @Test
    @DisplayName("Debe listar todos los mecánicos correctamente")
    public void listar_CuandoExistenMecanicos_DebeRetornarListaDTO() {
        // Arrange
        Mecanico m1 = new Mecanico();
        m1.setIdMecanico(1);
        Mecanico m2 = new Mecanico();
        m2.setIdMecanico(2);

        when(mecanicoRepository.obtenerTodos()).thenReturn(List.of(m1, m2));

        // Act
        List<MecanicoResponseDTO> resultado = mecanicoUC.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(mecanicoRepository, times(1)).obtenerTodos();
    }

    @Test
    @DisplayName("Debe crear un mecánico correctamente")
    public void crear_CuandoDatosValidos_DebeGuardarYRetornarDTO() {
        // Arrange
        MecanicoRequestDTO requestDTO = new MecanicoRequestDTO();
        Mecanico mecanicoGuardado = new Mecanico();
        mecanicoGuardado.setIdMecanico(1);

        when(mecanicoRepository.guardar(any(Mecanico.class))).thenReturn(mecanicoGuardado);

        // Act
        MecanicoResponseDTO resultado = mecanicoUC.crear(requestDTO);

        // Assert
        assertNotNull(resultado);
        verify(mecanicoRepository, times(1)).guardar(any(Mecanico.class));
    }

    @Test
    @DisplayName("Debe editar/actualizar un mecánico existente correctamente")
    public void editar_CuandoMecanicoExiste_DebeActualizarYRetornarDTO() {
        // Arrange
        int idMecanico = 1;
        MecanicoRequestDTO requestDTO = new MecanicoRequestDTO();
        Mecanico mecanicoExistente = new Mecanico();
        mecanicoExistente.setIdMecanico(idMecanico);

        when(mecanicoRepository.buscarPorId(idMecanico)).thenReturn(Optional.of(mecanicoExistente));
        when(mecanicoRepository.guardar(any(Mecanico.class))).thenReturn(mecanicoExistente);

        // Act
        MecanicoResponseDTO resultado = mecanicoUC.actualizar(idMecanico, requestDTO);

        // Assert
        assertNotNull(resultado);
        verify(mecanicoRepository, times(1)).buscarPorId(idMecanico);
        verify(mecanicoRepository, times(1)).guardar(any(Mecanico.class));
    }

    @Test
    @DisplayName("Debe eliminar un mecánico correctamente")
    public void borrar_CuandoIdExiste_DebeLlamarRepositoryEliminar() {
        // Arrange
        int idMecanico = 1;
        Mecanico mecanicoSimulado = new Mecanico();
        mecanicoSimulado.setIdMecanico(idMecanico);

        when(mecanicoRepository.buscarPorId(idMecanico)).thenReturn(Optional.of(mecanicoSimulado));
        doNothing().when(mecanicoRepository).eliminar(idMecanico);

        // Act
        mecanicoUC.eliminar(idMecanico);

        // Assert
        verify(mecanicoRepository, times(1)).eliminar(idMecanico);
    }
}