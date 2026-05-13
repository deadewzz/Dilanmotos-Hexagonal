package com.dilanmotos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoServicio {

    private Integer idTipoServicio;
    private String nombre;
    private String descripcion;

}
