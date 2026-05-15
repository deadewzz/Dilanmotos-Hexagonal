package com.dilanmotos.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    private Integer idUsuario; // CamelCase para compatibilidad con el Controller
    private String nombre;
    private String correo;
    private String contrasena;
    private String rol;
    private Integer habilitado;
}