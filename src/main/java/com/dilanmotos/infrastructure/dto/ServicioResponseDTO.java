package com.dilanmotos.infrastructure.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class ServicioResponseDTO {
    private Integer idServicio;
    private Integer idUsuario;
    private Integer idMecanico;
    private Integer idTipoDeServicio;
    private Date FechaServicio;
    private String EstadoServicio;
    private String Comentario;
    private Integer Puntuacion;
    private Boolean VisibleEnHistorial; 
}
