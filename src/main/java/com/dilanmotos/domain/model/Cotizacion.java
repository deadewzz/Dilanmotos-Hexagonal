package com.dilanmotos.domain.model;


import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cotizacion {
    private Integer idCotizacion;
    private Integer idUsuario;
    private String producto;
    private Integer cantidad;
    private double precioUnitario;
    private Date fecha;
    private Boolean producto_agregado;
    
}
