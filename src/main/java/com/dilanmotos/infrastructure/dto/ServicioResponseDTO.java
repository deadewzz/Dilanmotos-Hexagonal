package com.dilanmotos.infrastructure.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class ServicioResponseDTO {
    private Integer idServicio;
    private Integer idUsuario;
    private Integer idMecanico;
    private String nombreMecanico; 
    private Integer idTipoServicio;
    private String nombreServicio;
    private Date fechaServicio;
    private String estadoServicio;
    private String comentario;
    private Integer puntuacion;
    private Boolean visibleEnHistorial; 
}
