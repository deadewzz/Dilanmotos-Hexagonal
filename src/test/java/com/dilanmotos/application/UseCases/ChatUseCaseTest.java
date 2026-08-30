package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.ChatResponse;
import com.dilanmotos.domain.model.Moto;
import com.dilanmotos.domain.model.Marca;
import com.dilanmotos.domain.repository.MarcaRepository;
import com.dilanmotos.domain.repository.MotoRepository;
import com.dilanmotos.infrastructure.persistence.ChatExternalPort;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ChatUseCaseTest {

    @InjectMocks
    private ChatUseCase chatUseCase;
    @Mock
    private ChatExternalPort chatExternalPort;
    @Mock
    private MotoRepository motoRepository;
    @Mock
    private MarcaRepository marcaRepository;
    

    @Test
    @DisplayName("Debe ejecutar el caso de uso de chat correctamente")
    void debeEjecutarChatUseCaseCorrectamente() {
        // Arrange
        String mensaje = "Hola, necesito ayuda con mi moto.";
        Integer idUsuario = 1;

        Moto moto= new Moto();
        moto.setIdMarca(1);

        when(motoRepository.obtenerPorUsuario(idUsuario)).thenReturn(java.util.List.of(moto));
        when(marcaRepository.buscarPorId(1)).thenReturn(java.util.Optional.of(new Marca(1, "MarcaX")));
        when(chatExternalPort.getAiAnswer(mensaje, "MarcaX", moto.getModelo(), moto.getCilindraje()))
                .thenReturn(new ChatResponse("Respuesta simulada del AI"));

        // Act
        ChatResponse respuesta = chatUseCase.execute(mensaje, idUsuario);

        // Assert
        assertNotNull(respuesta);
    }

        @Test
        @DisplayName("Debe traer el nombre de la marca correctamente")
        void debeTraerNombreMarcaCorrectamente() {
            // Arrange
            Integer idMarca = 1;
            Marca marca = new Marca(idMarca, "MarcaX");
            
            when(marcaRepository.buscarPorId(idMarca)).thenReturn(java.util.Optional.of(marca));

            // Act
            String nombreMarca = marcaRepository.buscarPorId(idMarca)
                    .map(m -> m.getNombre())
                    .orElse("Marca desconocida");

            // Assert
            assertNotNull(nombreMarca);
            assertEquals("MarcaX", nombreMarca);
            verify(marcaRepository).buscarPorId(idMarca);

        }
        

        @Test
    @DisplayName("Debe retornar mensaje de advertencia cuando el usuario no tiene motos")
    void debeRetornarMensajeSiNoHayMotosRegistradas() {
        // Arrange
        String mensaje = "Hola";
        Integer idUsuario = 1;

        when(motoRepository.obtenerPorUsuario(idUsuario)).thenReturn(Collections.emptyList());

        // Act
        ChatResponse respuesta = chatUseCase.execute(mensaje, idUsuario);

        // Assert
        assertNotNull(respuesta);
        assertEquals(
            "Parcero, no tienes ninguna moto registrada. Registra tu moto primero para darte recomendaciones personalizadas.",
            respuesta.content()
        );
        verify(motoRepository).obtenerPorUsuario(idUsuario);
        verifyNoInteractions(marcaRepository, chatExternalPort);
    }
    }
