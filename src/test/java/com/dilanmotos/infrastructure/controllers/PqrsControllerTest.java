package com.dilanmotos.infrastructure.controllers;

import com.dilanmotos.application.UseCases.PqrsUC;
import com.dilanmotos.infrastructure.dto.PqrsRequestDTO;
import com.dilanmotos.infrastructure.dto.PqrsResponseDTO;
import com.dilanmotos.infrastructure.controller.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@TestPropertySource(properties = {
        "JWT_SECRET=Da!",
        "GROQ_API_KEY=dummy_groq_key_for_testing_12345"
})



@WebMvcTest(
    value= PqrsController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class}
)
@AutoConfigureMockMvc(addFilters = false)
public class PqrsControllerTest {

    @MockBean
    private com.dilanmotos.infrastructure.Security.JwtUtil jwtUtil;
    @Autowired
    private MockMvc mockMvc; 
    @MockBean
    private PqrsUC pqrsUC; 
    @Autowired
    private ObjectMapper objectMapper;
    private PqrsResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new PqrsResponseDTO();
        responseDTO.setId_pqrs(1);
    }

    @Test
    @DisplayName("Debe listar PQRS")
    void listarTodas_DebeRetornarListaY200() throws Exception {
        when(pqrsUC.listarTodas()).thenReturn(List.of(responseDTO)); 

        mockMvc.perform(get("/api/pqrs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));

        verify(pqrsUC, times(1)).listarTodas();
    }

    @Test
    @DisplayName("Debe listar PQRS por usuario")
    void listarPqrs_PorUsuario() throws Exception{
        when (pqrsUC.listarPorUsuario(eq(1))).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/pqrs/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id_pqrs").value(1));

                verify(pqrsUC, times(1)).listarPorUsuario(eq(1));
    }

    @Test
@DisplayName("Debe actualizar las PQRS")
void actualizarPqrs() throws Exception {
    PqrsRequestDTO requestDTO = new PqrsRequestDTO();

    when(pqrsUC.actualizar(eq(1), any(PqrsRequestDTO.class)))
            .thenReturn(responseDTO);

    mockMvc.perform(put("/api/pqrs/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id_pqrs").value(1));

    verify(pqrsUC, times(1)).actualizar(eq(1), any(PqrsRequestDTO.class));
}

    @Test
@DisplayName("Debe eliminar las PQRS")
void eliminarPqrs() throws Exception{

    mockMvc.perform(delete("/api/pqrs/1"))
        .andExpect(status().isNoContent());

        verify(pqrsUC, times(1)).eliminar(eq(1));
    
}
}