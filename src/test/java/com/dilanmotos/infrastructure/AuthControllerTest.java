package com.dilanmotos.infrastructure;

import com.dilanmotos.application.UseCases.UsuarioService;
import com.dilanmotos.domain.model.Usuario;
import com.dilanmotos.infrastructure.Security.JwtUtil;
import com.dilanmotos.infrastructure.controller.AuthController;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    // Dependencias inyectadas en el AuthController
    @Mock
    private AuthenticationManager authManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UsuarioService usuarioService;

    // Controlador real que vamos a probar
    @InjectMocks
    private AuthController authController;

    @Test
    void login_CuandoCredencialesSonValidas_DebeRetornarTokenYRespuesta200() {
        // --- 1. ARRANGE ---
        String correo = "juan@gmail.com";
        String clave = "J123456";
        // Creamos un mapa de solicitud simulando la entrada del usuario
        Map<String, String> requestLogin = Map.of(
                "correo", correo,
                "contrasena", clave);
        // Creamos un usuario simulado que será devuelto por el servicio
        Usuario usuarioMock = new Usuario();
        usuarioMock.setIdUsuario(1);
        usuarioMock.setNombre("Juan Andrés");
        usuarioMock.setCorreo(correo);
        usuarioMock.setRol("USER");

        // Simulamos que el usuario existe en la base de datos
        UserDetails userDetailsMock = mock(UserDetails.class);
        when(userDetailsMock.getUsername()).thenReturn(correo);

        // Simulaciones
        when(usuarioService.buscarPorCorreo(correo)).thenReturn(usuarioMock);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userDetailsService.loadUserByUsername(correo)).thenReturn(userDetailsMock);
        when(jwtUtil.generateToken(correo)).thenReturn("tokenSimuladoJWT123");

        // --- 2. ACT ---
        // Llamamos al método login del controlador con la solicitud simulada
        ResponseEntity<?> respuesta = authController.login(requestLogin);

        // --- 3. ASSERT ---
        // Verificamos que la respuesta tenga el estado HTTP 200 OK
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());

        // Verificamos el contenido del Map retornado
        Map<?, ?> body = (Map<?, ?>) respuesta.getBody();
        assertNotNull(body);
        assertEquals("tokenSimuladoJWT123", body.get("token"));
        assertEquals(1, body.get("id_usuario"));
        assertEquals("Juan Andrés", body.get("nombre"));
        assertEquals("USER", body.get("rol"));

        // Verificamos interacciones
        verify(authManager, times(1)).authenticate(any());
        verify(jwtUtil, times(1)).generateToken(correo);
    }
}