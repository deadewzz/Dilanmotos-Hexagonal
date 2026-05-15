package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.UsuarioService;
import com.dilanmotos.domain.model.Usuario;
import com.dilanmotos.infrastructure.Security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios/login")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;

    public AuthController(AuthenticationManager authManager, UserDetailsService uds, JwtUtil jwt, UsuarioService us) {
        this.authManager = authManager;
        this.userDetailsService = uds;
        this.jwtUtil = jwt;
        this.usuarioService = us;
    }

    @PostMapping
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        String correo = request.get("correo");
        String clave = request.get("contrasena");

        authManager.authenticate(new UsernamePasswordAuthenticationToken(correo, clave));

        final UserDetails userDetails = userDetailsService.loadUserByUsername(correo);
        final String token = jwtUtil.generateToken(userDetails.getUsername());
        
        Usuario user = usuarioService.buscarPorCorreo(correo);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("id_usuario", user.getIdUsuario());
        response.put("nombre", user.getNombre());
        response.put("rol", user.getRol());
        return response;
    }
}