package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.exception.MotoNotFoundException;
import com.dilanmotos.domain.model.Marca;
import com.dilanmotos.domain.model.Moto;
import com.dilanmotos.domain.repository.MarcaRepository;
import com.dilanmotos.domain.repository.MotoRepository;
import com.dilanmotos.infrastructure.dto.MotoRequestDTO;
import com.dilanmotos.infrastructure.dto.MotoResponseDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MotoUCTest {

    private MotoUC motoUC;

    @Mock
    private MotoRepository motoRepository;

    @Mock
    private MarcaRepository marcaRepository;

    @BeforeEach
    void setUp() {
        motoUC = new MotoUC(motoRepository, marcaRepository);
    }

    @Test
    @DisplayName("Debe listar todas las motos correctamente con su marca")
    void debeListarTodasMotosCorrectamente() {
        // Arrange
        Moto moto1 = new Moto();
        moto1.setIdMoto(1);
        moto1.setIdMarca(10);
        moto1.setModelo("2022");
        moto1.setCilindraje(150.0);

        Moto moto2 = new Moto();
        moto2.setIdMoto(2);
        moto2.setIdMarca(20);

        Marca marcaSimulada = new Marca();
        marcaSimulada.setIdMarca(10);
        marcaSimulada.setNombre("Yamaha");

        when(motoRepository.obtenerTodas()).thenReturn(List.of(moto1, moto2));
        when(marcaRepository.buscarPorId(10)).thenReturn(Optional.of(marcaSimulada));

        // Act
        List<MotoResponseDTO> resultado = motoUC.listarTodas();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Yamaha", resultado.get(0).getMarca().getNombre());
        verify(motoRepository, times(1)).obtenerTodas();
        verify(marcaRepository, times(2)).buscarPorId(any());
    }

    @Test
    @DisplayName("Debe crear una moto correctamente")
    void debeCrearMotoCorrectamente() {
        // Arrange
        MotoRequestDTO request = new MotoRequestDTO();
        request.setIdUsuario(1);
        request.setIdMarca(10);
        request.setModelo("2023");
        request.setCilindraje(200.0);

        Moto motoGuardada = new Moto();
        motoGuardada.setIdMoto(1);
        motoGuardada.setIdUsuario(1);
        motoGuardada.setIdMarca(10);
        motoGuardada.setModelo("2023");
        motoGuardada.setCilindraje(200.0);

        when(motoRepository.guardar(any(Moto.class))).thenReturn(motoGuardada);

        // Act
        MotoResponseDTO resultado = motoUC.crear(request);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getIdMoto());
        assertEquals("2023", resultado.getModelo());
        verify(motoRepository, times(1)).guardar(any(Moto.class));
    }

    @Test
    @DisplayName("Debe obtener una moto por ID correctamente")
    void debeObtenerMotoPorIdCorrectamente() {
        // Arrange
        Integer id = 1;
        Moto motoSimulada = new Moto();
        motoSimulada.setIdMoto(id);
        motoSimulada.setIdMarca(5);

        Marca marca = new Marca();
        marca.setIdMarca(5);
        marca.setNombre("Honda");

        when(motoRepository.buscarPorId(id)).thenReturn(Optional.of(motoSimulada));
        when(marcaRepository.buscarPorId(5)).thenReturn(Optional.of(marca));

        // Act
        MotoResponseDTO resultado = motoUC.obtenerPorId(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(id, resultado.getIdMoto());
        assertNotNull(resultado.getMarca());
        assertEquals("Honda", resultado.getMarca().getNombre());
        verify(motoRepository, times(1)).buscarPorId(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción al obtener una moto por ID inexistente")
    void debeLanzarExcepcionAlObtenerMotoPorIdInexistente() {
        // Arrange
        Integer idInexistente = 999;
        when(motoRepository.buscarPorId(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        MotoNotFoundException exception = assertThrows(
                MotoNotFoundException.class,
                () -> motoUC.obtenerPorId(idInexistente)
        );

        assertTrue(exception.getMessage().contains("Moto no encontrada: " + idInexistente));
        verify(motoRepository, times(1)).buscarPorId(idInexistente);
    }

    @Test
    @DisplayName("Debe listar las motos de un usuario correctamente")
    void debeListarPorUsuarioCorrectamente() {
        // Arrange
        Integer idUsuario = 1;
        Moto moto = new Moto();
        moto.setIdMoto(1);
        moto.setIdUsuario(idUsuario);
        moto.setIdMarca(2);

        when(motoRepository.obtenerPorUsuario(idUsuario)).thenReturn(List.of(moto));

        // Act
        List<MotoResponseDTO> resultado = motoUC.listarPorUsuario(idUsuario);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(idUsuario, resultado.get(0).getIdUsuario());
        verify(motoRepository, times(1)).obtenerPorUsuario(idUsuario);
    }

    @Test
    @DisplayName("Debe actualizar una moto correctamente")
    void debeActualizarMotoCorrectamente() {
        // Arrange
        Integer id = 1;
        MotoRequestDTO request = new MotoRequestDTO();
        request.setIdUsuario(1);
        request.setIdMarca(10);
        request.setModelo("2024");
        request.setCilindraje(250.0);

        Moto motoExistente = new Moto();
        motoExistente.setIdMoto(id);

        Moto motoActualizada = new Moto();
        motoActualizada.setIdMoto(id);
        motoActualizada.setModelo("2024");

        when(motoRepository.buscarPorId(id)).thenReturn(Optional.of(motoExistente));
        when(motoRepository.actualizar(any(Moto.class))).thenReturn(motoActualizada);

        // Act
        MotoResponseDTO resultado = motoUC.actualizar(id, request);

        // Assert
        assertNotNull(resultado);
        assertEquals(id, resultado.getIdMoto());
        assertEquals("2024", resultado.getModelo());
        verify(motoRepository, times(1)).buscarPorId(id);
        verify(motoRepository, times(1)).actualizar(any(Moto.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar actualizar una moto inexistente")
    void debeLanzarExcepcionAlActualizarMotoInexistente() {
        // Arrange
        Integer idInexistente = 999;
        MotoRequestDTO request = new MotoRequestDTO();

        when(motoRepository.buscarPorId(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        MotoNotFoundException exception = assertThrows(
                MotoNotFoundException.class,
                () -> motoUC.actualizar(idInexistente, request)
        );

        assertTrue(exception.getMessage().contains("No existe la moto: " + idInexistente));
        verify(motoRepository, times(1)).buscarPorId(idInexistente);
        verify(motoRepository, never()).actualizar(any());
    }

    @Test
    @DisplayName("Debe eliminar una moto correctamente")
    void debeEliminarMotoCorrectamente() {
        // Arrange
        Integer id = 1;
        doNothing().when(motoRepository).eliminar(id);

        // Act
        motoUC.eliminar(id);

        // Assert
        verify(motoRepository, times(1)).eliminar(id);
    }
}