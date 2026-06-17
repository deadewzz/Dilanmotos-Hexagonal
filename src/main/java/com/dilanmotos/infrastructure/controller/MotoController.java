package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.MotoUC;
import com.dilanmotos.infrastructure.dto.MotoRequestDTO;
import com.dilanmotos.infrastructure.dto.MotoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/motos")
@Tag(name = "MotoController", description = "Controlador para gestionar motos")
@CrossOrigin(origins = "*")
public class MotoController {

    private final MotoUC uc;

    public MotoController(MotoUC uc) {
        this.uc = uc;
    }

    @Operation(summary = "Listar motos por usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Motos listadas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Motos no encontradas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<MotoResponseDTO>> listarPorUsuario(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(uc.listarPorUsuario(idUsuario));
    }

    @Operation(summary = "Listar todas las motos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Motos listadas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Motos no encontradas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<MotoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(uc.listarTodas());
    }

    @Operation(summary = "Obtener una moto por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Moto obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Moto no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MotoResponseDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(uc.obtenerPorId(id));
    }

    @Operation(summary = "Crear una nueva moto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Moto creada exitosamente"),
            @ApiResponse(responseCode = "404", description = "La moto no se ha creado correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<MotoResponseDTO> crear(@RequestBody MotoRequestDTO request) {
        return new ResponseEntity<>(uc.crear(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar una moto existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Moto actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Moto no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MotoResponseDTO> actualizar(@PathVariable Integer id, @RequestBody MotoRequestDTO request) {
        return ResponseEntity.ok(uc.actualizar(id, request));
    }

    @Operation(summary = "Eliminar una moto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Moto eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Moto no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        uc.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
