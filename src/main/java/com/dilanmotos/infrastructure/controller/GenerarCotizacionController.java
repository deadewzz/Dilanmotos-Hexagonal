package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.GenerarCotizacionUseCase;
import com.dilanmotos.infrastructure.dto.GenerarCotizacionRequestDTO;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cotizacion")
@Tag(name = "GenerarCotizacionController", description = "Controlador para generar cotizaciones")
@RequiredArgsConstructor
@CrossOrigin("*")
public class GenerarCotizacionController {

    private final GenerarCotizacionUseCase useCase;

    @Operation(summary = "Generar una cotización")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cotización generada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/generar")
    public String generar(@RequestBody GenerarCotizacionRequestDTO request) {

        useCase.generarCotizacion(request.getDestino(), request.getNombreCliente(), request.getDetalleCotizacion(),
                request.getTotal());

        return "Cotización enviada correctamente";
    }
}
