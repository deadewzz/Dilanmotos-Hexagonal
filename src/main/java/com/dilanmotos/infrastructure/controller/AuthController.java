package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.UsuarioService;
import com.dilanmotos.domain.model.Usuario;
import com.dilanmotos.infrastructure.Security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/usuarios")
// AJUSTE: Agregamos el origen de Android Studio a nivel de controlador también
@CrossOrigin(origins = {"http://localhost:5173", "http://10.0.2.2:8080", "http://10.0.2.2"})
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;

    public AuthController(AuthenticationManager authManager,
                          UserDetailsService uds,
                          JwtUtil jwt,
                          UsuarioService us) {
        this.authManager = authManager;
        this.userDetailsService = uds;
        this.jwtUtil = jwt;
        this.usuarioService = us;
    }

    // ── Login ──────────────────────────────────────────────
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        String correo = request.get("correo");
        String clave = request.get("contrasena");

        authManager.authenticate(new UsernamePasswordAuthenticationToken(correo, clave));

        final UserDetails userDetails = userDetailsService.loadUserByUsername(correo);
        final String token = jwtUtil.generateToken(userDetails.getUsername());

        Usuario user = usuarioService.buscarPorCorreo(correo);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("id_usuario", user.getIdUsuario()); // Mapeado melo con id_usuario
        response.put("nombre", user.getNombre());
        response.put("rol", user.getRol());
        return response;
    }

    // ── Cambiar contraseña (usuario logueado) ──────────────
    @PostMapping("/cambiar-contrasena")
    public ResponseEntity<Map<String, String>> cambiarContrasena(
            @RequestBody Map<String, String> request) {

        Integer idUsuario = Integer.parseInt(request.get("idUsuario"));
        String actual = request.get("contrasenaActual");
        String nueva = request.get("contrasenaNueva");

        try {
            usuarioService.cambiarContrasena(idUsuario, actual, nueva);
            return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Solicitar recuperación (ASÍNCRONO / LIBRE DE BLOQUEO) ──
    @PostMapping("/recuperar-contrasena")
    public ResponseEntity<Map<String, String>> solicitarRecuperacion(
            @RequestBody Map<String, String> request) {

        String correo = request.get("correo");

        if (correo == null || correo.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El correo es obligatorio"));
        }

        try {

            CompletableFuture.runAsync(() -> {
                try {
                    usuarioService.solicitarRecuperacion(correo);
                    System.out.println("DEBUG ASYNC: Correo de recuperación procesado para: " + correo);
                } catch (Exception ex) {
                    System.err.println("DEBUG ASYNC ERROR: Falló el envío en segundo plano: " + ex.getMessage());
                }
            });

            return ResponseEntity.ok(Map.of(
                "mensaje", "Si el correo existe, recibirás las instrucciones"
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Resetear con el token del correo ───────────────────
    @PostMapping("/resetear-contrasena")
    public ResponseEntity<Map<String, String>> resetearContrasena(
            @RequestBody Map<String, String> request) {

        String token = request.get("token");
        String nueva = request.get("nuevaContrasena");

        try {
            usuarioService.resetearContrasena(token, nueva);
            return ResponseEntity.ok(Map.of("mensaje", "Contraseña restablecida correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}