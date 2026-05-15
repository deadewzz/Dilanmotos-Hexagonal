package com.dilanmotos.infrastructure.dto;

import lombok.Data;

@Data
public class CaracteristicaResponseDTO {
    private Integer idCaracteristica;
    private Integer idMoto;
    private String descripcion;
    private MotoResponseDTO moto;
}