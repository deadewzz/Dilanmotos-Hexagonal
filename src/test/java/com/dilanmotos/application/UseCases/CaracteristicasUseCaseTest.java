package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.exception.CaracteristicaNotFoundException;
import com.dilanmotos.domain.model.Caracteristica;
import com.dilanmotos.domain.model.Moto;
import com.dilanmotos.domain.repository.CaracteristicaRepository;
import com.dilanmotos.domain.repository.MotoRepository;
import com.dilanmotos.infrastructure.dto.CaracteristicaResponseDTO;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaracteristicasUseCaseTest {

    @Mock
    private CaracteristicaRepository caracteristicaRepository;

    @Mock
    private MotoRepository motoRepository;

    @InjectMocks
    private CaracteristicaUC caracteristicaUC;

    @Test
    @DisplayName("Debe listar todas las caracteristicas correctamente")
    void debeListarTodasCorrectamente() {
        // Arrange (Preparación)
        Caracteristica caracteristica1 = new Caracteristica();
        caracteristica1.setIdCaracteristica(1);
        caracteristica1.setIdMoto(10);
        caracteristica1.setDescripcion("Frenos ABS");

        Moto moto = new Moto();
        moto.setIdMoto(10);
        moto.setModelo("Sport 2026");

        when(caracteristicaRepository.obtenerTodas()).thenReturn(List.of(caracteristica1));
        when(motoRepository.buscarPorId(10)).thenReturn(Optional.of(moto));

        // Act (Acción)
        List<CaracteristicaResponseDTO> resultado = caracteristicaUC.listarTodas();

        // Assert (Verificación)
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Frenos ABS", resultado.get(0).getDescripcion());
        assertNotNull(resultado.get(0).getMoto());
        assertEquals("Sport 2026", resultado.get(0).getMoto().getModelo());

        verify(caracteristicaRepository, times(1)).obtenerTodas();
        verify(motoRepository, times(1)).buscarPorId(10);
    }

    @Test
    @DisplayName("Debe buscar una caracteristica por ID correctamente")
    void debeBuscarPorIdCorrectamente() {
        // Arrange 
        Integer id = 1;
        Caracteristica caracteristica = new Caracteristica();
        caracteristica.setIdCaracteristica(id);
        caracteristica.setIdMoto(5);
        caracteristica.setDescripcion("Escape deportivo");

        when(caracteristicaRepository.buscarPorId(id)).thenReturn(Optional.of(caracteristica));
        when(motoRepository.buscarPorId(5)).thenReturn(Optional.empty()); // Probando cuando no se encuentra la moto asociada

        // Act 
        CaracteristicaResponseDTO resultado = caracteristicaUC.obtenerPorId(id);

        // Assert 
        assertNotNull(resultado);
        assertEquals(id, resultado.getIdCaracteristica());
        assertEquals("Escape deportivo", resultado.getDescripcion());

        verify(caracteristicaRepository, times(1)).buscarPorId(id);
        verify(motoRepository, times(1)).buscarPorId(5);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando no encuentra la caracteristica por ID")
    void debeLanzarExcepcionCuandoNoEncuentraPorId() {
        // Arrange 
        Integer id = 99;
        when(caracteristicaRepository.buscarPorId(id)).thenReturn(Optional.empty());

        // Act
        assertThrows(CaracteristicaNotFoundException.class, () -> {
            caracteristicaUC.obtenerPorId(id);
        });

        // Assert

        verify(caracteristicaRepository, times(1)).buscarPorId(id);
        verifyNoInteractions(motoRepository);
    }
}