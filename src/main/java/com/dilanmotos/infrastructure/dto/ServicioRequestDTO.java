package com.dilanmotos.infrastructure.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class ServicioRequestDTO {
    private Integer idServicio;
    private Integer idUsuario;
    private Integer idMecanico;
    private Integer idTipoServicio;
    private Date fechaServicio;
    private String estadoServicio;
    private String comentario;
    private Integer puntuacion;
    private Boolean visibleEnHistorial; 
}
