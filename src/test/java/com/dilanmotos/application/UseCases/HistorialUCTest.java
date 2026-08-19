package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.Historial;
import com.dilanmotos.domain.repository.HistorialRepository;
import com.dilanmotos.infrastructure.dto.HistorialRequestDTO;
import com.dilanmotos.infrastructure.dto.HistorialResponseDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistorialUCTest {

    private HistorialUC historialUC;

    @Mock
    private HistorialRepository historialRepository;

    @BeforeEach
    void setUp() {
        historialUC = new HistorialUC(historialRepository);
    }

    // CREAR HISTORIAL
    @Test
    @DisplayName("Debe crear un historial correctamente")
    void debeCrearHistorialCorrectamente() {

        // Arrange
        HistorialRequestDTO request = new HistorialRequestDTO();

        request.setIdUsuario(1);
        request.setIdServicio(10);
        request.setAccion("SERVICIO_CREADO");
        request.setFecha(Date.valueOf("2026-08-19"));
        request.setDetalle("Se creó un nuevo servicio");

        Historial historialGuardado = new Historial();

        historialGuardado.setIdHistorial(1);
        historialGuardado.setIdUsuario(1);
        historialGuardado.setIdServicio(10);
        historialGuardado.setAccion("SERVICIO_CREADO");
        historialGuardado.setFecha(Date.valueOf("2026-08-19"));
        historialGuardado.setDetalle("Se creó un nuevo servicio");

        when(historialRepository.guardar(any(Historial.class)))
                .thenReturn(historialGuardado);

        // Act
        HistorialResponseDTO resultado =
                historialUC.crear(request);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getIdHistorial());
        assertEquals(1, resultado.getIdUsuario());
        assertEquals(10, resultado.getIdServicio());
        assertEquals("SERVICIO_CREADO",resultado.getAccion());
        assertEquals(Date.valueOf("2026-08-19"),resultado.getFecha());
        assertEquals( "Se creó un nuevo servicio",resultado.getDetalle());

        verify(historialRepository, times(1))
                .guardar(any(Historial.class));
    }

    // LISTAR TODOS LOS HISTORIALES
    @Test
    @DisplayName("Debe listar todos los historiales correctamente")
    void debeListarTodosLosHistorialesCorrectamente() {

        // Arrange
        Historial historial1 = new Historial();

        historial1.setIdHistorial(1);
        historial1.setIdUsuario(1);
        historial1.setIdServicio(10);
        historial1.setAccion("SERVICIO_CREADO");
        historial1.setFecha(Date.valueOf("2026-08-19"));
        historial1.setDetalle("Servicio creado");

        Historial historial2 = new Historial();

        historial2.setIdHistorial(2);
        historial2.setIdUsuario(2);
        historial2.setIdServicio(20);
        historial2.setAccion("SERVICIO_ACTUALIZADO");
        historial2.setFecha(Date.valueOf("2026-08-19"));
        historial2.setDetalle("Servicio actualizado");

        List<Historial> historiales = List.of(
                historial1,
                historial2
        );

        when(historialRepository.obtenerTodas())
                .thenReturn(historiales);

        // Act
        List<HistorialResponseDTO> resultado =
                historialUC.listarTodas();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(1,resultado.get(0).getIdHistorial());
        assertEquals("SERVICIO_CREADO", resultado.get(0).getAccion());
        assertEquals(2,resultado.get(1).getIdHistorial());
        assertEquals("SERVICIO_ACTUALIZADO",resultado.get(1).getAccion());

        verify(historialRepository, times(1))
                .obtenerTodas();
    }

    // OBTENER HISTORIAL POR ID
    @Test
    @DisplayName("Debe obtener un historial por ID correctamente")
    void debeObtenerHistorialPorIdCorrectamente() {

        // Arrange
        Integer id = 1;
        Historial historial = new Historial();

        historial.setIdHistorial(id);
        historial.setIdUsuario(1);
        historial.setIdServicio(10);
        historial.setAccion("SERVICIO_CREADO");
        historial.setFecha(Date.valueOf("2026-08-19"));
        historial.setDetalle("Se creó el servicio");

        when(historialRepository.buscarPorId(id))
                .thenReturn(Optional.of(historial));

        // Act
        HistorialResponseDTO resultado =
                historialUC.obtenerPorId(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(id,resultado.getIdHistorial());
        assertEquals(1,resultado.getIdUsuario());
        assertEquals(10,resultado.getIdServicio());
        assertEquals("SERVICIO_CREADO",resultado.getAccion());
        assertEquals("Se creó el servicio",resultado.getDetalle());

        verify(historialRepository, times(1))
                .buscarPorId(id);
    }

    // ACTUALIZAR HISTORIAL
    @Test
    @DisplayName("Debe actualizar un historial correctamente")
    void debeActualizarHistorialCorrectamente() {

        // Arrange
        Integer id = 1;
        Historial historialExistente = new Historial();

        historialExistente.setIdHistorial(id);
        historialExistente.setIdUsuario(1);
        historialExistente.setIdServicio(10);
        historialExistente.setAccion("SERVICIO_CREADO");
        historialExistente.setFecha(
                Date.valueOf("2026-08-18")
        );
        historialExistente.setDetalle("Servicio creado");

        HistorialRequestDTO request =
                new HistorialRequestDTO();

        request.setIdUsuario(1);
        request.setIdServicio(10);
        request.setAccion("SERVICIO_ACTUALIZADO");
        request.setFecha(
                Date.valueOf("2026-08-19")
        );
        request.setDetalle("Servicio actualizado");


        when(historialRepository.buscarPorId(id))
                .thenReturn(Optional.of(historialExistente));

        when(historialRepository.actualizar(
                any(Historial.class)
        )).thenAnswer(invocation -> {

            Historial historial =
                    invocation.getArgument(0);

            return historial;
        });

        // Act
        HistorialResponseDTO resultado =
                historialUC.actualizar(id, request);

        // Assert
        assertNotNull(resultado);
        assertEquals(id,resultado.getIdHistorial());
        assertEquals(1,resultado.getIdUsuario());
        assertEquals(10,resultado.getIdServicio());
        assertEquals("SERVICIO_ACTUALIZADO",resultado.getAccion());
        assertEquals(Date.valueOf("2026-08-19"),resultado.getFecha());
        assertEquals("Servicio actualizado",resultado.getDetalle());

        verify(historialRepository, times(1))
                .buscarPorId(id);

        verify(historialRepository, times(1))
                .actualizar(any(Historial.class));
    }

    // ELIMINAR HISTORIAL
    @Test
    @DisplayName("Debe eliminar un historial correctamente")
    void debeEliminarHistorialCorrectamente() {

        // Arrange
        Integer id = 1;
        Historial historial = new Historial();

        historial.setIdHistorial(id);

        when(historialRepository.buscarPorId(id))
                .thenReturn(Optional.of(historial));

        doNothing()
                .when(historialRepository)
                .eliminar(id);

        // Act
        historialUC.eliminar(id);

        // Assert
        verify(historialRepository, times(1))
                .buscarPorId(id);

        verify(historialRepository, times(1))
                .eliminar(id);
    }
}