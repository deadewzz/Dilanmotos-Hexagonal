package com.dilanmotos.application.UseCases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dilanmotos.domain.model.Marca;
import com.dilanmotos.domain.repository.MarcaRepository;
import com.dilanmotos.infrastructure.dto.MarcaRequestDTO;
import com.dilanmotos.infrastructure.dto.MarcaResponseDTO;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarcaUCTest {

    @Mock
    private MarcaRepository marcaRepository;

    @InjectMocks
    private MarcaUC marcaUC;

    @Test
    @DisplayName("Debe obtener una marca por ID correctamente")
    public void obtenerPorId_CuandoMarcaExiste_DebeRetornarMarca() {
        // Arrange
        int idMarca = 1;
        Marca marcaSimulada = new Marca();
        marcaSimulada.setIdMarca(idMarca);
        when(marcaRepository.buscarPorId(idMarca)).thenReturn(Optional.of(marcaSimulada));

        // Act
        MarcaResponseDTO resultado = marcaUC.obtenerPorId(idMarca);

        // Assert
        assertNotNull(resultado);
        verify(marcaRepository, times(1)).buscarPorId(idMarca);
    }

    @Test
    @DisplayName("Debe listar todas las marcas correctamente")
    public void listar_CuandoExistenMarcas_DebeRetornarListaDTO() {
        // Arrange
        Marca m1 = new Marca();
        m1.setIdMarca(1);
        Marca m2 = new Marca();
        m2.setIdMarca(2);

        when(marcaRepository.obtenerTodos()).thenReturn(List.of(m1, m2));

        // Act
        List<MarcaResponseDTO> resultado = marcaUC.listarTodas();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(marcaRepository, times(1)).obtenerTodos();
    }

    @Test
    @DisplayName("Debe crear una marca correctamente")
    public void crear_CuandoDatosValidos_DebeGuardarYRetornarDTO() {
        // Arrange
        MarcaRequestDTO requestDTO = new MarcaRequestDTO();
        Marca marcaGuardada = new Marca();
        marcaGuardada.setIdMarca(1);

        when(marcaRepository.guardar(any(Marca.class))).thenReturn(marcaGuardada);

        // Act
        MarcaResponseDTO resultado = marcaUC.crear(requestDTO);

        // Assert
        assertNotNull(resultado);
        verify(marcaRepository, times(1)).guardar(any(Marca.class));
    }

    @Test
    @DisplayName("Debe editar/actualizar una marca existente correctamente")
    public void editar_CuandoMarcaExiste_DebeActualizarYRetornarDTO() {
        // Arrange
        int idMarca = 1;
        MarcaRequestDTO requestDTO = new MarcaRequestDTO();
        Marca marcaExistente = new Marca();
        marcaExistente.setIdMarca(idMarca);

        when(marcaRepository.buscarPorId(idMarca)).thenReturn(Optional.of(marcaExistente));
        when(marcaRepository.actualizar(any(Marca.class))).thenReturn(marcaExistente);

        // Act
        MarcaResponseDTO resultado = marcaUC.actualizar(idMarca, requestDTO);

        // Assert
        assertNotNull(resultado);
        verify(marcaRepository, times(1)).buscarPorId(idMarca);
        verify(marcaRepository, times(1)).actualizar(any(Marca.class));
    }
    @Test
    @DisplayName("Debe eliminar una marca correctamente")
    public void borrar_CuandoIdExiste_DebeLlamarRepositoryEliminar() {
        // Arrange
        int idMarca = 1;
        Marca marcaSimulada = new Marca();
        marcaSimulada.setIdMarca(idMarca);

        when(marcaRepository.buscarPorId(idMarca)).thenReturn(Optional.of(marcaSimulada));
        doNothing().when(marcaRepository).eliminar(idMarca);

        // Act
        marcaUC.eliminar(idMarca);

        // Assert
        verify(marcaRepository, times(1)).eliminar(idMarca);
    }
}