package com.dilanmotos.domain.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cotizacion {
    private Integer idCotizacion;
    private Integer idUsuario;
    private Integer idProducto;
    private String producto;
    private Integer cantidad;
    private double precioUnitario;
    private LocalDate fecha; 
    // true = AGREGADO/COMPRADO, false = PENDIENTE
    private Boolean producto_agregado;
}