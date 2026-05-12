package com.dilanmotos.infrastructure.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class HistorialRequestDTO {
    private Integer idHistorial;
    private Integer idUsuario;
    private Integer idServicio;
    private String accion;
    private Date fecha;
    private String detalle;
}
