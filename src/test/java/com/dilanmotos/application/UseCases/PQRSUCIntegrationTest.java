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


import com.dilanmotos.domain.model.PQRS;
import com.dilanmotos.domain.model.Usuario;
import com.dilanmotos.domain.repository.PqrsRepository;
import com.dilanmotos.domain.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

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
    @Autowired
    private PqrsRepository pqrsRepository;

    @Test
    @DisplayName("Debe listar las PQRS exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testGetPQRS() throws Exception {
        mockMvc.perform(get("/api/pqrs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe listar las PQRS por usuario exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testGetPQRSByUser() throws Exception {
        mockMvc.perform(get("/api/pqrs/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe obtener una PQRS por ID exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testGetPQRSById() throws Exception {
        mockMvc.perform(get("/api/pqrs/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe crear una PQRS exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
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
                "fecha_envio", faker.date().birthday().toString());

        mockMvc.perform(post("/api/pqrs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pqrsPayload)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe actualizar una PQRS exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
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
                "fecha_envio", faker.date().birthday().toString());

        mockMvc.perform(put("/api/pqrs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pqrsPayload)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @MockBean
private PqrsRepository pqrsRepositoryMock;

@Test
@DisplayName("RF-19HU-19CP-019-04: Simulación de caída de BD debe retornar 500 Internal Server Error")
@WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
void testCreatePQRS_DatabaseFailure_ShouldFail() throws Exception {
    net.datafaker.Faker faker = new net.datafaker.Faker(new java.util.Locale("es"));

    java.util.Map<String, Object> pqrsPayload = java.util.Map.of(
            "idUsuario", 1,
            "tipo", "Peticion",
            "asunto", faker.backToTheFuture().character(),
            "descripcion", faker.chuckNorris().fact()
    );

    // Simular que el repositorio arroja una excepción de BD al guardar
    when(pqrsRepository.guardar(any(PQRS.class)))
            .thenThrow(new QueryTimeoutException("Database connection failure during transaction execution"));

    // Ejecutar endpoint con CSRF y Header de Authorization
    mockMvc.perform(post("/api/pqrs")
            .with(csrf())
            .header("Authorization", "Bearer dummy_token_for_testing")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(pqrsPayload)))
            .andExpect(status().isInternalServerError());
        }

}
