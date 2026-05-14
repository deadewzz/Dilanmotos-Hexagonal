package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.domain.model.Usuario;
import com.dilanmotos.application.UseCases.UsuarioService;
import com.dilanmotos.infrastructure.Security.JwtUtil;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios/login")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;

    public AuthController(AuthenticationManager authenticationManager, 
                          UserDetailsService userDetailsService, JwtUtil jwtUtil, UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
    }

    @PostMapping
public Map<String, Object> login(@RequestBody Map<String, String> request) {
    String correo = request.get("correo");
    String contrasena = request.get("contrasena");

    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(correo, contrasena));

    final UserDetails userDetails = userDetailsService.loadUserByUsername(correo);
    final String jwt = jwtUtil.generateToken(userDetails.getUsername());

    // Buscamos al usuario real para sacar su nombre y rol
    Usuario usuario = usuarioService.obtenerPorCorreo(correo); 

    Map<String, Object> response = new HashMap<>();
    response.put("token", jwt);
    response.put("nombre", usuario.getNombre());
    response.put("rol", usuario.getRol());
    response.put("correo", usuario.getCorreo());
    
    return response;
}

@Bean
public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
}
}