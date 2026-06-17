package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.ReferenciaMotoUC;
import com.dilanmotos.infrastructure.dto.ReferenciaMotoRequestDTO;
import com.dilanmotos.infrastructure.dto.ReferenciaMotoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/referencias")
@Tag(name = "ReferenciaMotoController", description = "Controlador para las referencias de las motos")
@CrossOrigin(origins = "*")
public class ReferenciaMotoController {

    public final ReferenciaMotoUC referenciaMotoUC;

    public ReferenciaMotoController(ReferenciaMotoUC referenciaMotoUC) {
        this.referenciaMotoUC = referenciaMotoUC;
    }

    @Operation(summary = "Listar todoas la referencias de motos o por marca")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Referencias de motos listadas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Referencias de motos no encontradas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<ReferenciaMotoResponseDTO>> listarTodas(
            @RequestParam(required = false) Integer marcaId) {

        if (marcaId != null) {
            return ResponseEntity.ok(referenciaMotoUC.listarPorMarca(marcaId));
        }
        return ResponseEntity.ok(referenciaMotoUC.listarTodas());
    }

    @Operation(summary = "Obtener una referencia de moto por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Referencia de moto obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Referencia de moto no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReferenciaMotoResponseDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(referenciaMotoUC.obtenerPorId(id));
    }

    @Operation(summary = "Crear una nueva referencia de moto")
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Referencia de moto creada exitosamente"),
            @ApiResponse(responseCode = "404", description = "La referencia de moto no se ha creado correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<ReferenciaMotoResponseDTO> crear(@Valid @RequestBody ReferenciaMotoRequestDTO request) {
        return new ResponseEntity<>(referenciaMotoUC.crear(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar una referencia de moto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Referencia de moto actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Referencia de moto no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReferenciaMotoResponseDTO> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ReferenciaMotoRequestDTO request) {
        return ResponseEntity.ok(referenciaMotoUC.actualizar(id, request));
    }

    @Operation(summary = "Eliminar una referencia de moto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Referencia de moto eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Referencia de moto no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        referenciaMotoUC.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
