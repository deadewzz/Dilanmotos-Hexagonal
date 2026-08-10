package com.dilanmotos.application.UseCases;

//Importamos las librerías necesarias para realizar las pruebas unitarias
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

//Importamos las clases necesarias para las pruebas unitarias
import com.dilanmotos.infrastructure.dto.MecanicoResponseDTO;
import com.dilanmotos.domain.model.Mecanico;
import com.dilanmotos.domain.repository.MecanicoRepository;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
//Anotación para indicar que se utilizará Mockito en las pruebas unitarias
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
/**
 * MecanicoUCTest
 */
class MecanicoUCTest {
    
    @Mock
    private MecanicoRepository mecanicoRepository;

    @InjectMocks
    private MecanicoUC mecanicoUC;

    @Test
    @DisplayName("Debe obtener un mecánico por ID correctamente")
    public void obtenerPorId_CuandoMecanicoExiste_DebeRetornarMecanico() {
        
        //Arrange
        int idMecanico = 1;
        Mecanico mecanicoSimulado = new Mecanico();
        mecanicoSimulado.setIdMecanico(idMecanico);
        when(mecanicoRepository.buscarPorId(idMecanico)).thenReturn(Optional.of(mecanicoSimulado));

        // Act
        MecanicoResponseDTO resultado = mecanicoUC.obtenerPorId(idMecanico);

        // Assert
        assertNotNull(resultado);
    }

}
