package com.dilanmotos.infrastructure.dto;

import lombok.Data;

@Data
public class MarcaProductoResponseDTO {
    private Integer idMarcaProducto;
    private String nombre;
    private Integer idCategoria;
}

