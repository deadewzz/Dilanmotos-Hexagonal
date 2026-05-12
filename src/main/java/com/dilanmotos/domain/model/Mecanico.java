package com.dilanmotos.domain.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mecanico {
    private Integer idMecanico;
    private String nombre;
    private String especialidad;
    private String telefono;

}
