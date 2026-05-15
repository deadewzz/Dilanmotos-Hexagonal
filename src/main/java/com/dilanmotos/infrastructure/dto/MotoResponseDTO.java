package com.dilanmotos.infrastructure.dto;

import lombok.Data;

@Data
public class MotoResponseDTO {
    private Integer idMoto;
    private Integer idUsuario;
    private Integer idMarca;
    private String modelo;
    private Double cilindraje;
    private MarcaResponseDTO marca;
}