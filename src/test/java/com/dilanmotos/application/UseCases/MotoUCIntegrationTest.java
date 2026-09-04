package com.dilanmotos.application.UseCases;

import com.dilanmotos.infrastructure.dto.MotoRequestDTO;
import com.dilanmotos.infrastructure.persistence.MotoEntity;
import com.dilanmotos.infrastructure.persistence.MotoJpaRepository;
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
class MotoUCIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MotoJpaRepository motoJpaRepository;

    private Faker faker;

    @BeforeEach
    void setUp() {
    faker = new Faker(Locale.forLanguageTag("es"));
    }

    @Test
    @DisplayName("Debe listar todas las motos exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testGetMotos() throws Exception {
        mockMvc.perform(get("/api/motos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe listar las motos por usuario exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testGetMotosByUser() throws Exception {
        MotoEntity moto = new MotoEntity();
        moto.setIdUsuario(1);
        moto.setIdMarca(1);
        moto.setModelo(faker.vehicle().model());
        moto.setCilindraje(150.0);
        moto = motoJpaRepository.save(moto);

        mockMvc.perform(get("/api/motos/usuario/" + moto.getIdUsuario()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe obtener una moto por ID exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testGetMotoById() throws Exception {
        MotoEntity moto = new MotoEntity();
        moto.setIdUsuario(1);
        moto.setIdMarca(1);
        moto.setModelo(faker.vehicle().model());
        moto.setCilindraje(200.0);
        moto = motoJpaRepository.save(moto);

        mockMvc.perform(get("/api/motos/" + moto.getIdMoto()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe crear una moto exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testCreateMoto() throws Exception {
        MotoRequestDTO request = new MotoRequestDTO();
        request.setIdUsuario(1);
        request.setIdMarca(1);
        request.setModelo(faker.vehicle().model());
        request.setCilindraje(250.0);

        mockMvc.perform(post("/api/motos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe actualizar una moto exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testUpdateMoto() throws Exception {
        MotoEntity moto = new MotoEntity();
        moto.setIdUsuario(1);
        moto.setIdMarca(1);
        moto.setModelo("Modelo Antiguo");
        moto.setCilindraje(125.0);
        moto = motoJpaRepository.save(moto);

        MotoRequestDTO request = new MotoRequestDTO();
        request.setIdUsuario(moto.getIdUsuario());
        request.setIdMarca(moto.getIdMarca());
        request.setModelo(faker.vehicle().model());
        request.setCilindraje(300.0);

        mockMvc.perform(put("/api/motos/" + moto.getIdMoto())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe eliminar una moto exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testDeleteMoto() throws Exception {
        MotoEntity moto = new MotoEntity();
        moto.setIdUsuario(1);
        moto.setIdMarca(1);
        moto.setModelo(faker.vehicle().model());
        moto.setCilindraje(180.0);
        moto = motoJpaRepository.save(moto);

        mockMvc.perform(delete("/api/motos/" + moto.getIdMoto()))
                .andExpect(status().isNoContent());
    }
}