package com.dilanmotos.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuario")
@Data // Genera automáticamente getId_usuario(), getNombre(), etc.
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id_usuario; // Cambiado para coincidir con tu DB

    private String nombre;
    private String correo;
    private String contrasena;
    private String rol;

    @Column(name = "habilitado", nullable = false)
    private Integer habilitado = 1; // Por defecto 1 (activo) para evitar el error SQL 1364
}