package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.Categoria;
import com.dilanmotos.domain.model.Marca;
import com.dilanmotos.domain.model.Producto;
import com.dilanmotos.domain.repository.CategoriaRepository;
import com.dilanmotos.domain.repository.MarcaRepository;
import com.dilanmotos.domain.repository.ProductoRepository;
import com.dilanmotos.infrastructure.dto.ProductoRequestDTO;
import com.dilanmotos.infrastructure.dto.ProductoResponseDTO;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "JWT_SECRET=faker",
        "GROQ_API_KEY=dumb_api_key"
})
@Transactional
class ProductoUCIntegrationTest {

    @Autowired
    private ProductoUC productoUC;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private MarcaRepository marcaRepository;

    @Test
    @DisplayName("Debe registrar y consultar un producto con su categoria y marca en la base de datos")
    void testRegistrarYConsultarProducto() {

        Faker faker = new Faker(Locale.forLanguageTag("es"));

        // 1. preparar dependecias requeridas (Categoria y marca)
        Categoria categoria = new Categoria();
        categoria.setNombre("Cascos" + faker.code().isbn10());
        Categoria categoriaGuardada = categoriaRepository.guardar(categoria);

        Marca marca = new Marca();
        marca.setNombre("Shaft" + faker.code().isbn10());
        Marca marcaGuardada = marcaRepository.guardar(marca);

        // 2. Contrumos la entidad producto
        ProductoRequestDTO producto = new ProductoRequestDTO();
        producto.setIdCategoria(categoriaGuardada.getIdCategoria());
        producto.setIdMarca(marcaGuardada.getIdMarca());
        producto.setNombre("Casco" + faker.commerce().productName());
        producto.setPrecio(Double.parseDouble(faker.commerce().price(100000, 500000).replace(',', '.')));
        producto.setStock(faker.number().numberBetween(5, 50));
        producto.setDescripcion(faker.lorem().sentence());
        producto.setImagenUrl("https://ejemplo.com/imagen.jpg");
        producto.setDisponible(true);
        // 3. Ejecutar el caso de uso (Guardar)
        ProductoResponseDTO guardado = productoUC.crear(producto);

        // 4. Verificaciones (Assertions)
        assertNotNull(guardado);
        assertNotNull(guardado.getIdProducto());

        // 5. Consultar directamente en el repositorio de persistencia
        Optional<Producto> productoEnBD = productoRepository.buscarPorId(guardado.getIdProducto());
        assertTrue(productoEnBD.isPresent());
        assertEquals(guardado.getNombre(), productoEnBD.get().getNombre());
    }

}
