package com.dilanmotos.domain.model;

import lombok.Data;

@Data
public class MotoResumen {
    private Integer idMoto;
    private String modelo;
    private Double cilindraje;
    private Integer idMarca;
    private String nombreMarca;
}