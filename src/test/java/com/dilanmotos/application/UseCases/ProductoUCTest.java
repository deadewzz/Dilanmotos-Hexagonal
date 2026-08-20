package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.Producto;
import com.dilanmotos.domain.repository.ProductoRepository;
import com.dilanmotos.infrastructure.dto.ProductoRequestDTO;
import com.dilanmotos.infrastructure.dto.ProductoResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class ProductoUCTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoUC productoUC;

    private Producto productoEjemplo;
    private ProductoRequestDTO requestDTO;

    // Un método que se ejecuta antes de cada prueba para inicializar los objetos de
    // prueba
    @BeforeEach
    void setUp() {
        productoEjemplo = new Producto();
        productoEjemplo.setIdProducto(1);
        productoEjemplo.setIdCategoria(2);
        productoEjemplo.setIdMarca(3);
        productoEjemplo.setNombre("Aceite DiverOsma");
        productoEjemplo.setDescripcion("Aceite para motos");
        productoEjemplo.setPrecio(35000.00);
        productoEjemplo.setImagen_url("http://img.com/aceite.jpg");
        productoEjemplo.setStock(15);
        productoEjemplo.setDisponible(true);
        productoEjemplo.setNombreCategoria("Lubricantes");
        productoEjemplo.setNombreMarca("Yamaha");

        // Crear un ProductoRequestDTO de ejemplo
        requestDTO = new ProductoRequestDTO();
        requestDTO.setIdCategoria(2);
        requestDTO.setIdMarca(3);
        requestDTO.setNombre("Aceite DiverOsma");
        requestDTO.setDescripcion("Aceite para motos sepsis");
        requestDTO.setPrecio(35000.00);
        requestDTO.setImagenUrl("http://img.com/aceite.jpg");
        requestDTO.setStock(15);
        requestDTO.setDisponible(true);

    }

    @Test
    @DisplayName("Listar productos exitosamente")
    void listarTodos_RetornarListaDeDTOs() {
        // Configurar el comportamiento del mock para obtener todos los productos
        when(productoRepository.obtenerTodos())
                .thenReturn(List.of(productoEjemplo));

        // Llamar al método que se está probando
        List<ProductoResponseDTO> resultado = productoUC.listarTodos();

        // Verificar que el resultado no sea nulo y contenga los datos esperados
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Aceite DiverOsma", resultado.get(0).getNombre());
        assertEquals("Lubricantes", resultado.get(0).getNombreCategoria());
        assertEquals("Yamaha", resultado.get(0).getNombreMarca());
        verify(productoRepository, times(1)).obtenerTodos();

    }

    @Test
    @DisplayName("Buscar producto por ID exitosamente")
    void buscarPorId_Existente_RetornarDTO() {

        // Configurar el comportamiento del mock para buscar un producto por ID
        when(productoRepository.buscarPorId(1))
                .thenReturn(Optional.of(productoEjemplo));

        ProductoResponseDTO resultado = productoUC.buscarPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getIdProducto());
        assertEquals("Aceite DiverOsma", resultado.getNombre());
        verify(productoRepository, times(1)).buscarPorId(1);

    }

    @Test
    @DisplayName("Buscar producto por ID que no existe")
    void buscarPorId_Inexistente_LazarExepction() {
        // Configurar el comportamiento del mock para buscar un producto por ID que no
        // existe
        when(productoRepository.buscarPorId(99)).thenReturn(Optional.empty());
        // Verificar que se lance una excepción al buscar un producto que no existe
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productoUC.buscarPorId(99);
        });
        assertEquals("Producto no encontrado", exception.getMessage());
        verify(productoRepository, times(1)).buscarPorId(99);
    }

    @Test
    @DisplayName("Crear producto exitosamente")
    void crear_Exitoso_RetornarDTO() {
        // Configurar el comportamiento del mock para guardar un producto
        when(productoRepository.guardar(any(Producto.class))).thenReturn(productoEjemplo);

        ProductoResponseDTO resultado = productoUC.crear(requestDTO);
        // Verificar que el resultado no sea nulo y contenga los datos esperados
        assertNotNull(resultado);
        assertEquals("Aceite DiverOsma", resultado.getNombre());
        verify(productoRepository, times(1)).guardar(any(Producto.class));

    }

    @Test
    @DisplayName("Actualizar producto exitosamente")
    void actualizar_Exitoso_RetornarDTO() {

        when(productoRepository.actualizar(eq(1), any(Producto.class))).thenReturn(productoEjemplo);

        ProductoResponseDTO resultado = productoUC.actualizar(1, requestDTO);

        assertNotNull(resultado);
        assertEquals("Aceite DiverOsma", resultado.getNombre());
        verify(productoRepository, times(1)).actualizar(eq(1), any(Producto.class));

    }

    @Test
    @DisplayName("Eliminar producto exitosamente")
    void eliminar_Exitoso() {
        doNothing().when(productoRepository).eliminar(1);
        productoUC.eliminar(1);
        verify(productoRepository, times(1)).eliminar(1);

    }

}
