package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.ProductoUC;
import com.dilanmotos.infrastructure.dto.ProductoRequestDTO;
import com.dilanmotos.infrastructure.dto.ProductoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "ProductoController", description = "Controlador para gestionar productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoUC productoUC;

    public ProductoController(ProductoUC productoUC) {
        this.productoUC = productoUC;
    }

    @Operation(summary = "Listar todos los productos")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Productos listados exitosamente"),
            @ApiResponse(responseCode = "404", description = "Productos no encontrados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listar() {
        return ResponseEntity.ok(productoUC.listarTodos());
    }

    @Operation(summary = "Obtener un producto por su ID")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Producto obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerPorId(@PathVariable Integer id) {
        // Nota: Asegúrate de que tu Caso de Uso (ProductoUC) tenga un método
        // para buscar por ID (por ejemplo, obtenerPorId o buscarPorId)
        return ResponseEntity.ok(productoUC.buscarPorId(id));
    }

    @Operation(summary = "Crear un nuevo producto")
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "404", description = "El producto no se ha creado correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crear(@RequestBody ProductoRequestDTO request) {
        return new ResponseEntity<>(productoUC.crear(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizar(@PathVariable Integer id,
            @RequestBody ProductoRequestDTO request) {
        return ResponseEntity.ok(productoUC.actualizar(id, request));
    }

    @Operation(summary = "Eliminar un producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        productoUC.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}