package com.dilanmotos.infrastructure.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferenciaMotoResponseDTO {

    private Integer idReferencia;
    private String nombre;
    private Integer idMarca;
}