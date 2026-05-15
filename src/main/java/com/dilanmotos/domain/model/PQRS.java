package com.dilanmotos.domain.model;

import lombok.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter

public class PQRS {

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
    private String estado;
}