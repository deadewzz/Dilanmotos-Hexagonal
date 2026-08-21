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

import com.dilanmotos.infrastructure.persistence.CategoriaEntity;
import com.dilanmotos.infrastructure.persistence.CategoriaJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "JWT_SECRET=Da!",
        "GROQ_API_KEY=dummy_groq_key_for_testing_12345"
})
class MarcaProductoUCIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CategoriaJpaRepository categoriaJpaRepository;

     @SuppressWarnings("deprecation")
    private net.datafaker.Faker faker = new net.datafaker.Faker(new java.util.Locale("es"));

    /** Crea una categoría real en BD para satisfacer la FK id_categoria (NOT NULL en marca_producto). */
    private CategoriaEntity crearCategoriaPrueba() {
        CategoriaEntity categoria = new CategoriaEntity();
        categoria.setNombre(faker.options().option("Aceite", "Llanta", "Kit de arrastre"));
        return categoriaJpaRepository.save(categoria);
    }

    private java.util.Map<String, Object> construirPayloadMarcaProducto(Integer idCategoria, String nombre) {
        java.util.Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("nombre", nombre);
        payload.put("idCategoria", idCategoria);
        return payload;
    }

    @Test
    @DisplayName("Debe listar todas las Marcas de Producto exitosamente")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void testGetMarcasProducto() throws Exception {
        mockMvc.perform(get("/api/marcas-producto"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe obtener una Marca de Producto por ID exitosamente")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void testGetMarcaProductoById() throws Exception {
    CategoriaEntity categoria = crearCategoriaPrueba();

    java.util.Map<String, Object> payload = construirPayloadMarcaProducto(categoria.getIdCategoria(), faker.commerce().brand());

    String responseBody = mockMvc.perform(post("/api/marcas-producto")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

    Integer idCreado = objectMapper.readTree(responseBody).get("idMarcaProducto").asInt();
    
    // Verificación de seguridad
    org.junit.jupiter.api.Assertions.assertNotNull(idCreado, "El idMarcaProducto no debe ser nulo");

    mockMvc.perform(get("/api/marcas-producto/{id}", idCreado)) // Forma recomendada para construir paths con variables
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe crear una Marca de Producto exitosamente")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void testCreateMarcaProducto() throws Exception {
        CategoriaEntity categoria = crearCategoriaPrueba();

        java.util.Map<String, Object> payload = construirPayloadMarcaProducto(categoria.getIdCategoria(), faker.commerce().brand());

        mockMvc.perform(post("/api/marcas-producto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe actualizar una Marca de Producto exitosamente")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void testUpdateMarcaProducto() throws Exception {
        CategoriaEntity categoria = crearCategoriaPrueba();

        java.util.Map<String, Object> payloadCreacion = construirPayloadMarcaProducto(categoria.getIdCategoria(), faker.commerce().brand());

        String responseBody = mockMvc.perform(post("/api/marcas-producto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payloadCreacion)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer idCreado = objectMapper.readTree(responseBody).get("idMarcaProducto").asInt();

        java.util.Map<String, Object> payloadActualizacion = construirPayloadMarcaProducto(categoria.getIdCategoria(), "Marca Actualizada");

        mockMvc.perform(put("/api/marcas-producto/" + idCreado)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payloadActualizacion)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe eliminar una Marca de Producto exitosamente")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void testDeleteMarcaProducto() throws Exception {
        CategoriaEntity categoria = crearCategoriaPrueba();

        java.util.Map<String, Object> payload = construirPayloadMarcaProducto(categoria.getIdCategoria(), faker.commerce().brand());

        String responseBody = mockMvc.perform(post("/api/marcas-producto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer idCreado = objectMapper.readTree(responseBody).get("idMarcaProducto").asInt();
        mockMvc.perform(delete("/api/marcas-producto/" + idCreado))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Debe listar las Marcas de Producto filtradas por categoría")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void testGetMarcasProductoPorCategoria() throws Exception {
        CategoriaEntity categoria = crearCategoriaPrueba();

        mockMvc.perform(get("/api/marcas-producto/categoria/" + categoria.getIdCategoria()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}