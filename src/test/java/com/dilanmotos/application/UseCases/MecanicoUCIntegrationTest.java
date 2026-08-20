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

import com.dilanmotos.domain.repository.MecanicoRepository;
import com.dilanmotos.infrastructure.dto.MecanicoRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "JWT_SECRET=Da!",
        "GROQ_API_KEY=dummy_groq_key_for_testing_12345"
})
public class MecanicoUCIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MecanicoUC mecanicoUC;

    @Autowired
    private MecanicoRepository mecanicoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Debe listar todos los mecánicos exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testListarTodosMecanicos() throws Exception {
        mockMvc.perform(get("/api/mecanicos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe agregar un nuevo mecánico exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testAgregarMecanico() throws Exception {
        MecanicoRequestDTO nuevoMecanico = new MecanicoRequestDTO();
        nuevoMecanico.setNombre("Mecánico Test");
        nuevoMecanico.setEspecialidad("Motores"); // <-- Campo obligatorio faltante
        nuevoMecanico.setTelefono("3001234567");

        mockMvc.perform(post("/api/mecanicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoMecanico)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Mecánico Test"))
                .andExpect(jsonPath("$.especialidad").value("Motores"))
                .andExpect(jsonPath("$.telefono").value("3001234567"));
    }

    @Test
    @DisplayName("Debe eliminar un mecánico exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testEliminarMecanico() throws Exception {
        MecanicoRequestDTO nuevoMecanico = new MecanicoRequestDTO();
        nuevoMecanico.setNombre("Mecánico A Eliminar");
        nuevoMecanico.setEspecialidad("Frenos"); // <-- Agregar aquí también
        nuevoMecanico.setTelefono("3110000000");

        var mecanicoGuardado = mecanicoUC.crear(nuevoMecanico);

        mockMvc.perform(delete("/api/mecanicos/{id}", mecanicoGuardado.getIdMecanico()))
                .andExpect(status().isNoContent());
    }
}