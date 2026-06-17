package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.ServicioUC;
import com.dilanmotos.infrastructure.dto.ServicioRequestDTO;
import com.dilanmotos.infrastructure.dto.ServicioResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
@Tag(name = "ServicioController", description = "Controlador para gestionar servicios")
@CrossOrigin(origins = "*")
public class ServicioController {

    private final ServicioUC uc;

    public ServicioController(ServicioUC uc) {
        this.uc = uc;
    }

    @Operation(summary = "Listar todos los servicios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Servicios listados exitosamente"),
            @ApiResponse(responseCode = "404", description = "Servicios no encontrados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<ServicioResponseDTO>> listarTodas() {
        return ResponseEntity.ok(uc.listarTodas());
    }

    @Operation(summary = "Listar servicios por usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Servicios listados exitosamente"),
            @ApiResponse(responseCode = "404", description = "Servicios no encontrados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ServicioResponseDTO>> listarPorUsuario(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(uc.listarPorUsuario(idUsuario));
    }

    @Operation(summary = "Obtener un servicio por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Servicio obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Servicio no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ServicioResponseDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(uc.obtenerPorId(id));
    }

    @Operation(summary = "Crear un nuevo servicio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Servicio creado exitosamente"),
            @ApiResponse(responseCode = "404", description = "El servicio no se ha creado correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<ServicioResponseDTO> crear(@RequestBody ServicioRequestDTO request) {
        return new ResponseEntity<>(uc.crear(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un servicio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Servicio actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Servicio no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ServicioResponseDTO> actualizar(@PathVariable Integer id,
            @RequestBody ServicioRequestDTO request) {
        return ResponseEntity.ok(uc.actualizar(id, request));
    }

    @Operation(summary = "Eliminar un servicio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Servicio eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Servicio no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        uc.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
