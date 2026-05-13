package com.dilanmotos.infrastructure.dto;

import java.util.Date;
import lombok.Data;

@Data
public class CotizacionRequestDTO {
    private Integer idCotizacion;
    private Integer idUsuario;
    private String producto;
    private Integer cantidad;
    private double precioUnitario;
    private Date fecha;
    private Boolean producto_agregado;
    
}
