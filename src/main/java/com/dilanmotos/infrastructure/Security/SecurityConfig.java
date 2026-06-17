package com.dilanmotos.infrastructure.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. MÉTODOS Y RUTAS COMPLETAMENTE PÚBLICOS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/usuarios/login").permitAll()
                        .requestMatchers("/api/usuarios/recuperar-contrasena").permitAll()
                        .requestMatchers("/api/usuarios/resetear-contrasena").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll() // Registro de usuario
                        .requestMatchers("/api/productos/**").permitAll() // Catálogo de repuestos público
                        .requestMatchers("/error").permitAll()

                        // 2. Listas de marcas/motos públicas SOLO para lectura (GET)
                        .requestMatchers(HttpMethod.GET, "/api/marcas/**").permitAll() // Cualquiera lee las marcas
                        .requestMatchers(HttpMethod.GET, "/api/referencias/**").permitAll() // Cualquiera lee las
                                                                                            // referencias
                        .requestMatchers(HttpMethod.GET, "/api/categorias/**").permitAll() // Cualquiera lee las
                                                                                           // categorías

                        // 3. DOCUMENTACIÓN DE SWAGGER PÚBLICA
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html")
                        .permitAll()

                        // 4. RUTAS BLINDADAS (Exigen Token obligatoriamente)
                        // Nota que quitamos los 'GET' de marcas/categorías de aquí porque ya se manejan
                        // arriba.
                        // Las peticiones POST, PUT, DELETE para marcas/categorías caerán
                        // automáticamente aquí.
                        .requestMatchers(
                                "/api/usuarios/**",
                                "/api/marcas/**", // Modificar Marcas pide token
                                "/api/referencias/**", // Modificar Referencias pide token
                                "/api/categorias/**", // Modificar Categorías pide token
                                "/api/motos/**",
                                "/api/IA/**",
                                "/api/tipoServicio/**",
                                "/api/servicios/**",
                                "/api/pqrs/**",
                                "/api/mecanicos/**",
                                "/api/caracteristicas/**",
                                "/api/cotizaciones/**")
                        .authenticated()

                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autorizado");
                        }))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration
                .setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://10.0.2.2:8000", "http://10.0.2.2"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // ESTA ES LA LÍNEA CLAVE PARA QUITAR EL 401:
        // Debemos permitir explícitamente "Authorization" y "Content-Type"
        configuration.setAllowedHeaders(
                Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With", "Cache-Control"));

        // Esto permite que el navegador vea el token en la respuesta
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}