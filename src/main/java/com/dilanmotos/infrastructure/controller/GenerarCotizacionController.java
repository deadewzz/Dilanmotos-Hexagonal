package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.GenerarCotizacionUseCase;
import com.dilanmotos.infrastructure.dto.GenerarCotizacionRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cotizacion")
@RequiredArgsConstructor
@CrossOrigin("*")
public class GenerarCotizacionController {

    private final GenerarCotizacionUseCase useCase;

    @PostMapping("/generar")
    public String generar(@RequestBody GenerarCotizacionRequestDTO request) {

        useCase.generarCotizacion(request.getDestino(), request.getNombreCliente(), request.getDetalleCotizacion(), request.getTotal());

        return "Cotización enviada correctamente";
    }
}
