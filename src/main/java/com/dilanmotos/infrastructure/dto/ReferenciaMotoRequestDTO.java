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
}