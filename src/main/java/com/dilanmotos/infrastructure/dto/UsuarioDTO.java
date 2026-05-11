package com.dilanmotos.infrastructure.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
    private String nombre;
    private String correo;
    private String contrasena;
}
