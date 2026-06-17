package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.TipoServicioUC;
import com.dilanmotos.infrastructure.dto.TipoServicioRequestDTO;
import com.dilanmotos.infrastructure.dto.TipoServicioResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipoServicio")
@Tag(name = "TipoServicioController", description = "Controlador para gestionar tipos de servicio")
@CrossOrigin(origins = "*")
public class TipoServicioController {

    private final TipoServicioUC uc;

    public TipoServicioController(TipoServicioUC uc) {
        this.uc = uc;
    }

    @Operation(summary = "Listar todos los tipos de servicio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipos de servicio listados exitosamente")
    })
    @GetMapping
    public ResponseEntity<List<TipoServicioResponseDTO>> listarTodas() {
        return ResponseEntity.ok(uc.listarTodas());
    }

    @Operation(summary = "Obtener un tipo de servicio por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de servicio obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Tipo de servicio no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TipoServicioResponseDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(uc.obtenerPorId(id));
    }

    @Operation(summary = "Crear un nuevo tipo de servicio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tipo de servicio creado exitosamente")
    })
    @PostMapping
    public ResponseEntity<TipoServicioResponseDTO> crear(@RequestBody TipoServicioRequestDTO request) {
        return new ResponseEntity<>(uc.crear(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un tipo de servicio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de servicio actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Tipo de servicio no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TipoServicioResponseDTO> actualizar(@PathVariable Integer id,
            @RequestBody TipoServicioRequestDTO request) {
        return ResponseEntity.ok(uc.actualizar(id, request));
    }

    @Operation(summary = "Eliminar un tipo de servicio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de servicio eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Tipo de servicio no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        uc.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
