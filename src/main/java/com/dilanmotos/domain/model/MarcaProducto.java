package com.dilanmotos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarcaProducto {
    private Integer idMarcaProducto;
    private String nombre;
    private Integer idCategoria;
}
