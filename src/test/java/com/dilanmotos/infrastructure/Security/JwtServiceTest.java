package com.dilanmotos.infrastructure.Security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtUtil jwtUtil;

    // Clave secreta fija para la prueba (debe tener al menos 32 caracteres para HS256)
    private final String secretKeyPrueba = "clave_secreta_super_segura_para_pruebas_unitarias_123456";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();

        // Asignamos el valor al atributo privado 'secret' que usas con @Value
        ReflectionTestUtils.setField(jwtUtil, "secret", secretKeyPrueba);
    }

    @Test
    @DisplayName("Debe generar un token JWT válido para el correo dado")
    void debeGenerarTokenExitosamente() {
        String correo = "dilan@motos.com";

        String token = jwtUtil.generateToken(correo);

        assertNotNull(token);
        assertFalse(token.trim().isEmpty());
        // Un token JWT estándar tiene 3 partes estructuradas separadas por 2 puntos (.)
        assertEquals(2, token.chars().filter(ch -> ch == '.').count());
    }

    @Test
    @DisplayName("Debe extraer el correo (subject) correctamente usando extractUsername")
    void debeExtraerUsernameCorrectamente() {
        String correoEsperado = "dilan@motos.com";
        String token = jwtUtil.generateToken(correoEsperado);

        String correoExtraido = jwtUtil.extractUsername(token);

        assertEquals(correoEsperado, correoExtraido);
    }

    @Test
    @DisplayName("Debe retornar true al validar un token con el correo correcto")
    void debeValidarTokenCorrecto() {
        String correo = "dilan@motos.com";
        String token = jwtUtil.generateToken(correo);

        Boolean esValido = jwtUtil.validateToken(token, correo);

        assertTrue(esValido);
    }

    @Test
    @DisplayName("Debe retornar false al validar un token con un correo diferente")
    void debeRechazarTokenSiCorreoNoCoincide() {
        String token = jwtUtil.generateToken("dilan@motos.com");

        Boolean esValido = jwtUtil.validateToken(token, "otro_usuario@motos.com");

        assertFalse(esValido);
    }
}