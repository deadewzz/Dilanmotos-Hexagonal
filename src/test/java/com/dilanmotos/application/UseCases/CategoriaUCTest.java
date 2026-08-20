package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.exception.CategoriaNotFoundException;
import com.dilanmotos.domain.model.Categoria;
import com.dilanmotos.domain.repository.CategoriaRepository;
import com.dilanmotos.infrastructure.dto.CategoriaRequestDTO;
import com.dilanmotos.infrastructure.dto.CategoriaResponseDTO;

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
class CategoriaUCTest {

    private CategoriaUC categoriaUC;

    @Mock
    private CategoriaRepository categoriaRepository;

    @BeforeEach
    void setUp() {
        categoriaUC = new CategoriaUC(categoriaRepository);
    }

    @Test
    @DisplayName("Debe listar todas las categorías correctamente")
    void debeListarTodasCategoriasCorrectamente() {
        // Arrange
        Categoria cat1 = new Categoria();
        cat1.setIdCategoria(1);
        cat1.setNombre("Repuestos");

        Categoria cat2 = new Categoria();
        cat2.setIdCategoria(2);
        cat2.setNombre("Accesorios");

        when(categoriaRepository.obtenerTodas()).thenReturn(List.of(cat1, cat2));

        // Act
        List<CategoriaResponseDTO> resultado = categoriaUC.listarTodas();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Repuestos", resultado.get(0).getNombre());
        assertEquals("Accesorios", resultado.get(1).getNombre());
        verify(categoriaRepository, times(1)).obtenerTodas();
    }

    @Test
    @DisplayName("Debe crear una categoría correctamente")
    void debeCrearCategoriaCorrectamente() {
        // Arrange
        CategoriaRequestDTO request = new CategoriaRequestDTO();
        request.setNombre("Lubricantes");

        Categoria categoriaGuardada = new Categoria();
        categoriaGuardada.setIdCategoria(1);
        categoriaGuardada.setNombre("Lubricantes");

        when(categoriaRepository.guardar(any(Categoria.class))).thenReturn(categoriaGuardada);

        // Act
        CategoriaResponseDTO resultado = categoriaUC.crear(request);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getIdCategoria());
        assertEquals("Lubricantes", resultado.getNombre());
        verify(categoriaRepository, times(1)).guardar(any(Categoria.class));
    }

    @Test
    @DisplayName("Debe obtener una categoría por ID correctamente")
    void debeObtenerCategoriaPorIdCorrectamente() {
        // Arrange
        Integer id = 1;
        Categoria categoriaSimulada = new Categoria();
        categoriaSimulada.setIdCategoria(id);
        categoriaSimulada.setNombre("Cascos");

        when(categoriaRepository.buscarPorId(id)).thenReturn(Optional.of(categoriaSimulada));

        // Act
        CategoriaResponseDTO resultado = categoriaUC.obtenerPorId(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(id, resultado.getIdCategoria());
        assertEquals("Cascos", resultado.getNombre());
        verify(categoriaRepository, times(1)).buscarPorId(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción al obtener una categoría por ID inexistente")
    void debeLanzarExcepcionAlObtenerCategoriaPorIdInexistente() {
        // Arrange
        Integer idInexistente = 999;
        when(categoriaRepository.buscarPorId(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        CategoriaNotFoundException exception = assertThrows(
                CategoriaNotFoundException.class,
                () -> categoriaUC.obtenerPorId(idInexistente)
        );

        assertTrue(exception.getMessage().contains("Categoria no encontrada con ID: " + idInexistente));
        verify(categoriaRepository, times(1)).buscarPorId(idInexistente);
    }

    @Test
    @DisplayName("Debe actualizar una categoría correctamente")
    void debeActualizarCategoriaCorrectamente() {
        // Arrange
        Integer id = 1;
        CategoriaRequestDTO request = new CategoriaRequestDTO();
        request.setNombre("Llantas");

        Categoria categoriaExistente = new Categoria();
        categoriaExistente.setIdCategoria(id);
        categoriaExistente.setNombre("Neumáticos");

        Categoria categoriaActualizada = new Categoria();
        categoriaActualizada.setIdCategoria(id);
        categoriaActualizada.setNombre("Llantas");

        when(categoriaRepository.buscarPorId(id)).thenReturn(Optional.of(categoriaExistente));
        when(categoriaRepository.actualizar(any(Categoria.class))).thenReturn(categoriaActualizada);

        // Act
        CategoriaResponseDTO resultado = categoriaUC.actualizar(id, request);

        // Assert
        assertNotNull(resultado);
        assertEquals(id, resultado.getIdCategoria());
        assertEquals("Llantas", resultado.getNombre());
        verify(categoriaRepository, times(1)).buscarPorId(id);
        verify(categoriaRepository, times(1)).actualizar(any(Categoria.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar actualizar una categoría inexistente")
    void debeLanzarExcepcionAlActualizarCategoriaInexistente() {
        // Arrange
        Integer idInexistente = 999;
        CategoriaRequestDTO request = new CategoriaRequestDTO();

        when(categoriaRepository.buscarPorId(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        CategoriaNotFoundException exception = assertThrows(
                CategoriaNotFoundException.class,
                () -> categoriaUC.actualizar(idInexistente, request)
        );

        assertTrue(exception.getMessage().contains("No se puede actualizar, categoria no existe: " + idInexistente));
        verify(categoriaRepository, times(1)).buscarPorId(idInexistente);
        verify(categoriaRepository, never()).actualizar(any());
    }

    @Test
    @DisplayName("Debe eliminar una categoría correctamente cuando existe")
    void debeEliminarCategoriaCorrectamente() {
        // Arrange
        Integer id = 1;
        Categoria categoriaExistente = new Categoria();
        categoriaExistente.setIdCategoria(id);

        when(categoriaRepository.buscarPorId(id)).thenReturn(Optional.of(categoriaExistente));
        doNothing().when(categoriaRepository).eliminar(id);

        // Act
        categoriaUC.eliminar(id);

        // Assert
        verify(categoriaRepository, times(1)).buscarPorId(id);
        verify(categoriaRepository, times(1)).eliminar(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar eliminar una categoría inexistente")
    void debeLanzarExcepcionAlEliminarCategoriaInexistente() {
        // Arrange
        Integer idInexistente = 999;
        when(categoriaRepository.buscarPorId(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        CategoriaNotFoundException exception = assertThrows(
                CategoriaNotFoundException.class,
                () -> categoriaUC.eliminar(idInexistente)
        );

        assertTrue(exception.getMessage().contains("No se puede eliminar, categoria no encontrada: " + idInexistente));
        verify(categoriaRepository, times(1)).buscarPorId(idInexistente);
        verify(categoriaRepository, never()).eliminar(any());
    }
}