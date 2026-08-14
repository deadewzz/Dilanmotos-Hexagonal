package com.dilanmotos.application.UseCases;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dilanmotos.domain.model.Cotizacion;
import com.dilanmotos.domain.repository.CotizacionRepository;
import com.dilanmotos.infrastructure.dto.CotizacionResponseDTO;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

/** 
 * * CotizacionUCTest 
 */

class CotizacionUCTest {
    @Mock 
    private CotizacionRepository cotizacionRepository;

    @InjectMocks 
    private CotizacionUC cotizacionUC;

    @Test
    @DisplayName("Debe obtener una cotización por ID correctamente")
    public void obtenerPorId_CuandoCotizacionExiste_DebeRetornarCotizacion() {
      // Arrange (Preparar)       
        Integer idCotizacion = 1;

        Cotizacion cotizacionSimulada = new Cotizacion();
        cotizacionSimulada.setIdCotizacion(idCotizacion);
        cotizacionSimulada.setNombreUsuario("Juan Pérez");
        cotizacionSimulada.setFecha(LocalDate.now());

        when(cotizacionRepository.buscarPorId(idCotizacion))
             .thenReturn(Optional.of(cotizacionSimulada));
         
      // Act (Ejecutar)        
        CotizacionResponseDTO resultado = cotizacionUC.obtenerPorId(idCotizacion);

      // Assert (Verificar)       
        assertNotNull(resultado);
        verify(cotizacionRepository, times(1)).buscarPorId(idCotizacion);
     }
    }