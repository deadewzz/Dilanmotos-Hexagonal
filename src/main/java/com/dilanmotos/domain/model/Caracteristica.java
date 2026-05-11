package com.dilanmotos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Caracteristica {
    private Integer idCaracteristica;
    private Integer idMoto;
    private String descripcion;
}