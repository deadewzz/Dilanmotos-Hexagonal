package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.HistorialUC;
import com.dilanmotos.infrastructure.dto.HistorialRequestDTO;
import com.dilanmotos.infrastructure.dto.HistorialResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historiales")
@Tag(name = "HistorialController", description = "Controlador para gestionar historiales de servicios")
@CrossOrigin(origins = "*")
public class HistorialController {

    private final HistorialUC uc;

    public HistorialController(HistorialUC uc) {
        this.uc = uc;
    }

    @Operation(summary = "Listar todos los historiales de servicios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historiales listados exitosamente"),
            @ApiResponse(responseCode = "404", description = "Historiales no encontrados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<HistorialResponseDTO>> listarTodas() {
        return ResponseEntity.ok(uc.listarTodas());
    }

    @Operation(summary = "Obtener un historial de servicio por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Historial no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<HistorialResponseDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(uc.obtenerPorId(id));
    }

    @Operation(summary = "Crear un nuevo historial de servicio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Historial creado exitosamente"),
            @ApiResponse(responseCode = "404", description = "El historial no se ha creado correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<HistorialResponseDTO> crear(@RequestBody HistorialRequestDTO request) {
        return new ResponseEntity<>(uc.crear(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un historial de servicio existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Historial no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<HistorialResponseDTO> actualizar(@PathVariable Integer id,
            @RequestBody HistorialRequestDTO request) {
        return ResponseEntity.ok(uc.actualizar(id, request));
    }

    @Operation(summary = "Eliminar un historial de servicio por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Historial no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        uc.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
