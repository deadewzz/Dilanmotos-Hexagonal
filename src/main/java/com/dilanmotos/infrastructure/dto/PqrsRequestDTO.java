package com.dilanmotos.infrastructure.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PqrsRequestDTO {

    @NotNull(message = "El ID de usuario es obligatorio")
    private Integer id_usuario;

    @NotBlank(message = "El tipo (Petición, Queja, Recurso, Sugerencia) es obligatorio")
    private String tipo;

    @NotBlank(message = "El asunto no puede estar vacío")
    private String asunto;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

}