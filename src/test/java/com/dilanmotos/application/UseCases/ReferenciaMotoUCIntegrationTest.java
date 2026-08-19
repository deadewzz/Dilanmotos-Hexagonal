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


import com.dilanmotos.domain.repository.ReferenciaMotoRepository;
import com.dilanmotos.infrastructure.dto.ReferenciaMotoRequestDTO;
import com.dilanmotos.domain.model.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.With;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
"JWT_SECRET=Da!",
    "GROQ_API_KEY=dummy_groq_key_for_testing_12345"
})

public class ReferenciaMotoUCIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ReferenciaMotoUC referenciaMotoUC;
    @Autowired
    private ReferenciaMotoRepository referenciaMotoRepository;

    @Test
    @DisplayName("Debe listar todas las referencias de motos exitosamente")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void testListarTodasReferencias() throws Exception {
        mockMvc.perform(get("/api/referencias"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe agregar una nueva referencia de moto exitosamente")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void testAgregarReferencia() throws Exception {
        
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1); // Asegúrate de que este ID exista en
        // la base de datos o ajusta según tu configuración de prueba.
        // Crea un objeto de referencia de moto para enviar en la solicitud
        ReferenciaMotoRequestDTO nuevaReferencia = new ReferenciaMotoRequestDTO();
        nuevaReferencia.setNombre("Referencia de prueba");
        nuevaReferencia.setIdMarca(1); // Asegúrate de que esta marca exista
        nuevaReferencia.setCilindraje(250.0);

        mockMvc.perform(post("/api/referencias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(nuevaReferencia)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Referencia de prueba"))
                .andExpect(jsonPath("$.idMarca").value(1))
                .andExpect(jsonPath("$.cilindraje").value(250.0));
    }

    @Test
    @DisplayName("Debe eliminar una referencia de moto exitosamente")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void testEliminarReferencia() throws Exception {
        // Primero, crea una referencia de moto para eliminarla
        ReferenciaMotoRequestDTO nuevaReferencia = new ReferenciaMotoRequestDTO();
        nuevaReferencia.setNombre("Referencia a eliminar");
        nuevaReferencia.setIdMarca(1);
        nuevaReferencia.setCilindraje(250.0);

        // Guardar la referencia en la base de datos
        var referenciaGuardada = referenciaMotoUC.crear(nuevaReferencia);

        // Ahora, realiza la solicitud DELETE para eliminarla
        mockMvc.perform(delete("/api/referencias/{id}", referenciaGuardada.getIdReferencia()))
                .andExpect(status().isNoContent());
    }
}
