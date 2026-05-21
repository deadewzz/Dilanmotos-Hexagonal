package com.dilanmotos.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    private Integer idUsuario;
    private String nombre;
    private String correo;
    private String contrasena;
    private String rol;
    private Integer habilitado;
    private List<MotoResumen> motos;
    private Integer idReferencia; // ✅ campo temporal para el registro inicial
}