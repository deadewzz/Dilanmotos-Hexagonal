package com.dilanmotos.application;

import com.dilanmotos.application.UseCases.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenerarCotizacionUseCase {

    private final EmailService emailService;

    public void generarCotizacion(String correo, String nombre, String detalle, Double total) {
        emailService.enviarCotizacion(
                correo,
                nombre,
                detalle,
                total
        );
    }
}
