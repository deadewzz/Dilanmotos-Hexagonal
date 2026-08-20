package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.ReferenciaMoto;
import com.dilanmotos.domain.repository.ReferenciaMotoRepository;
import com.dilanmotos.infrastructure.dto.ReferenciaMotoRequestDTO;
import com.dilanmotos.infrastructure.dto.ReferenciaMotoResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReferenciaMotoUCTest {

    private ReferenciaMotoUC referenciaMotoUC;

    @Mock
    private ReferenciaMotoRepository referenciaMotoRepository;

    @BeforeEach
    void setUp() {
        referenciaMotoUC = new ReferenciaMotoUC(referenciaMotoRepository);
    }

    @Test
    @DisplayName("Debe guardar exitosamente una referencia recibiendo el DTO de solicitud")
    void testCrearReferenciaMoto() {
        // Arrange
        ReferenciaMotoRequestDTO requestDTO = new ReferenciaMotoRequestDTO();
        requestDTO.setNombre("FZ 25");
        requestDTO.setIdMarca(1);
        requestDTO.setCilindraje(249.0);

        ReferenciaMoto motoGuardada = new ReferenciaMoto();
        motoGuardada.setIdReferencia(10);
        motoGuardada.setNombre(requestDTO.getNombre());
        motoGuardada.setIdMarca(requestDTO.getIdMarca());
        motoGuardada.setCilindraje(requestDTO.getCilindraje());

        when(referenciaMotoRepository.guardar(any(ReferenciaMoto.class))).thenReturn(motoGuardada);

        // Act
        ReferenciaMotoResponseDTO response = referenciaMotoUC.crear(requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(10, response.getIdReferencia());
        assertEquals("FZ 25", response.getNombre());
        assertEquals(249.0, response.getCilindraje());

        verify(referenciaMotoRepository, times(1)).guardar(any(ReferenciaMoto.class));
    }

    @Test
    @DisplayName("Debe retornar la referencia cuando existe el ID buscado")
    void testObtenerPorIdExitoso() {
        // Arrange
        ReferenciaMoto moto = new ReferenciaMoto();
        moto.setIdReferencia(1);
        moto.setNombre("Gixxer 250");
        moto.setIdMarca(2);
        moto.setCilindraje(249.0);

        when(referenciaMotoRepository.buscarPorId(1)).thenReturn(Optional.of(moto));

        // Act
        ReferenciaMotoResponseDTO response = referenciaMotoUC.obtenerPorId(1);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getIdReferencia());
        assertEquals("Gixxer 250", response.getNombre());

        verify(referenciaMotoRepository, times(1)).buscarPorId(1);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el ID buscado no existe")
    void testObtenerPorIdNoEncontrado() {
        // Arrange
        when(referenciaMotoRepository.buscarPorId(99)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> referenciaMotoUC.obtenerPorId(99));

        assertTrue(exception.getMessage().contains("Referencia no encontrada con ID: 99"));
        verify(referenciaMotoRepository, times(1)).buscarPorId(99);
    }

    @Test
    @DisplayName("Debe eliminar una referencia si existe en la base de datos")
    void testEliminarExitoso() {
        // Arrange
        ReferenciaMoto moto = new ReferenciaMoto();
        moto.setIdReferencia(5);

        when(referenciaMotoRepository.buscarPorId(5)).thenReturn(Optional.of(moto));
        doNothing().when(referenciaMotoRepository).eliminarPorId(5);

        // Act
        referenciaMotoUC.eliminar(5);

        // Assert
        verify(referenciaMotoRepository, times(1)).buscarPorId(5);
        verify(referenciaMotoRepository, times(1)).eliminarPorId(5);
    }
}