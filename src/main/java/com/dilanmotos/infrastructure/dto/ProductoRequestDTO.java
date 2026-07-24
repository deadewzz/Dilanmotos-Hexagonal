package com.dilanmotos.infrastructure.dto;

import lombok.Data;

@Data
public class ProductoRequestDTO {
    private Integer idProducto;
    private Integer idCategoria;
    private Integer idMarca;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String imagenUrl;
    private Integer stock;
    private Boolean disponible;

    // Campos enriquecidos para React
    private String nombreCategoria;
    private String nombreMarca;
}