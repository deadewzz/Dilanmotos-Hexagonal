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

import com.dilanmotos.domain.repository.TipoServicioRepository;
import com.dilanmotos.infrastructure.dto.TipoServicioRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "JWT_SECRET=Da!",
        "GROQ_API_KEY=dummy_groq_key_for_testing_12345"
})
public class TipoServicioUCIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TipoServicioUC tipoServicioUC;

    @Autowired
    private TipoServicioRepository tipoServicioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Debe listar todos los tipos de servicio exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testListarTodosTiposServicio() throws Exception {
        mockMvc.perform(get("/api/tipoServicio"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe agregar un nuevo tipo de servicio exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testAgregarTipoServicio() throws Exception {
        TipoServicioRequestDTO nuevoTipo = new TipoServicioRequestDTO();
        nuevoTipo.setNombre("Mantenimiento General");
        nuevoTipo.setDescripcion("Revisión preventiva de frenos, motor y sistema eléctrico");

        mockMvc.perform(post("/api/tipoServicio")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoTipo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Mantenimiento General"))
                .andExpect(jsonPath("$.descripcion").value("Revisión preventiva de frenos, motor y sistema eléctrico"));
    }

    @Test
    @DisplayName("Debe eliminar un tipo de servicio exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testEliminarTipoServicio() throws Exception {
        TipoServicioRequestDTO nuevoTipo = new TipoServicioRequestDTO();
        nuevoTipo.setNombre("Alineación");
        nuevoTipo.setDescripcion("Alineación y balanceo de llantas");

        var tipoGuardado = tipoServicioUC.crear(nuevoTipo);

        mockMvc.perform(delete("/api/tipoServicio/{id}", tipoGuardado.getIdTipo()))
                .andExpect(status().isNoContent());
    }
}