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

        // 1. CAPTURAR EL HEADER PARA DEBUG
        final String authorizationHeader = request.getHeader("Authorization");
        
        System.out.println("DEBUG: -----------------------------------------");
        System.out.println("DEBUG: Ruta solicitada: " + request.getRequestURI());
        System.out.println("DEBUG: Header Authorization recibido: " + authorizationHeader);

        String username = null;
        String jwt = null;

        // 2. VALIDAR FORMATO
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                System.out.println("DEBUG: Error al extraer usuario del token: " + e.getMessage());
            }
        } else {
            System.out.println("DEBUG: El header no contiene un token válido (Bearer)");
        }

        // 3. ESTABLECER AUTENTICACIÓN
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

        // 4. PASAR AL SIGUIENTE FILTRO
        chain.doFilter(request, response);
    }
}