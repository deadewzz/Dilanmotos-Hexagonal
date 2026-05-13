package com.dilanmotos.domain.model;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Servicio {

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
