package com.dilanmotos.infrastructure.dto;

import lombok.Data;

@Data
public class MecanicoRequestDTO {
    private Integer idMecanico;
    private String nombre;
    private String especialidad;
    private String telefono;
    
}