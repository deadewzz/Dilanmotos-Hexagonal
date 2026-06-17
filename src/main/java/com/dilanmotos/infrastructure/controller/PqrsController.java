package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.PqrsUC;
import com.dilanmotos.infrastructure.dto.PqrsRequestDTO;
import com.dilanmotos.infrastructure.dto.PqrsResponseDTO;
import com.dilanmotos.infrastructure.dto.PqrsUpdateDTO;
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
@RequestMapping("/api/pqrs")
@Tag(name = "PqrsController", description = "Controlador para gestionar PQRS")
@CrossOrigin(origins = "*")
public class PqrsController {

    private final PqrsUC pqrsUC;

    public PqrsController(PqrsUC pqrsUC) {
        this.pqrsUC = pqrsUC;
    }

    @Operation(summary = "Listar todas las PQRS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PQRS listadas exitosamente"),
            @ApiResponse(responseCode = "404", description = "PQRS no encontradas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<PqrsResponseDTO>> listarTodas() {
        System.out.println("GET /api/pqrs - Listando todas");
        return ResponseEntity.ok(pqrsUC.listarTodas());
    }

    @Operation(summary = "Listar PQRS por usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PQRS listadas exitosamente"),
            @ApiResponse(responseCode = "404", description = "PQRS  no encontradas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<PqrsResponseDTO>> listarPorUsuario(@PathVariable Integer idUsuario) {
        System.out.println("GET /api/pqrs/usuario/" + idUsuario);
        return ResponseEntity.ok(pqrsUC.listarPorUsuario(idUsuario));
    }

    @Operation(summary = "Obtener una PQRS por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PQRS obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "PQRS no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PqrsResponseDTO> obtenerPorId(@PathVariable Integer id) {
        System.out.println("GET /api/pqrs/" + id);
        return ResponseEntity.ok(pqrsUC.obtenerPorId(id));
    }

    @Operation(summary = "Crear una nueva PQRS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "PQRS creada exitosamente"),
            @ApiResponse(responseCode = "404", description = "La PQRS no se ha creado correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<PqrsResponseDTO> crear(@Valid @RequestBody PqrsRequestDTO request) {
        System.out.println("POST /api/pqrs - Creando: " + request);
        return new ResponseEntity<>(pqrsUC.crear(request), HttpStatus.CREATED);
    }

    // Endpoint para actualización completa (admin o usuario)
    @PutMapping("/{id}")
    public ResponseEntity<PqrsResponseDTO> actualizar(@PathVariable Integer id, @RequestBody PqrsRequestDTO request) {
        System.out.println("PUT /api/pqrs/" + id);
        System.out.println("Body recibido: " + request);
        PqrsResponseDTO response = pqrsUC.actualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Actualizar PQRS (solo para admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PQRS actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "PQRS no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    // NUEVO: Endpoint específico para que el admin actualice solo estado y
    // respuesta
    @PutMapping("/admin/{id}")
    public ResponseEntity<PqrsResponseDTO> actualizarAdmin(@PathVariable Integer id,
            @RequestBody PqrsUpdateDTO request) {
        System.out.println("PUT /api/pqrs/admin/" + id);
        System.out.println("Body recibido: " + request);
        PqrsResponseDTO response = pqrsUC.actualizarAdmin(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar una PQRS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PQRS eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "PQRS no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        System.out.println("DELETE /api/pqrs/" + id);
        pqrsUC.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}