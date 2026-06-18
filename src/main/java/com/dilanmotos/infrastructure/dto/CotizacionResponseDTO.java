package com.dilanmotos.infrastructure.dto;

import java.util.Date;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class CotizacionResponseDTO {
    private Integer idCotizacion;
    private Integer idUsuario;
    private String nombreUsuario;
    private Integer idProducto;
    private String producto;
    private Integer cantidad;   
    private double precioUnitario;
    private Date fecha;

    @JsonProperty("producto_agregado")
    private Boolean producto_agregado;

}
