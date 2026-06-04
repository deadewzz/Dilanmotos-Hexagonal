package com.dilanmotos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Producto {
    private Integer idProducto;
    private Integer idCategoria;
    private Integer idMarca;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String imagenUrl;   
    private Integer stock;
    private Boolean disponible;

    // Campos enriquecidos desde JOIN (para el prompt)
    private String nombreMarca;
    private String nombreCategoria;
}