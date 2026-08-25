package com.dilanmotos.infrastructure.controllers;

import com.dilanmotos.application.UseCases.ReferenciaMotoUC;
import com.dilanmotos.infrastructure.dto.ReferenciaMotoRequestDTO;
import com.dilanmotos.infrastructure.dto.ReferenciaMotoResponseDTO;
import com.dilanmotos.infrastructure.controller.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource(properties = {
        "JWT_SECRET=Da!",
        "GROQ_API_KEY=dummy_groq_key_for_testing_12345"
})
@WebMvcTest(
        value = ReferenciaMotoController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class}
)
@AutoConfigureMockMvc(addFilters = false)
class ReferenciaMotoControllerTest {

    @MockBean
    private ReferenciaMotoUC referenciaMotoUC;

    @MockBean
    private com.dilanmotos.infrastructure.Security.JwtUtil jwtUtil;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    
    private ReferenciaMotoResponseDTO respuestaDTO;

    @BeforeEach
    void setUp() {
        respuestaDTO = new ReferenciaMotoResponseDTO();
        respuestaDTO.setIdMarca(1);
        respuestaDTO.setCilindraje(200);
        respuestaDTO.setIdReferencia(1);
    }

    @Test
    @DisplayName("Debe listar todas las referencias")
    void listarReferencias() throws Exception {
        when(referenciaMotoUC.listarTodas()).thenReturn(List.of(respuestaDTO));

        mockMvc.perform(get("/api/referencias"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));

        verify(referenciaMotoUC, times(1)).listarTodas();
    }
}