package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.Usuario;
import com.dilanmotos.domain.repository.UsuarioRepository;

//Importamos las librerías necesarias para realizar las pruebas unitarias
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
//Anotación para indicar que se utilizará Mockito en las pruebas unitarias
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//ExtendWith se utiliza para extender la funcionalidad de JUnit 5 y permitir el uso de Mockito en las pruebas unitarias
@ExtendWith(MockitoExtension.class)
/**
 * UsuarioUCTest
 */
public class UsuarioUCTest {
    // // Utilizamos la anotación @Mock para crear un objeto simulado (mock) del
    // // repositorio de usuarios
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    // // Utilizamos la anotación @InjectMocks para crear una instancia de UsuarioUC
    // y
    // // inyectar automáticamente los objetos simulados (mocks) en sus dependencias
    @InjectMocks
    private UsuarioUC usuarioUC;

    // // Utilizamos la anotación @Test para indicar que el método
    // // registrar_CuandoEmailNoExiste_DebeGuardarUsuarioExitosamente es un caso de
    // // prueba
    @Test
    void registrar_CuandoEmailNoExiste_DebeGuardarUsuarioExitosamente() {

        // // // 1. Arrange (Preparar)
        String correo = "juan@gmail.com";
        String clave = "1234567";

        // // // Datos de entrada
        Usuario usuarioEntrada = new Usuario();
        usuarioEntrada.setCorreo(correo);
        usuarioEntrada.setContrasena(clave);

        // // // Simulamos que el correo no existente en la DB
        // // // Cuando se llame al método buscarPorCorreo con el correo proporcionado,
        // // // devolverá un Optional vacío
        when(usuarioRepository.buscarPorCorreo(correo)).thenReturn(Optional.empty());

        // // // Simulamos la encriptacion
        // // // Cuando se llame al método encode con la clave proporcionada, devolverá
        // // // "ClaveEncriptada"
        when(passwordEncoder.encode(clave)).thenReturn("ClaveEncriptada");

        // // // simulamos el guardado en el repositorio
        // // // Cuando se llame al método guardar con cualquier objeto Usuario,
        // devolverá
        // // el
        // // // mismo objeto Usuario que se pasó como argumento
        when(usuarioRepository.guardar(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // // // 2. Act (Actuar)
        // // // Llamamos al método registrar del caso de uso UsuarioUC con el usuario
        // de
        // // // entrada
        Usuario usuarioCreado = usuarioUC.registrar(usuarioEntrada);

        // // 3. Verificacion (Assert)

        assertNotNull(usuarioCreado, "El usuario no debe ser nulo");
        assertEquals(correo, usuarioCreado.getCorreo());
        assertEquals("USER", usuarioCreado.getRol(), "Debe asignar un rol");
        assertEquals("ClaveEncriptada", usuarioCreado.getContrasena());

        // // Verifcamos que los metodos se llamaron una vez
        verify(usuarioRepository, times(1)).buscarPorCorreo(correo);
        verify(usuarioRepository, times(1)).guardar(any(Usuario.class));

    }

    @Test
    void cambiarContrasena_CuandoDatosSonCorrectos_DebeActualizarExitosamente() {

        // 1.Arrange (Preparar)
        Integer idUsuario = 1;
        String contrasenaActual = "claveVieja123";
        String contrasenaNueva = "claveNueva456";

        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setIdUsuario(idUsuario);
        usuarioExistente.setContrasena("claveViejaEncriptada");

        // Simulamos que encuentra el usuario por ID
        when(usuarioRepository.buscarPorId(idUsuario)).thenReturn(Optional.of(usuarioExistente));

        // Simulamos que la contrasena actual coincide

        when(passwordEncoder.matches(contrasenaActual, "claveViejaEncriptada")).thenReturn(true);

        // simulamos encriptacion de contraseña nueva
        when(passwordEncoder.encode(contrasenaNueva)).thenReturn("claveNuevaEncriptada");

        // 2.ACT
        usuarioUC.cambiarContrasena(idUsuario, contrasenaActual, contrasenaNueva);

        // 3.assert
        verify(usuarioRepository, times(1)).actualizarContrasena(idUsuario, "claveNuevaEncriptada");
    }
}
