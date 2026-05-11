package com.dilanmotos.infrastructure.dto;

import lombok.Data;

@Data
public class ProductoResponseDTO {
    private Integer idProducto;
    private String nombre;
    private Double precio;
    private String descripcion;
}