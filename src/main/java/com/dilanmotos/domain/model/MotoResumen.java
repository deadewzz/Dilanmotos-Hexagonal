package com.dilanmotos.domain.model;

import lombok.Data;

@Data
public class MotoResumen {
    private Integer idMoto;
    private String modelo;
    private Integer cilindraje;
    private Integer idMarca;
    private String nombreMarca;
}