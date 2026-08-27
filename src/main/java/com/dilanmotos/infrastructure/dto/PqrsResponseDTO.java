package com.dilanmotos.infrastructure.dto;

import lombok.*;
import java.time.LocalDateTime;

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
    private String estado;
    private String respuesta_admin;
    private LocalDateTime fecha;
    private LocalDateTime fecha_respuesta;
}