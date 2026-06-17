package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.MarcaUC;
import com.dilanmotos.infrastructure.dto.MarcaRequestDTO;
import com.dilanmotos.infrastructure.dto.MarcaResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marcas")
@Tag(name = "MarcaController", description = "Controlador para gestionar marcas de motos")
@CrossOrigin(origins = "*")
public class MarcaController {

    private final MarcaUC uc;

    public MarcaController(MarcaUC uc) {
        this.uc = uc;
    }

    @Operation(summary = "Listar todas las marcas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marcas listadas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Marcas no encontradas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<MarcaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(uc.listarTodas());
    }

    @Operation(summary = "Obtener una marca por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marca obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Marca no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MarcaResponseDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(uc.obtenerPorId(id));
    }

    @Operation(summary = "Crear una nueva marca")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Marca creada exitosamente"),
            @ApiResponse(responseCode = "404", description = "La marca no se ha creado correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<MarcaResponseDTO> crear(@RequestBody MarcaRequestDTO request) {
        return new ResponseEntity<>(uc.crear(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar una marca existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marca actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Marca no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MarcaResponseDTO> actualizar(@PathVariable Integer id, @RequestBody MarcaRequestDTO request) {
        return ResponseEntity.ok(uc.actualizar(id, request));
    }

    @Operation(summary = "Eliminar una marca")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marca eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Marca no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        uc.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
