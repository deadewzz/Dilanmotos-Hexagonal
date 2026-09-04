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
import com.dilanmotos.domain.model.Servicio;
import com.dilanmotos.domain.repository.UsuarioRepository;
import com.dilanmotos.domain.repository.ServicioRepository;
import com.dilanmotos.infrastructure.persistence.MecanicoEntity;
import com.dilanmotos.infrastructure.persistence.MecanicoJpaRepository;
import com.dilanmotos.infrastructure.persistence.TipoServicioEntity;
import com.dilanmotos.infrastructure.persistence.TipoServicioJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Date;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "JWT_SECRET=faker",
        "GROQ_API_KEY=dumb_api_key"
})
class HistorialUCIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ServicioRepository servicioRepository;
    @Autowired
    private MecanicoJpaRepository mecanicoJpaRepository;
    @Autowired
    private TipoServicioJpaRepository tipoServicioJpaRepository;

    @SuppressWarnings("deprecation")
    private net.datafaker.Faker faker = new net.datafaker.Faker(new java.util.Locale("es"));

    /** Crea un usuario real en BD con correo único (correo tiene UNIQUE) y contraseña válida (>= 6 caracteres). */
    private Usuario crearUsuarioPrueba() {
        Usuario usuario = new Usuario();
        usuario.setNombre(faker.name().fullName());
        usuario.setCorreo("test_" + UUID.randomUUID() + "@dilanmotos.com");
        usuario.setContrasena("test123");
        return usuarioRepository.guardar(usuario);
    }

    private MecanicoEntity crearMecanicoPrueba() {
        MecanicoEntity mecanico = new MecanicoEntity();
        mecanico.setNombre(faker.name().fullName());
        mecanico.setEspecialidad("Mantenimiento general");
        mecanico.setTelefono("3000000000");
        return mecanicoJpaRepository.save(mecanico);
    }

    private TipoServicioEntity crearTipoServicioPrueba() {
        TipoServicioEntity tipoServicio = new TipoServicioEntity();
        tipoServicio.setNombre("Mantenimiento");
        tipoServicio.setDescripcion("Cambio de aceite, revisión general");
        return tipoServicioJpaRepository.save(tipoServicio);
    }

    /** Crea un servicio real en BD para poder referenciarlo desde historial (id_servicio es FK). */
    private Servicio crearServicioPrueba(Integer idUsuario, Integer idMecanico, Integer idTipoServicio) {
        Servicio servicio = new Servicio();
        servicio.setIdUsuario(idUsuario);
        servicio.setIdMecanico(idMecanico);
        servicio.setIdTipoServicio(idTipoServicio);
        servicio.setFechaServicio(Date.valueOf("2026-08-19"));
        servicio.setEstadoServicio("Completado");
        servicio.setComentario("Servicio de prueba para historial");
        servicio.setPuntuacion(5);
        servicio.setVisibleEnHistorial(true);
        return servicioRepository.guardar(servicio);
    }

    private java.util.Map<String, Object> construirPayloadHistorial(Integer idUsuario, Integer idServicio, String accion) {
        java.util.Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("idUsuario", idUsuario);
        payload.put("idServicio", idServicio);
        payload.put("accion", accion);
        payload.put("fecha", "2026-08-19");
        payload.put("detalle", faker.chuckNorris().fact());
        return payload;
    }

    @Test
    @DisplayName("Debe listar los Historiales exitosamente")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void testGetHistoriales() throws Exception {
        mockMvc.perform(get("/api/historiales"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe obtener un Historial por ID exitosamente")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void testGetHistorialById() throws Exception {
        Usuario usuario = crearUsuarioPrueba();
        MecanicoEntity mecanico = crearMecanicoPrueba();
        TipoServicioEntity tipoServicio = crearTipoServicioPrueba();
        Servicio servicio = crearServicioPrueba(usuario.getIdUsuario(), mecanico.getIdMecanico(), tipoServicio.getIdTipoServicio());

        java.util.Map<String, Object> payload = construirPayloadHistorial(
                usuario.getIdUsuario(), servicio.getIdServicio(), "Evaluación de servicio");

        String responseBody = mockMvc.perform(post("/api/historiales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer idCreado = objectMapper.readTree(responseBody).get("idHistorial").asInt();

        mockMvc.perform(get("/api/historiales/" + idCreado))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe crear un Historial exitosamente")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void testCreateHistorial() throws Exception {
        Usuario usuario = crearUsuarioPrueba();
        MecanicoEntity mecanico = crearMecanicoPrueba();
        TipoServicioEntity tipoServicio = crearTipoServicioPrueba();
        Servicio servicio = crearServicioPrueba(usuario.getIdUsuario(), mecanico.getIdMecanico(), tipoServicio.getIdTipoServicio());

        java.util.Map<String, Object> payload = construirPayloadHistorial(
                usuario.getIdUsuario(), servicio.getIdServicio(), "Cambio de aceite");

        mockMvc.perform(post("/api/historiales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe actualizar un Historial exitosamente")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void testUpdateHistorial() throws Exception {
        Usuario usuario = crearUsuarioPrueba();
        MecanicoEntity mecanico = crearMecanicoPrueba();
        TipoServicioEntity tipoServicio = crearTipoServicioPrueba();
        Servicio servicio = crearServicioPrueba(usuario.getIdUsuario(), mecanico.getIdMecanico(), tipoServicio.getIdTipoServicio());

        java.util.Map<String, Object> payloadCreacion = construirPayloadHistorial(
                usuario.getIdUsuario(), servicio.getIdServicio(), "Cambio de aceite");

        String responseBody = mockMvc.perform(post("/api/historiales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payloadCreacion)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer idCreado = objectMapper.readTree(responseBody).get("idHistorial").asInt();

        java.util.Map<String, Object> payloadActualizacion = construirPayloadHistorial(
                usuario.getIdUsuario(), servicio.getIdServicio(), "Evaluación de servicio");

        mockMvc.perform(put("/api/historiales/" + idCreado)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payloadActualizacion)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe eliminar un Historial exitosamente")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void testDeleteHistorial() throws Exception {
        Usuario usuario = crearUsuarioPrueba();
        MecanicoEntity mecanico = crearMecanicoPrueba();
        TipoServicioEntity tipoServicio = crearTipoServicioPrueba();
        Servicio servicio = crearServicioPrueba(usuario.getIdUsuario(), mecanico.getIdMecanico(), tipoServicio.getIdTipoServicio());

        java.util.Map<String, Object> payload = construirPayloadHistorial(
                usuario.getIdUsuario(), servicio.getIdServicio(), "Cambio de aceite");

        String responseBody = mockMvc.perform(post("/api/historiales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer idCreado = objectMapper.readTree(responseBody).get("idHistorial").asInt();

        mockMvc.perform(delete("/api/historiales/" + idCreado))
                .andExpect(status().isNoContent());
    }
}