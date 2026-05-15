package com.dilanmotos.infrastructure.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PqrsResponseDTO {
    private Integer id_pqrs; // Cambiado a snake_case para coincidir con tu BD y Front
    private Integer id_usuario;
    private String tipo;
    private String asunto;
    private String descripcion;
    private String estado;
    private String respuesta_admin;
    private LocalDateTime fecha;
    private LocalDateTime fecha_respuesta;
}