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
    private Integer idTipoDeServicio;
    private Date FechaServicio;
    private String EstadoServicio;
    private String Comentario;
    private Integer Puntuacion;
    private Boolean VisibleEnHistorial;     

}
