package com.dilanmotos.infrastructure.persistence;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pqrs")
@Getter
@Setter
public class PqrsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pqrs")
    private Integer id_pqrs;
    
    @Column(name = "id_usuario")
    private Integer id_usuario;
    
    @Column(name = "tipo")
    private String tipo;
    
    @Column(name = "asunto")
    private String asunto;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "fecha")
    private LocalDateTime fecha;
    
    @Column(name = "respuesta_admin")
    private String respuesta_admin;
    
    @Column(name = "fecha_respuesta")
    private LocalDateTime fecha_respuesta;
    
    @Column(name = "calificacion_servicio")
    private String calificacion_servicio;
    
    @Column(name = "comentario_servicio")
    private String comentario_servicio;
    
    @Column(name = "estado")
    private String estado;
}