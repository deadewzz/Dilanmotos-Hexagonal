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

import com.dilanmotos.domain.model.Usuario;
import com.dilanmotos.domain.repository.UsuarioRepository;
import com.dilanmotos.infrastructure.persistence.MecanicoEntity;
import com.dilanmotos.infrastructure.persistence.MecanicoJpaRepository;
import com.dilanmotos.infrastructure.persistence.TipoServicioEntity;
import com.dilanmotos.infrastructure.persistence.TipoServicioJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "JWT_SECRET=faker",
        "GROQ_API_KEY=dumb_api_key"
})
class ServicioUCIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private MecanicoJpaRepository mecanicoJpaRepository;
    @Autowired
    private TipoServicioJpaRepository tipoServicioJpaRepository;

    private net.datafaker.Faker faker = new net.datafaker.Faker(new java.util.Locale("es"));

    /** Crea un usuario real en BD con correo único (correo tiene UNIQUE) y contraseña válida (>= 6 caracteres). */
    private Usuario crearUsuarioPrueba() {
        Usuario usuario = new Usuario();
        usuario.setNombre(faker.name().fullName());
        usuario.setCorreo("test_" + UUID.randomUUID() + "@dilanmotos.com");
        usuario.setContrasena("test123");
        return usuarioRepository.guardar(usuario);
    }

    /** Crea un mecánico real en BD (tabla mecanico: nombre, especialidad, telefono). */
    private MecanicoEntity crearMecanicoPrueba() {
    MecanicoEntity mecanico = new MecanicoEntity();
    mecanico.setNombre(faker.name().fullName());
    mecanico.setEspecialidad("Mantenimiento general");
    mecanico.setTelefono("3136405768");
    return mecanicoJpaRepository.save(mecanico);}

    /** Crea un tipo de servicio real en BD (tabla tiposervicio: nombre, descripcion). */
    private TipoServicioEntity crearTipoServicioPrueba() {
    TipoServicioEntity tipoServicio = new TipoServicioEntity();
    tipoServicio.setNombre("Mantenimiento");
    tipoServicio.setDescripcion("Cambio de aceite, revisión general");
    return tipoServicioJpaRepository.save(tipoServicio);}

    private java.util.Map<String, Object> construirPayloadServicio(Integer idUsuario, Integer idMecanico, Integer idTipoServicio, String estado) {
        java.util.Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("idUsuario", idUsuario);
        payload.put("idMecanico", idMecanico);
        payload.put("idTipoServicio", idTipoServicio);
        payload.put("fechaServicio", "2026-08-19");
        payload.put("estadoServicio", estado);
        payload.put("comentario", faker.chuckNorris().fact());
        payload.put("puntuacion", faker.number().numberBetween(1, 5));
        payload.put("visibleEnHistorial", true);
        return payload;
    }

    @Test
    @DisplayName("Debe listar los Servicios exitosamente")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void testGetServicios() throws Exception {
        mockMvc.perform(get("/api/servicios"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe listar los Servicios por usuario exitosamente")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void testGetServiciosByUsuario() throws Exception {
        Usuario usuario = crearUsuarioPrueba();

        mockMvc.perform(get("/api/servicios/usuario/" + usuario.getIdUsuario()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe obtener un Servicio por ID exitosamente")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void testGetServicioById() throws Exception {
        Usuario usuario = crearUsuarioPrueba();
        MecanicoEntity mecanico = crearMecanicoPrueba();
        TipoServicioEntity tipoServicio = crearTipoServicioPrueba();

        java.util.Map<String, Object> payload = construirPayloadServicio(
                usuario.getIdUsuario(), mecanico.getIdMecanico(), tipoServicio.getIdTipoServicio(), "Pendiente");

        String responseBody = mockMvc.perform(post("/api/servicios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer idCreado = objectMapper.readTree(responseBody).get("idServicio").asInt();

        mockMvc.perform(get("/api/servicios/" + idCreado))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe crear un Servicio exitosamente")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void testCreateServicio() throws Exception {
        Usuario usuario = crearUsuarioPrueba();
        MecanicoEntity mecanico = crearMecanicoPrueba();
        TipoServicioEntity tipoServicio = crearTipoServicioPrueba();

        java.util.Map<String, Object> payload = construirPayloadServicio(
                usuario.getIdUsuario(), mecanico.getIdMecanico(), tipoServicio.getIdTipoServicio(), "Pendiente");

        mockMvc.perform(post("/api/servicios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe actualizar un Servicio exitosamente")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void testUpdateServicio() throws Exception {
        Usuario usuario = crearUsuarioPrueba();
        MecanicoEntity mecanico = crearMecanicoPrueba();
        TipoServicioEntity tipoServicio = crearTipoServicioPrueba();

        java.util.Map<String, Object> payloadCreacion = construirPayloadServicio(
                usuario.getIdUsuario(), mecanico.getIdMecanico(), tipoServicio.getIdTipoServicio(), "Pendiente");

        String responseBody = mockMvc.perform(post("/api/servicios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payloadCreacion)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer idCreado = objectMapper.readTree(responseBody).get("idServicio").asInt();

        java.util.Map<String, Object> payloadActualizacion = construirPayloadServicio(
                usuario.getIdUsuario(), mecanico.getIdMecanico(), tipoServicio.getIdTipoServicio(), "Completado");

        mockMvc.perform(put("/api/servicios/" + idCreado)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payloadActualizacion)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe eliminar un Servicio exitosamente")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void testDeleteServicio() throws Exception {
        Usuario usuario = crearUsuarioPrueba();
        MecanicoEntity mecanico = crearMecanicoPrueba();
        TipoServicioEntity tipoServicio = crearTipoServicioPrueba();

        java.util.Map<String, Object> payload = construirPayloadServicio(
                usuario.getIdUsuario(), mecanico.getIdMecanico(), tipoServicio.getIdTipoServicio(), "Pendiente");

        String responseBody = mockMvc.perform(post("/api/servicios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer idCreado = objectMapper.readTree(responseBody).get("idServicio").asInt();

        mockMvc.perform(delete("/api/servicios/" + idCreado))
                .andExpect(status().isNoContent());
    }
}
