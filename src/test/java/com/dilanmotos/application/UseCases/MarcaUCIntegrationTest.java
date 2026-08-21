package com.dilanmotos.application.UseCases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.dilanmotos.infrastructure.dto.MarcaRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "JWT_SECRET=Da!",
        "GROQ_API_KEY=dummy_groq_key_for_testing_12345"
})
public class MarcaUCIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MarcaUC marcaUC;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Debe listar todas las marcas exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testListarTodasMarcas() throws Exception {
        mockMvc.perform(get("/api/marcas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe agregar una nueva marca exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testAgregarMarca() throws Exception {
        MarcaRequestDTO nuevaMarca = new MarcaRequestDTO();
        nuevaMarca.setNombre("Yamaha");

        mockMvc.perform(post("/api/marcas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevaMarca)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Yamaha"));
    }

    @Test
    @DisplayName("Debe eliminar una marca exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testEliminarMarca() throws Exception {
        MarcaRequestDTO nuevaMarca = new MarcaRequestDTO();
        nuevaMarca.setNombre("Marca A Eliminar");

        var marcaGuardada = marcaUC.crear(nuevaMarca);

        mockMvc.perform(delete("/api/marcas/{id}", marcaGuardada.getIdMarca()))
                .andExpect(status().isNoContent());
    }
}