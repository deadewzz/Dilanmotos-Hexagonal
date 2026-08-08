package com.dilanmotos.application.UseCases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import com.dilanmotos.domain.model.Usuario;
import com.dilanmotos.domain.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
"JWT_SECRET=Da!",
    "GROQ_API_KEY=dummy_groq_key_for_testing_12345"
})
class PQRSUCIntegrationTest {
    
    @Autowired 
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Debe listar las PQRS exitosamente")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"}) 
    void testGetPQRS() throws Exception {
        mockMvc.perform(get("/api/pqrs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe listar las PQRS por usuario exitosamente")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void testGetPQRSByUser() throws Exception {
        mockMvc.perform(get("/api/pqrs/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe obtener una PQRS por ID exitosamente")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void testGetPQRSById() throws Exception {
        mockMvc.perform(get("/api/pqrs/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    
    @Test 
@DisplayName("Debe crear una PQRS exitosamente")
@WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
void testCreatePQRS() throws Exception {
    // 1. Crear un usuario real en BD para satisfacer la FK
    Usuario usuarioPrueba = new Usuario();
    usuarioPrueba.setNombre("Test User");
    // ... setear los campos requeridos por tu entidad Usuario
    usuarioPrueba = usuarioRepository.guardar(usuarioPrueba);

    net.datafaker.Faker faker = new net.datafaker.Faker(new java.util.Locale("es"));

    // 2. Usar el ID del usuario persistido
    java.util.Map<String, Object> pqrsPayload = java.util.Map.of(
        "idUsuario", usuarioPrueba.getIdUsuario(),
        "tipo", faker.options().option("Petición", "Queja", "Reclamo", "Sugerencia", "fecha_envio"),
        "asunto", faker.backToTheFuture().character(),
        "descripcion", faker.chuckNorris().fact(),
        "fecha_envio", faker.date().birthday().toString()
    );

    mockMvc.perform(post("/api/pqrs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(pqrsPayload)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
}

    @Test
    @DisplayName("Debe actualizar una PQRS exitosamente")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void testUpdatePQRS() throws Exception {
        // 1. Crear un usuario real en BD para satisfacer la FK
        Usuario usuarioPrueba = new Usuario();
        usuarioPrueba.setNombre("Test User");
        // ... setear los campos requeridos por tu entidad Usuario
        usuarioPrueba = usuarioRepository.guardar(usuarioPrueba);

        net.datafaker.Faker faker = new net.datafaker.Faker(new java.util.Locale("es"));

        // 2. Usar el ID del usuario persistido
        java.util.Map<String, Object> pqrsPayload = java.util.Map.of(
            "idUsuario", usuarioPrueba.getIdUsuario(),
            "tipo", faker.options().option("Petición", "Queja", "Reclamo", "Sugerencia", "fecha_envio"),
            "asunto", faker.backToTheFuture().character(),
            "descripcion", faker.chuckNorris().fact(),
            "fecha_envio", faker.date().birthday().toString()
        );

        mockMvc.perform(put("/api/pqrs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pqrsPayload)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

}
