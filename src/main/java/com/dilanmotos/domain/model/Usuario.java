package com.dilanmotos.domain.model;

import lombok.Data;

@Data


public class Usuario {
    private Integer idUsuario;
    private String nombre;
    private String correo;
    private String contrasena;
    private String rol; // Agregado para manejar roles de usuario (e.g., "USER", "ADMIN")

    // 1. Constructor vacío (Esencial)
    public Usuario() {
    }

    // 2. Constructor con todos los campos (Para el Adapter)
    public Usuario(Integer idUsuario, String nombre, String correo, String contrasena, String rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    // 3. Constructor opcional (Para registros nuevos sin ID)
    public Usuario(String nombre, String correo, String contrasena, String rol) {
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
    }
}