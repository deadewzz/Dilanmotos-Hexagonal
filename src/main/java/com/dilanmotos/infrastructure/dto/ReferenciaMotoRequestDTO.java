package com.dilanmotos.infrastructure.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReferenciaMotoRequestDTO {

    @NotBlank(message = "El nombre de la referencia es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String nombre;

    @NotNull(message = "El ID de la marca es obligatorio")
    private Integer idMarca;

    // ¡Agregamos el cilindraje para que no te vuelva a tirar Error 400!
    @NotNull(message = "El cilindraje es obligatorio")
    @Positive(message = "El cilindraje debe ser un número positivo")
    private Double cilindraje;
}