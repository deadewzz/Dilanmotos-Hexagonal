package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.exception.CotizacionNotFoundException;
import com.dilanmotos.domain.model.Cotizacion;
import com.dilanmotos.domain.model.Producto;
import com.dilanmotos.domain.repository.CotizacionRepository;
import com.dilanmotos.domain.repository.ProductoRepository;
import com.dilanmotos.infrastructure.dto.CotizacionRequestDTO;
import com.dilanmotos.infrastructure.dto.CotizacionResponseDTO;
import com.dilanmotos.infrastructure.persistence.CotizacionEntity;
import com.dilanmotos.infrastructure.persistence.UsuarioEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CotizacionUCTest {

    private CotizacionUC cotizacionUC;

    @Mock
    private CotizacionRepository cotizacionRepository;

    @Mock
    private ProductoRepository productoRepository;

    @BeforeEach
    void setUp() {
        cotizacionUC = new CotizacionUC(cotizacionRepository, productoRepository);
    }

    @Test
    @DisplayName("Debe listar todas las cotizaciones correctamente")
    void debeListarTodasCotizacionesCorrectamente() {
        // Arrange
        Cotizacion cotizacion1 = new Cotizacion();
        cotizacion1.setIdCotizacion(1);
        cotizacion1.setFecha(LocalDate.now());

        Cotizacion cotizacion2 = new Cotizacion();
        cotizacion2.setIdCotizacion(2);
        cotizacion2.setFecha(LocalDate.now());

        when(cotizacionRepository.obtenerTodas()).thenReturn(List.of(cotizacion1, cotizacion2));

        // Act
        List<CotizacionResponseDTO> resultado = cotizacionUC.listarTodas();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(cotizacionRepository, times(1)).obtenerTodas();
    }

    @Test
    @DisplayName("Debe listar cotizaciones por usuario correctamente")
    void debeListarPorUsuarioCorrectamente() {
        // Arrange
        Integer idUsuario = 1;
        CotizacionEntity entity = new CotizacionEntity();
        entity.setIdCotizacion(10);
        entity.setIdUsuario(idUsuario);
        entity.setIdProducto(100);
        entity.setProducto("Casco");
        entity.setCantidad(1);
        entity.setPrecioUnitario(150000.0);
        entity.setFecha(new Date());
        
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNombre("Carlos Pérez");
        entity.setUsuario(usuario);

        when(cotizacionRepository.findByIdUsuario(idUsuario)).thenReturn(List.of(entity));

        // Act
        List<CotizacionResponseDTO> resultado = cotizacionUC.listarPorUsuario(idUsuario);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Carlos Pérez", resultado.get(0).getNombreUsuario());
        verify(cotizacionRepository, times(1)).findByIdUsuario(idUsuario);
    }

    @Test
    @DisplayName("Debe crear una cotización correctamente")
    void debeCrearCotizacionCorrectamente() {
        // Arrange
        CotizacionRequestDTO request = new CotizacionRequestDTO();
        request.setIdUsuario(1);
        request.setIdProducto(101);
        request.setCantidad(2);
        request.setPrecioUnitario(0.0);
        request.setFecha(new Date());

        Producto productoSimulado = new Producto();
        productoSimulado.setIdProducto(101);
        productoSimulado.setNombre("Aceite de Motor");
        productoSimulado.setPrecio(25000.0);

        Cotizacion cotizacionGuardada = new Cotizacion();
        cotizacionGuardada.setIdCotizacion(1);
        cotizacionGuardada.setIdProducto(101);
        cotizacionGuardada.setProducto("Aceite de Motor");
        cotizacionGuardada.setPrecioUnitario(25000.0);
        cotizacionGuardada.setProducto_agregado(false);

        when(productoRepository.buscarPorId(101)).thenReturn(Optional.of(productoSimulado));
        when(cotizacionRepository.guardar(any(Cotizacion.class))).thenReturn(cotizacionGuardada);

        // Act
        CotizacionResponseDTO resultado = cotizacionUC.crear(request);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getIdCotizacion());
        assertEquals("Aceite de Motor", resultado.getProducto());
        assertFalse(resultado.getProducto_agregado());
        verify(cotizacionRepository, times(1)).guardar(any(Cotizacion.class));
    }

    @Test
    @DisplayName("Debe obtener una cotización por ID correctamente")
    void debeObtenerCotizacionPorIdCorrectamente() {
        // Arrange
        Integer id = 1;
        Cotizacion cotizacionSimulada = new Cotizacion();
        cotizacionSimulada.setIdCotizacion(id);

        when(cotizacionRepository.buscarPorId(id)).thenReturn(Optional.of(cotizacionSimulada));

        // Act
        CotizacionResponseDTO resultado = cotizacionUC.obtenerPorId(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(id, resultado.getIdCotizacion());
        verify(cotizacionRepository, times(1)).buscarPorId(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción al obtener una cotización por ID inexistente")
    void debeLanzarExcepcionAlObtenerCotizacionPorIdInexistente() {
        // Arrange
        Integer idInexistente = 999;
        when(cotizacionRepository.buscarPorId(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        CotizacionNotFoundException exception = assertThrows(
                CotizacionNotFoundException.class,
                () -> cotizacionUC.obtenerPorId(idInexistente)
        );

        assertTrue(exception.getMessage().contains("Cotización no encontrada con ID: " + idInexistente));
        verify(cotizacionRepository, times(1)).buscarPorId(idInexistente);
    }

    @Test
    @DisplayName("Debe actualizar una cotización correctamente")
    void debeActualizarCotizacionCorrectamente() {
        // Arrange
        Integer id = 1;
        CotizacionRequestDTO request = new CotizacionRequestDTO();
        request.setCantidad(5);

        Cotizacion cotizacionExistente = new Cotizacion();
        cotizacionExistente.setIdCotizacion(id);

        Cotizacion cotizacionActualizada = new Cotizacion();
        cotizacionActualizada.setIdCotizacion(id);
        cotizacionActualizada.setCantidad(5);

        when(cotizacionRepository.buscarPorId(id)).thenReturn(Optional.of(cotizacionExistente));
        when(cotizacionRepository.actualizar(any(Cotizacion.class))).thenReturn(cotizacionActualizada);

        // Act
        CotizacionResponseDTO resultado = cotizacionUC.actualizar(id, request);

        // Assert
        assertNotNull(resultado);
        assertEquals(5, resultado.getCantidad());
        verify(cotizacionRepository, times(1)).buscarPorId(id);
        verify(cotizacionRepository, times(1)).actualizar(any(Cotizacion.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar actualizar una cotización inexistente")
    void debeLanzarExcepcionAlActualizarCotizacionInexistente() {
        // Arrange
        Integer idInexistente = 999;
        CotizacionRequestDTO request = new CotizacionRequestDTO();

        when(cotizacionRepository.buscarPorId(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CotizacionNotFoundException.class, () -> cotizacionUC.actualizar(idInexistente, request));
        verify(cotizacionRepository, times(1)).buscarPorId(idInexistente);
        verify(cotizacionRepository, never()).actualizar(any());
    }

    @Test
    @DisplayName("Debe confirmar la compra descontando stock del producto correctamente")
    void debeConfirmarCompraCorrectamente() {
        // Arrange
        Integer id = 1;
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setIdCotizacion(id);
        cotizacion.setIdProducto(50);
        cotizacion.setCantidad(3);
        cotizacion.setProducto_agregado(false);

        Producto producto = new Producto();
        producto.setIdProducto(50);
        producto.setStock(10);
        producto.setDisponible(true);

        Cotizacion cotizacionConfirmada = new Cotizacion();
        cotizacionConfirmada.setIdCotizacion(id);
        cotizacionConfirmada.setProducto_agregado(true);

        when(cotizacionRepository.buscarPorId(id)).thenReturn(Optional.of(cotizacion));
        when(productoRepository.buscarPorId(50)).thenReturn(Optional.of(producto));
        when(cotizacionRepository.actualizar(any(Cotizacion.class))).thenReturn(cotizacionConfirmada);

        // Act
        CotizacionResponseDTO resultado = cotizacionUC.confirmarCompra(id);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.getProducto_agregado());
        assertEquals(7, producto.getStock()); // El stock bajó de 10 a 7
        verify(productoRepository, times(1)).actualizar(eq(50), any(Producto.class));
        verify(cotizacionRepository, times(1)).actualizar(cotizacion);
    }

    @Test
    @DisplayName("Debe lanzar excepción en confirmar compra si ya fue procesada previamente")
    void debeLanzarExcepcionEnConfirmarCompraSiYaFueProcesada() {
        // Arrange
        Integer id = 1;
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setIdCotizacion(id);
        cotizacion.setProducto_agregado(true); // Ya fue agregada/procesada

        when(cotizacionRepository.buscarPorId(id)).thenReturn(Optional.of(cotizacion));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> cotizacionUC.confirmarCompra(id));
        assertEquals("Esta cotización ya fue procesada como una venta.", exception.getMessage());
        verify(productoRepository, never()).buscarPorId(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción en confirmar compra si el stock es insuficiente")
    void debeLanzarExcepcionEnConfirmarCompraSiStockInsuficiente() {
        // Arrange
        Integer id = 1;
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setIdCotizacion(id);
        cotizacion.setIdProducto(50);
        cotizacion.setCantidad(10);
        cotizacion.setProducto_agregado(false);

        Producto producto = new Producto();
        producto.setIdProducto(50);
        producto.setStock(2); // Stock menor a la cantidad solicitada (10)

        when(cotizacionRepository.buscarPorId(id)).thenReturn(Optional.of(cotizacion));
        when(productoRepository.buscarPorId(50)).thenReturn(Optional.of(producto));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> cotizacionUC.confirmarCompra(id));
        assertTrue(exception.getMessage().contains("No hay stock suficiente"));
        verify(productoRepository, never()).actualizar(anyInt(), any());
    }

    @Test
    @DisplayName("Debe eliminar una cotización correctamente")
    void debeEliminarCotizacionCorrectamente() {
        // Arrange
        Integer id = 1;
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setIdCotizacion(id);

        when(cotizacionRepository.buscarPorId(id)).thenReturn(Optional.of(cotizacion));
        doNothing().when(cotizacionRepository).eliminar(id);

        // Act
        cotizacionUC.eliminar(id);

        // Assert
        verify(cotizacionRepository, times(1)).buscarPorId(id);
        verify(cotizacionRepository, times(1)).eliminar(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar eliminar una cotización inexistente")
    void debeLanzarExcepcionAlEliminarCotizacionInexistente() {
        // Arrange
        Integer idInexistente = 999;
        when(cotizacionRepository.buscarPorId(idInexistente)).thenReturn(Optional.empty());

        // Act 
        assertThrows(CotizacionNotFoundException.class, () -> cotizacionUC.eliminar(idInexistente));

        //Assert
        verify(cotizacionRepository, times(1)).buscarPorId(idInexistente);
        verify(cotizacionRepository, never()).eliminar(any());
    }
}