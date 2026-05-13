package com.dilanmotos.infrastructure.persistence;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pqrs")
@Getter
@Setter
public class PqrsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
