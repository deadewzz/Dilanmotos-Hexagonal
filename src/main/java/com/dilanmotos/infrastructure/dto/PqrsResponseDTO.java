package com.dilanmotos.infrastructure.dto;

import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PqrsResponseDTO {

    private Integer id_pqrs;
    private Integer id_usuario;
    private String tipo;
    private String asunto;
    private String descripcion;
    private LocalDateTime fecha;
    private String respuesta_admin;
    private LocalDateTime fecha_respuesta;
    private String calificacion_servicio;
    private String comentario_servicio;
}