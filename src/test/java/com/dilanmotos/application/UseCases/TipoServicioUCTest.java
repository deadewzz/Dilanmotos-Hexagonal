package com.dilanmotos.application.UseCases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dilanmotos.domain.exception.TipoServicioNotFoundException;
import com.dilanmotos.domain.model.TipoServicio;
import com.dilanmotos.domain.repository.TipoServicioRepository;
import com.dilanmotos.infrastructure.dto.TipoServicioRequestDTO;
import com.dilanmotos.infrastructure.dto.TipoServicioResponseDTO;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoServicioUCTest {

    @Mock
    private TipoServicioRepository tipoServicioRepository;

    @InjectMocks
    private TipoServicioUC tipoServicioUC;

    @Test
    @DisplayName("Debe obtener un tipo de servicio por ID correctamente")
    public void obtenerPorId_CuandoTipoServicioExiste_DebeRetornarDTO() {
        // Arrange
        Integer id = 1;
        TipoServicio tsSimulado = new TipoServicio();
        tsSimulado.setIdTipoServicio(id);
        tsSimulado.setNombre("Mantenimiento");
        tsSimulado.setDescripcion("Mantenimiento preventivo");

        when(tipoServicioRepository.buscarPorId(id)).thenReturn(Optional.of(tsSimulado));

        // Act
        TipoServicioResponseDTO resultado = tipoServicioUC.obtenerPorId(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(id, resultado.getIdTipo());
        assertEquals("Mantenimiento", resultado.getNombre());
        verify(tipoServicioRepository, times(1)).buscarPorId(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el tipo de servicio no existe al buscar por ID")
    public void obtenerPorId_CuandoTipoServicioNoExiste_DebeLanzarExcepcion() {
        // Arrange
        Integer id = 1;
        when(tipoServicioRepository.buscarPorId(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TipoServicioNotFoundException.class, () -> tipoServicioUC.obtenerPorId(id));
        verify(tipoServicioRepository, times(1)).buscarPorId(id);
    }

    @Test
    @DisplayName("Debe listar todos los tipos de servicio correctamente")
    public void listarTodas_CuandoExistenTiposServicio_DebeRetornarListaDTO() {
        // Arrange
        TipoServicio ts1 = new TipoServicio();
        ts1.setIdTipoServicio(1);
        ts1.setNombre("Mantenimiento");

        TipoServicio ts2 = new TipoServicio();
        ts2.setIdTipoServicio(2);
        ts2.setNombre("Reparación");

        when(tipoServicioRepository.obtenerTodas()).thenReturn(List.of(ts1, ts2));

        // Act
        List<TipoServicioResponseDTO> resultado = tipoServicioUC.listarTodas();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(tipoServicioRepository, times(1)).obtenerTodas();
    }

    @Test
    @DisplayName("Debe crear un tipo de servicio correctamente")
    public void crear_CuandoDatosValidos_DebeGuardarYRetornarDTO() {
        // Arrange
        TipoServicioRequestDTO requestDTO = new TipoServicioRequestDTO();
        requestDTO.setNombre("Mantenimiento");
        requestDTO.setDescripcion("Cambio de aceite");

        TipoServicio tsGuardado = new TipoServicio();
        tsGuardado.setIdTipoServicio(1);
        tsGuardado.setNombre("Mantenimiento");
        tsGuardado.setDescripcion("Cambio de aceite");

        when(tipoServicioRepository.guardar(any(TipoServicio.class))).thenReturn(tsGuardado);

        // Act
        TipoServicioResponseDTO resultado = tipoServicioUC.crear(requestDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getIdTipo());
        verify(tipoServicioRepository, times(1)).guardar(any(TipoServicio.class));
    }

    @Test
    @DisplayName("Debe editar/actualizar un tipo de servicio existente correctamente")
    public void actualizar_CuandoTipoServicioExiste_DebeActualizarYRetornarDTO() {
        // Arrange
        Integer id = 1;
        TipoServicioRequestDTO requestDTO = new TipoServicioRequestDTO();
        requestDTO.setNombre("Mantenimiento General");
        requestDTO.setDescripcion("Revisión completa");

        TipoServicio tsExistente = new TipoServicio();
        tsExistente.setIdTipoServicio(id);

        TipoServicio tsActualizado = new TipoServicio();
        tsActualizado.setIdTipoServicio(id);
        tsActualizado.setNombre("Mantenimiento General");
        tsActualizado.setDescripcion("Revisión completa");

        when(tipoServicioRepository.buscarPorId(id)).thenReturn(Optional.of(tsExistente));
        when(tipoServicioRepository.actualizar(any(TipoServicio.class))).thenReturn(tsActualizado);

        // Act
        TipoServicioResponseDTO resultado = tipoServicioUC.actualizar(id, requestDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(id, resultado.getIdTipo());
        verify(tipoServicioRepository, times(1)).buscarPorId(id);
        verify(tipoServicioRepository, times(1)).actualizar(any(TipoServicio.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar cuando el tipo de servicio no existe")
    public void actualizar_CuandoTipoServicioNoExiste_DebeLanzarExcepcion() {
        // Arrange
        Integer id = 1;
        TipoServicioRequestDTO requestDTO = new TipoServicioRequestDTO();

        when(tipoServicioRepository.buscarPorId(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TipoServicioNotFoundException.class, () -> tipoServicioUC.actualizar(id, requestDTO));
        verify(tipoServicioRepository, times(1)).buscarPorId(id);
        verify(tipoServicioRepository, never()).actualizar(any(TipoServicio.class));
    }

    @Test
    @DisplayName("Debe eliminar un tipo de servicio correctamente")
    public void eliminar_CuandoIdExiste_DebeLlamarRepositoryEliminar() {
        // Arrange
        Integer id = 1;
        TipoServicio tsExistente = new TipoServicio();
        tsExistente.setIdTipoServicio(id);

        when(tipoServicioRepository.buscarPorId(id)).thenReturn(Optional.of(tsExistente));
        doNothing().when(tipoServicioRepository).eliminar(id);

        // Act
        tipoServicioUC.eliminar(id);

        // Assert
        verify(tipoServicioRepository, times(1)).buscarPorId(id);
        verify(tipoServicioRepository, times(1)).eliminar(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar cuando el tipo de servicio no existe")
    public void eliminar_CuandoIdNoExiste_DebeLanzarExcepcion() {
        // Arrange
        Integer id = 1;
        when(tipoServicioRepository.buscarPorId(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TipoServicioNotFoundException.class, () -> tipoServicioUC.eliminar(id));
        verify(tipoServicioRepository, times(1)).buscarPorId(id);
        verify(tipoServicioRepository, never()).eliminar(id);
    }
}