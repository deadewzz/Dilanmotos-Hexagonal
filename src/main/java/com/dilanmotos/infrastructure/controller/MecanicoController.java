package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.MecanicoUC;
import com.dilanmotos.infrastructure.dto.MecanicoRequestDTO;
import com.dilanmotos.infrastructure.dto.MecanicoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mecanicos")
@Tag(name = "MecanicoController", description = "Controlador para gestionar mecanicos")
@CrossOrigin(origins = "*")
public class MecanicoController {

    private final MecanicoUC uc;

    public MecanicoController(MecanicoUC uc) {
        this.uc = uc;
    }

    @Operation(summary = "Listar todos los mecanicos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mecanicos listados exitosamente"),
            @ApiResponse(responseCode = "404", description = "Mecanicos no encontrados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<MecanicoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(uc.listarTodos());
    }

    @Operation(summary = "Obtener un mecanico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mecanico obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Mecanico no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })

    @GetMapping("/{id}")
    public ResponseEntity<MecanicoResponseDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(uc.obtenerPorId(id));
    }

    @Operation(summary = "Crear un nuevo mecanico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mecanico creado exitosamente"),
            @ApiResponse(responseCode = "404", description = "El mecanico no se ha creado correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<MecanicoResponseDTO> crear(@RequestBody MecanicoRequestDTO request) {
        return new ResponseEntity<>(uc.crear(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un mecanico existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mecanico actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Mecanico no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MecanicoResponseDTO> actualizar(@PathVariable Integer id,
            @RequestBody MecanicoRequestDTO request) {
        return ResponseEntity.ok(uc.actualizar(id, request));
    }

    @Operation(summary = "Eliminar un mecanico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mecanico eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Mecanico no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        uc.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
