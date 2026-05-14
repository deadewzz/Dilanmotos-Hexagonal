package com.dilanmotos.infrastructure.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtRequestFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 1. CAPTURAR DATOS PARA DEBUG
        final String authorizationHeader = request.getHeader("Authorization");
        final String path = request.getRequestURI();
        final String method = request.getMethod();
        
        System.out.println("DEBUG: -----------------------------------------");
        System.out.println("DEBUG: Ruta solicitada: " + path + " [" + method + "]");

        // 2. EXCEPCIÓN PARA REGISTRO Y LOGIN (Permitir sin validar token)
        // Se permite el registro (POST /api/usuarios) y el login
        if (("/api/usuarios".equals(path) && "POST".equalsIgnoreCase(method)) || "/api/usuarios/login".equals(path)) {
            System.out.println("DEBUG: Ruta pública detectada, saltando validación de token.");
            chain.doFilter(request, response);
            return;
        }

        String username = null;
        String jwt = null;

        // 3. VALIDAR FORMATO DEL HEADER
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                System.out.println("DEBUG: Error al extraer usuario del token: " + e.getMessage());
            }
        } else {
            System.out.println("DEBUG: Header Authorization ausente o inválido para ruta protegida.");
        }

        // 4. ESTABLECER AUTENTICACIÓN SI EL TOKEN ES VÁLIDO
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(jwt, username)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username, null, new ArrayList<>());
                
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("DEBUG: Usuario autenticado correctamente: " + username);
            } else {
                System.out.println("DEBUG: El token no pasó la validación");
            }
        }

        // 5. PASAR AL SIGUIENTE FILTRO
        chain.doFilter(request, response);
    }
}