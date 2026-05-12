package com.dilanmotos.infrastructure.dto;

import lombok.Data;

@Data
public class MotoRequestDTO {
    private Integer idUsuario;
    private Integer idMarca;
    private String modelo;
    private Double cilindraje;
}