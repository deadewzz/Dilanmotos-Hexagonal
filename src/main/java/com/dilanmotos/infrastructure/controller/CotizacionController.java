package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.CotizacionUC;
import com.dilanmotos.infrastructure.dto.CotizacionRequestDTO;
import com.dilanmotos.infrastructure.dto.CotizacionResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/cotizaciones")
@Tag(name = "CotizacionController", description = "Controlador para gestionar cotizaciones")
@CrossOrigin(origins = "*")
public class CotizacionController {

    private final CotizacionUC uc;

    public CotizacionController(CotizacionUC uc) {
        this.uc = uc;
    }

    @Operation(summary = "Listar todas las cotizaciones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cotizaciones listadas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cotizaciones no encontradas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<CotizacionResponseDTO>> listarTodas() {
        return ResponseEntity.ok(uc.listarTodas());
    }

    @Operation(summary = "Listar cotizaciones por usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cotizaciones listadas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cotizaciones no encontradas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<CotizacionResponseDTO>> listarPorUsuario(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(uc.listarPorUsuario(idUsuario));
    }

    @Operation(summary = "Obtener una cotización por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cotización obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cotización no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CotizacionResponseDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(uc.obtenerPorId(id));
    }

    @Operation(summary = "Crear una nueva cotización")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cotización creada exitosamente"),
            @ApiResponse(responseCode = "404", description = "La cotización no se ha creado correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<CotizacionResponseDTO> crear(@RequestBody CotizacionRequestDTO request) {
        return new ResponseEntity<>(uc.crear(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar una cotización existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cotización actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cotización no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CotizacionResponseDTO> actualizar(@PathVariable Integer id,
            @RequestBody CotizacionRequestDTO request) {
        return ResponseEntity.ok(uc.actualizar(id, request));
    }

    @Operation(summary = "Confirmar la compra de una cotización")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compra confirmada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cotización no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<CotizacionResponseDTO> confirmarCompra(@PathVariable Integer id) {
        return ResponseEntity.ok(uc.confirmarCompra(id));
    }

    @Operation(summary = "Eliminar una cotización")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cotización eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cotización no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        uc.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
