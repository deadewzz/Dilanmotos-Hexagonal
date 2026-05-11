package com.dilanmotos.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para cuando no encontramos un registro en la DB.
 * Devuelve automáticamente un código 404 (Not Found).
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CaracteristicaNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CaracteristicaNotFoundException(String message) {
        super(message);
    }
}