package com.dilanmotos.infrastructure.dto;

import lombok.Data;

@Data
public class ProductoRequestDTO {
    private Integer idCategoria;
    private Integer idMarca;
    private String nombre;
    private String descripcion;
    private Double precio;
}