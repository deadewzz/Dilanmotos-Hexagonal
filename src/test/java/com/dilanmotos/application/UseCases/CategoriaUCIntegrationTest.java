package com.dilanmotos.application.UseCases;

import com.dilanmotos.infrastructure.dto.CategoriaRequestDTO;
import com.dilanmotos.infrastructure.persistence.CategoriaEntity;
import com.dilanmotos.infrastructure.persistence.CategoriaJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "JWT_SECRET=Da!",
        "GROQ_API_KEY=dummy_groq_key_for_testing_12345"
})
class CategoriaUCIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoriaJpaRepository categoriaJpaRepository;

    private Faker faker;

    @BeforeEach
    void setUp() {
    faker = new Faker(Locale.forLanguageTag("es"));
    }

    @Test
    @DisplayName("Debe listar todas las categorías exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testGetCategorias() throws Exception {
        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe obtener una categoría por ID exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testGetCategoriaById() throws Exception {
        CategoriaEntity entity = new CategoriaEntity();
        entity.setNombre(faker.commerce().department());
        entity = categoriaJpaRepository.save(entity);

        mockMvc.perform(get("/api/categorias/" + entity.getIdCategoria()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe crear una categoría exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testCreateCategoria() throws Exception {
        CategoriaRequestDTO request = new CategoriaRequestDTO();
        request.setNombre(faker.commerce().department());

        mockMvc.perform(post("/api/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe actualizar una categoría exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testUpdateCategoria() throws Exception {
        CategoriaEntity entity = new CategoriaEntity();
        entity.setNombre("Categoría Inicial");
        entity = categoriaJpaRepository.save(entity);

        CategoriaRequestDTO request = new CategoriaRequestDTO();
        request.setNombre(faker.commerce().department());

        mockMvc.perform(put("/api/categorias/" + entity.getIdCategoria())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe eliminar una categoría exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testDeleteCategoria() throws Exception {
        CategoriaEntity entity = new CategoriaEntity();
        entity.setNombre(faker.commerce().department());
        entity = categoriaJpaRepository.save(entity);

        mockMvc.perform(delete("/api/categorias/" + entity.getIdCategoria()))
                .andExpect(status().isNoContent());
    }
}