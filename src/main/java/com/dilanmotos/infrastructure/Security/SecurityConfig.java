// package com.dilanmotos.infrastructure.Security;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import
// org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import
// org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// // ESTA ES LA IMPORTACIÓN QUE FALTA
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

// @Bean
// public PasswordEncoder passwordEncoder() {
// return new BCryptPasswordEncoder();
// }

// @Bean
// public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
// http
// .csrf(csrf -> csrf.disable()) // Desactiva CSRF para permitir POST desde
// Thunder Client [cite: 6, 16]
// .authorizeHttpRequests(auth -> auth
// .requestMatchers("/api/usuarios/**").permitAll() // Permite acceso total a
// este endpoint [cite:
// // 13]
// .anyRequest().authenticated());

// return http.build();
// }
// }