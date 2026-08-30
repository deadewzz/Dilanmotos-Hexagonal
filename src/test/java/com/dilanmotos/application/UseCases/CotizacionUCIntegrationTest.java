package com.dilanmotos.application.UseCases;

import com.dilanmotos.infrastructure.dto.CotizacionRequestDTO;
import com.dilanmotos.infrastructure.persistence.CategoriaEntity;
import com.dilanmotos.infrastructure.persistence.CategoriaJpaRepository;
import com.dilanmotos.infrastructure.persistence.CotizacionEntity;
import com.dilanmotos.infrastructure.persistence.CotizacionJpaRepository;
import com.dilanmotos.infrastructure.persistence.MarcaProductoEntity;
import com.dilanmotos.infrastructure.persistence.MarcaProductoJpaRepository;
import com.dilanmotos.infrastructure.persistence.ProductoEntity;
import com.dilanmotos.infrastructure.persistence.ProductoJpaRepository;
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

import java.util.Date;
import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "JWT_SECRET=Da!",
        "GROQ_API_KEY=dummy_groq_key_for_testing_12345"
})
class CotizacionUCIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CotizacionJpaRepository cotizacionJpaRepository;

    @Autowired
    private ProductoJpaRepository productoJpaRepository;

    @Autowired
    private MarcaProductoJpaRepository marcaProductoJpaRepository;

    @Autowired
    private CategoriaJpaRepository categoriaJpaRepository;

    private Faker faker;
    private Integer idProductoValido;

    @BeforeEach
    void setUp() {
        faker = new Faker(Locale.forLanguageTag("es"));

        // 1. Crear e insertar la marca previa necesaria
        CategoriaEntity categoria = new CategoriaEntity();
        categoria.setNombre("Cat " + faker.number().digits(4));
        categoria = categoriaJpaRepository.save(categoria);

        // 2. Crear e insertar el producto asociado a la marca
        MarcaProductoEntity marca = new MarcaProductoEntity();
        marca.setNombre("Marca " + faker.number().digits(4));
        marca.setCategoria(categoria);
        marca = marcaProductoJpaRepository.save(marca);

        // 3. Crear e insertar Producto
        ProductoEntity producto = new ProductoEntity();
        producto.setNombre("Prod " + faker.number().digits(4));
        producto.setPrecio(100000.0);
        producto.setStock(50);
        producto.setDisponible(true);
        producto.setMarca(marca);
        producto = productoJpaRepository.save(producto);

        this.idProductoValido = producto.getIdProducto();
    }

    @Test
    @DisplayName("Debe listar todas las cotizaciones exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testGetCotizaciones() throws Exception {
        mockMvc.perform(get("/api/cotizaciones"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe listar cotizaciones por usuario exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testGetCotizacionesByUser() throws Exception {
        CotizacionEntity entity = new CotizacionEntity();
        entity.setIdUsuario(1);
        entity.setIdProducto(idProductoValido);
        entity.setProducto("Producto Test");
        entity.setCantidad(2);
        entity.setPrecioUnitario(50000.0);
        entity.setFecha(new Date());
        entity.setProducto_agregado(false);
        entity = cotizacionJpaRepository.save(entity);

        mockMvc.perform(get("/api/cotizaciones/usuario/" + entity.getIdUsuario()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe obtener una cotización por ID exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testGetCotizacionById() throws Exception {
        CotizacionEntity entity = new CotizacionEntity();
        entity.setIdUsuario(1);
        entity.setIdProducto(idProductoValido);
        entity.setProducto("Producto Test");
        entity.setCantidad(1);
        entity.setPrecioUnitario(120000.0);
        entity.setFecha(new Date());
        entity.setProducto_agregado(false);
        entity = cotizacionJpaRepository.save(entity);

        mockMvc.perform(get("/api/cotizaciones/" + entity.getIdCotizacion()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe crear una cotización exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testCreateCotizacion() throws Exception {
        CotizacionRequestDTO request = new CotizacionRequestDTO();
        request.setIdUsuario(1);
        request.setIdProducto(idProductoValido);
        request.setProducto(faker.commerce().productName());
        request.setCantidad(3);
        request.setPrecioUnitario(45000.0);
        request.setFecha(new Date());
        request.setProducto_agregado(false);

        mockMvc.perform(post("/api/cotizaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe actualizar una cotización exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testUpdateCotizacion() throws Exception {
        CotizacionEntity entity = new CotizacionEntity();
        entity.setIdUsuario(1);
        entity.setIdProducto(idProductoValido);
        entity.setProducto("Casco Inicial");
        entity.setCantidad(1);
        entity.setPrecioUnitario(100000.0);
        entity.setFecha(new Date());
        entity.setProducto_agregado(false);
        entity = cotizacionJpaRepository.save(entity);

        CotizacionRequestDTO request = new CotizacionRequestDTO();
        request.setIdUsuario(entity.getIdUsuario());
        request.setIdProducto(idProductoValido);
        request.setProducto(faker.commerce().productName());
        request.setCantidad(5);
        request.setPrecioUnitario(150000.0);
        request.setFecha(new Date());
        request.setProducto_agregado(false);

        mockMvc.perform(put("/api/cotizaciones/" + entity.getIdCotizacion())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe confirmar la compra de una cotización exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testConfirmarCompra() throws Exception {
        CotizacionEntity entity = new CotizacionEntity();
        entity.setIdUsuario(1);
        entity.setIdProducto(idProductoValido);
        entity.setProducto("Producto Test");
        entity.setCantidad(1);
        entity.setPrecioUnitario(30000.0);
        entity.setFecha(new Date());
        entity.setProducto_agregado(false);
        entity = cotizacionJpaRepository.save(entity);

        mockMvc.perform(put("/api/cotizaciones/" + entity.getIdCotizacion() + "/confirmar"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe eliminar una cotización exitosamente")
    @WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
    void testDeleteCotizacion() throws Exception {
        CotizacionEntity entity = new CotizacionEntity();
        entity.setIdUsuario(1);
        entity.setIdProducto(idProductoValido);
        entity.setProducto("Producto Test");
        entity.setCantidad(1);
        entity.setPrecioUnitario(20000.0);
        entity.setFecha(new Date());
        entity.setProducto_agregado(false);
        entity = cotizacionJpaRepository.save(entity);

        mockMvc.perform(delete("/api/cotizaciones/" + entity.getIdCotizacion()))
                .andExpect(status().isNoContent());
    }
}