package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.CaracteristicaUC;
import com.dilanmotos.infrastructure.dto.CaracteristicaRequestDTO;
import com.dilanmotos.infrastructure.dto.CaracteristicaResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/caracteristicas")
@Tag(name = "CaracteristicaController", description = "Controlador para gestionar características")
@CrossOrigin(origins = "*")

public class CaracteristicaController {

    private final CaracteristicaUC uc;

    public CaracteristicaController(CaracteristicaUC uc) {
        this.uc = uc;
    }

    @Operation(summary = "Listar todas las características")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Características listadas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Características no encontradas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    // GET para listar TODO
    @GetMapping
    public ResponseEntity<List<CaracteristicaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(uc.listarTodas());
    }

    @Operation(summary = "Obtener una característica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Característica obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Característica no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CaracteristicaResponseDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(uc.obtenerPorId(id));
    }

    @Operation(summary = "Listar características por moto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Características listadas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Características no encontradas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/moto/{idMoto}")
    public ResponseEntity<List<CaracteristicaResponseDTO>> listarPorMoto(@PathVariable Integer idMoto) {
        return ResponseEntity.ok(uc.listarPorMoto(idMoto));
    }

    @PostMapping
    public ResponseEntity<CaracteristicaResponseDTO> crear(@RequestBody CaracteristicaRequestDTO request) {
        return ResponseEntity.ok(uc.agregar(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        uc.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}