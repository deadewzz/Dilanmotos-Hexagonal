package com.dilanmotos.infrastructure.dto;

import lombok.Data;

@Data
public class GenerarCotizacionRequestDTO {
    private String destino;
    private String nombreCliente;
    private String detalleCotizacion;
    private Double total;
}
