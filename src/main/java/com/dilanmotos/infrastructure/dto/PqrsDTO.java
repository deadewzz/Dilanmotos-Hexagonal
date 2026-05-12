package com.dilanmotos.infrastructure.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import lombok.*;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
public class PqrsDTO {

    @NotNull
    private Integer id_pqrs;
    @NotBlank
    private Integer id_usuario;
    @NotBlank
    private String tipo;
    @NotBlank
    private String asunto;
    private String descripcion;
    private LocalDateTime fecha;
    private String respuesta_admin;
    private LocalDateTime fecha_respuesta;
    private String calificacion_servicio;
    private String comentario_servicio;

}