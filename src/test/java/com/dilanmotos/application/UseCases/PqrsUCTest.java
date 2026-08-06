package com.dilanmotos.application.UseCases;

import java.util.*;
import com.dilanmotos.domain.model.PQRS;
import com.dilanmotos.infrastructure.dto.PqrsRequestDTO;
import com.dilanmotos.infrastructure.dto.PqrsResponseDTO;
import com.dilanmotos.infrastructure.dto.PqrsUpdateDTO;
import com.dilanmotos.domain.exception.PqrsNotFoundException;
import com.dilanmotos.domain.repository.PqrsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PqrsUCTest {
    
    private PqrsUC pqrsUC;

    @Mock
    private PqrsRepository pqrsRepository;

    @BeforeEach
    void setUp() {
        pqrsUC = new PqrsUC(pqrsRepository);
    }

    @Test
    @DisplayName("Debe crear un PQRS correctamente")
    void debeCrearPqrsCorrectamente() {
        // Arrange
        PqrsRequestDTO request = new PqrsRequestDTO();
        request.setIdUsuario(1);
        request.setTipo("Queja");
        request.setAsunto("Problema con el producto");
        request.setDescripcion("El producto llegó dañado.");

        PQRS pqrsSimulado = new PQRS();
        pqrsSimulado.setId_usuario(request.getIdUsuario());

        when(pqrsRepository.guardar(any(PQRS.class))).thenReturn(pqrsSimulado);

        // Act
        PqrsResponseDTO resultado = pqrsUC.crear(request);

        // Assert
        assertNotNull(resultado);
        verify(pqrsRepository, times(1)).guardar(any(PQRS.class));
    }

    @Test
    @DisplayName("Debe listar todos los PQRS correctamente")
    void debeListarTodosPqrsCorrectamente() {
        // Arrange
        PQRS pqrs1 = new PQRS();
        PQRS pqrs2 = new PQRS();
        List<PQRS> pqrsList = List.of(pqrs1, pqrs2);

        when(pqrsRepository.obtenerTodos()).thenReturn(pqrsList);

        // Act
        List<PqrsResponseDTO> resultado = pqrsUC.listarTodas();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(pqrsRepository, times(1)).obtenerTodos();
    }

    @Test
    @DisplayName("Debe obtener un PQRS por ID correctamente")
    void debeObtenerPqrsPorIdCorrectamente() {
        // Arrange
        int id = 1;
        PQRS pqrsSimulado = new PQRS();
        pqrsSimulado.setId_pqrs(id);

        when(pqrsRepository.buscarPorId(id)).thenReturn(Optional.of(pqrsSimulado));

        // Act
        PqrsResponseDTO resultado = pqrsUC.obtenerPorId(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(id, resultado.getId_pqrs());
        verify(pqrsRepository, times(1)).buscarPorId(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción al obtener un PQRS por ID inexistente")
    void debeLanzarExcepcionAlObtenerPqrsPorIdInexistente() {
        // Arrange
        int idInexistente = 999;
        when(pqrsRepository.buscarPorId(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(PqrsNotFoundException.class, () -> {
            pqrsUC.obtenerPorId(idInexistente);
        });

        String mensajeEsperado = "PQRS no encontrada: " + idInexistente;
        String mensajeActual = exception.getMessage();

        assertTrue(mensajeActual.contains(mensajeEsperado));
        verify(pqrsRepository, times(1)).buscarPorId(idInexistente);
    }

    @Test
    @DisplayName("Debe actualizar un PQRS correctamente")
    void debeActualizarPqrsCorrectamente() {
        // Arrange
        int id = 1;
        PqrsRequestDTO request = new PqrsRequestDTO();
        request.setEstado("RESUELTO");
        request.setRespuesta_admin("La queja ha sido resuelta.");

        PQRS pqrsExistente = new PQRS();
        pqrsExistente.setId_pqrs(id);
        pqrsExistente.setEstado("PENDIENTE");

        when(pqrsRepository.buscarPorId(id)).thenReturn(Optional.of(pqrsExistente));
        when(pqrsRepository.guardar(pqrsExistente)).thenReturn(pqrsExistente);

        // Act
        PqrsResponseDTO resultado = pqrsUC.actualizar(id, request);

        // Assert
        assertNotNull(resultado);
        assertEquals("RESUELTO", resultado.getEstado());
        verify(pqrsRepository, times(1)).buscarPorId(id);
        verify(pqrsRepository, times(1)).guardar(pqrsExistente);
    }

    @Test
@DisplayName("Debe eliminar un PQRS correctamente")
void debeEliminarPqrsCorrectamente() {
    // Arrange
    int id = 1;
    doNothing().when(pqrsRepository).eliminarPorId(id);

    // Act
    pqrsUC.eliminar(id);

    // Assert
    verify(pqrsRepository, times(1)).eliminarPorId(id);
}
        @Test
    @DisplayName("Debe actualizar un PQRS correctamente solo para admin")
    void debeBuscarActualizarAdmin()
    {
        // Arrange 
        int id = 1;
        PqrsUpdateDTO updateDTO = new PqrsUpdateDTO();
        updateDTO.setEstado("RESUELTO");
        updateDTO.setRespuesta_admin("La queja ha sido resuelta.");

        PQRS pqrsExistente = new PQRS();
        pqrsExistente.setId_pqrs(id);
        pqrsExistente.setEstado("PENDIENTE");

        when(pqrsRepository.buscarPorId(id)).thenReturn(Optional.of(pqrsExistente));
        when(pqrsRepository.guardar(pqrsExistente)).thenReturn(pqrsExistente);

        // Act
        PqrsResponseDTO resultado = pqrsUC.actualizarAdmin(id, updateDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals("RESUELTO", resultado.getEstado());
        assertEquals("La queja ha sido resuelta.", resultado.getRespuesta_admin());
        verify(pqrsRepository, times(1)).buscarPorId(id);
        verify(pqrsRepository, times(1)).guardar(pqrsExistente);
    }

    @Test
    @DisplayName("Debe buscar un PQRS por ID")
    void BuscarPQRSPorId()
    {
        // Arrange
        int id = 1;
        PQRS pqrsExistente = new PQRS();
        pqrsExistente.setId_pqrs(id);
        pqrsExistente.setEstado("PENDIENTE");

        when(pqrsRepository.buscarPorId(id)).thenReturn(Optional.of(pqrsExistente));

        // Act
        PqrsResponseDTO resultado = pqrsUC.obtenerPorId(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(id, resultado.getId_pqrs());
        verify(pqrsRepository, times(1)).buscarPorId(id);
    }
    
}