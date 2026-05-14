package com.dilanmotos.infrastructure.Security;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // 1. Definimos el nombre del esquema (puede ser cualquier nombre, pero debe ser
        // consistente)
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // 2. Le decimos a OpenAPI que use este esquema de seguridad globalmente
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                // 3. Definimos los componentes técnicos del esquema
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP) // Es seguridad a nivel de protocolo HTTP
                                        .scheme("bearer") // Indica que usaremos el esquema Bearer
                                        .bearerFormat("JWT"))); // Especificamos que el formato es JWT
    }
}
