package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.MarcaProductoUC;
import com.dilanmotos.infrastructure.dto.MarcaProductoRequestDTO;
import com.dilanmotos.infrastructure.dto.MarcaProductoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marcas-producto")
@Tag(name = "MarcaProductoController", description = "Controlador para gestionar marcas de productos")
@CrossOrigin(origins = "*")
public class MarcaProductoController {

    private final MarcaProductoUC uc;

    public MarcaProductoController(MarcaProductoUC uc) {
        this.uc = uc;
    }

    @Operation(summary = "Listar todas las marcas de productos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marcas de productos listadas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Marcas de productos no encontradas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<MarcaProductoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(uc.listarTodas());
    }

    @Operation(summary = "Obtener una marca de producto por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marca de producto obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Marca de producto no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MarcaProductoResponseDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(uc.obtenerPorId(id));
    }

    @Operation(summary = "Obtener marcas de producto por categoría")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marcas de productos obtenidas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/categoria/{idCategoria}")
    public ResponseEntity<List<MarcaProductoResponseDTO>> obtenerPorCategoria(@PathVariable Integer idCategoria) {
        return ResponseEntity.ok(uc.listarPorCategoria(idCategoria));
    }

    @Operation(summary = "Crear una nueva marca de producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Marca de producto creada exitosamente"),
            @ApiResponse(responseCode = "404", description = "La marca de producto no se ha creado correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<MarcaProductoResponseDTO> crear(@RequestBody MarcaProductoRequestDTO request) {
        return new ResponseEntity<>(uc.crear(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar una marca de producto existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marca de producto actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Marca de producto no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MarcaProductoResponseDTO> actualizar(@PathVariable Integer id, @RequestBody MarcaProductoRequestDTO request) {
        return ResponseEntity.ok(uc.actualizar(id, request));
    }

    @Operation(summary = "Eliminar una marca de producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marca de producto eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Marca de producto no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        uc.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
