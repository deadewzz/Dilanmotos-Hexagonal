package com.dilanmotos.domain.model;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Historial {

    private Integer idHistorial;
    private Integer idUsuario;
    private Integer idServicio;  
    private String accion;
    private Date fecha;
    private String detalle;

}
