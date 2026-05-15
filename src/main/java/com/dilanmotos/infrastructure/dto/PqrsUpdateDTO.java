package com.dilanmotos.infrastructure.dto;

import lombok.Data;

@Data
public class PqrsUpdateDTO {
    private Integer idUsuario;
    private String estado;
    private String respuesta_admin;
}