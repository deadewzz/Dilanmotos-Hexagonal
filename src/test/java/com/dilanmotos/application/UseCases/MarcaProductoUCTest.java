package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.MarcaProducto;
import com.dilanmotos.domain.repository.MarcaProductoRepository;
import com.dilanmotos.infrastructure.dto.MarcaProductoRequestDTO;
import com.dilanmotos.infrastructure.dto.MarcaProductoResponseDTO;

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
class MarcaProductoUCTest {

    private MarcaProductoUC marcaProductoUC;

    @Mock
    private MarcaProductoRepository marcaProductoRepository;

    @BeforeEach
    void setUp() {
        marcaProductoUC =
                new MarcaProductoUC(marcaProductoRepository);
    }

    // CREAR MARCA DE PRODUCTO
    @Test
    @DisplayName("Debe crear una marca de producto correctamente")
    void debeCrearMarcaProductoCorrectamente() {

        // Arrange
        MarcaProductoRequestDTO request =
                new MarcaProductoRequestDTO();

        request.setIdMarcaProducto(1);
        request.setNombre("Castrol");
        request.setIdCategoria(10);

        MarcaProducto marcaProductoGuardada =
                new MarcaProducto();

        marcaProductoGuardada.setIdMarcaProducto(1);
        marcaProductoGuardada.setNombre("Castrol");
        marcaProductoGuardada.setIdCategoria(10);

        when(marcaProductoRepository.guardar(
                any(MarcaProducto.class)
        )).thenReturn(marcaProductoGuardada);

        // Act
        MarcaProductoResponseDTO resultado =
                marcaProductoUC.crear(request);

        // Assert
        assertNotNull(resultado);
        assertEquals(1,resultado.getIdMarcaProducto());
        assertEquals("Castrol",resultado.getNombre());
        assertEquals(10,resultado.getIdCategoria());

        verify(marcaProductoRepository, times(1))
                .guardar(any(MarcaProducto.class));
    }

    // LISTAR TODAS LAS MARCAS
    @Test
    @DisplayName("Debe listar todas las marcas de productos correctamente")
    void debeListarTodasLasMarcasCorrectamente() {

        // Arrange
        MarcaProducto marca1 =
                new MarcaProducto();

        marca1.setIdMarcaProducto(1);
        marca1.setNombre("Castrol");
        marca1.setIdCategoria(10);

        MarcaProducto marca2 =
                new MarcaProducto();

        marca2.setIdMarcaProducto(2);
        marca2.setNombre("Motul");
        marca2.setIdCategoria(10);

        List<MarcaProducto> marcas =
                List.of(
                        marca1,
                        marca2
                );

        when(marcaProductoRepository.obtenerTodos())
                .thenReturn(marcas);

        // Act
        List<MarcaProductoResponseDTO> resultado =
                marcaProductoUC.listarTodas();

        // Assert
        assertNotNull(resultado);
        assertEquals(2,resultado.size());
        assertEquals(1,resultado.get(0).getIdMarcaProducto());
        assertEquals("Castrol",resultado.get(0).getNombre());
        assertEquals(2,resultado.get(1).getIdMarcaProducto());
        assertEquals("Motul",resultado.get(1).getNombre());

        verify(marcaProductoRepository, times(1))
                .obtenerTodos();
    }

    // LISTAR POR CATEGORÍA
    @Test
    @DisplayName("Debe listar las marcas de una categoría correctamente")
    void debeListarMarcasPorCategoriaCorrectamente() {

        // Arrange
        Integer idCategoria = 10;

        MarcaProducto marca1 =
                new MarcaProducto();

        marca1.setIdMarcaProducto(1);
        marca1.setNombre("Castrol");
        marca1.setIdCategoria(idCategoria);

        MarcaProducto marca2 =
                new MarcaProducto();

        marca2.setIdMarcaProducto(2);
        marca2.setNombre("Motul");
        marca2.setIdCategoria(idCategoria);

        when(marcaProductoRepository.obtenerPorCategoria(
                idCategoria
        )).thenReturn(List.of(marca1, marca2));

        // Act
        List<MarcaProductoResponseDTO> resultado =
                marcaProductoUC.listarPorCategoria(idCategoria);

        // Assert
        assertNotNull(resultado);
        assertEquals(2,resultado.size());
        assertEquals(1,resultado.get(0).getIdMarcaProducto());
        assertEquals("Castrol",resultado.get(0).getNombre());
        assertEquals(idCategoria,resultado.get(0).getIdCategoria());
        assertEquals(2,resultado.get(1).getIdMarcaProducto());
        assertEquals("Motul",resultado.get(1).getNombre());
        assertEquals(idCategoria,resultado.get(1).getIdCategoria());

        verify(marcaProductoRepository, times(1))
                .obtenerPorCategoria(idCategoria);
    }

    // OBTENER MARCA POR ID
    @Test
    @DisplayName("Debe obtener una marca de producto por ID correctamente")
    void debeObtenerMarcaProductoPorIdCorrectamente() {

        // Arrange
        Integer id = 1;
        MarcaProducto marcaProducto =
                new MarcaProducto();

        marcaProducto.setIdMarcaProducto(id);
        marcaProducto.setNombre("Castrol");
        marcaProducto.setIdCategoria(10);

        when(marcaProductoRepository.buscarPorId(id))
                .thenReturn(Optional.of(marcaProducto));

        // Act
        MarcaProductoResponseDTO resultado =
                marcaProductoUC.obtenerPorId(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(id,resultado.getIdMarcaProducto());
        assertEquals("Castrol",resultado.getNombre());
        assertEquals(10,resultado.getIdCategoria());

        verify(marcaProductoRepository, times(1))
                .buscarPorId(id);
    }

    // ACTUALIZAR MARCA DE PRODUCTO
    @Test
    @DisplayName("Debe actualizar una marca de producto correctamente")
    void debeActualizarMarcaProductoCorrectamente() {

        // Arrange
        Integer id = 1;

        MarcaProducto marcaExistente =
                new MarcaProducto();

        marcaExistente.setIdMarcaProducto(id);
        marcaExistente.setNombre("Castrol");
        marcaExistente.setIdCategoria(10);

        MarcaProductoRequestDTO request =
                new MarcaProductoRequestDTO();

        request.setNombre("Castrol Edge");
        request.setIdCategoria(10);

        when(marcaProductoRepository.buscarPorId(id))
                .thenReturn(Optional.of(marcaExistente));

        when(marcaProductoRepository.actualizar(
                any(MarcaProducto.class)
        )).thenAnswer(invocation -> {

            MarcaProducto marca =
                    invocation.getArgument(0);

            return marca;
        });

        // Act
        MarcaProductoResponseDTO resultado = marcaProductoUC.actualizar(id,request);

        // Assert
        assertNotNull(resultado);
        assertEquals(id,resultado.getIdMarcaProducto());
        assertEquals("Castrol Edge",resultado.getNombre());
        assertEquals(10,resultado.getIdCategoria());

        verify(marcaProductoRepository, times(1))
                .buscarPorId(id);

        verify(marcaProductoRepository, times(1))
                .actualizar(any(MarcaProducto.class));
    }

    // ELIMINAR MARCA DE PRODUCTO
    @Test
    @DisplayName("Debe eliminar una marca de producto correctamente")
    void debeEliminarMarcaProductoCorrectamente() {

        // Arrange
        Integer id = 1;

        MarcaProducto marcaProducto =
                new MarcaProducto();

        marcaProducto.setIdMarcaProducto(id);
        marcaProducto.setNombre("Castrol");
        marcaProducto.setIdCategoria(10);

        when(marcaProductoRepository.buscarPorId(id))
                .thenReturn(Optional.of(marcaProducto));

        doNothing()
                .when(marcaProductoRepository)
                .eliminar(id);

        // Act
        marcaProductoUC.eliminar(id);

        // Assert
        verify(marcaProductoRepository, times(1))
                .buscarPorId(id);

        verify(marcaProductoRepository, times(1))
                .eliminar(id);
    }
}