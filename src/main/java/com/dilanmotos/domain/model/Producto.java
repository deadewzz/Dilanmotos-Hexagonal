package com.dilanmotos.domain.model;

import jakarta.persistence.Column;
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
    private String imagen_url;   
    private Integer stock;
    private Boolean disponible;

    // Campos enriquecidos desde JOIN (para el prompt)
    private String nombreMarca;
    private String nombreCategoria;
}