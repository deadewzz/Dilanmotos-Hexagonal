package com.dilanmotos.infrastructure.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferenciaMotoDTO {

    private Integer idReferencia;
    private String nombre;
    private Integer idMarca;
}