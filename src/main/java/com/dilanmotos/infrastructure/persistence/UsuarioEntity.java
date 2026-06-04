package com.dilanmotos.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario")
@Data
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id_usuario;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario")
    private List<MotoEntity> motos = new ArrayList<>();

    private String nombre;
    private String correo;
    private String contrasena;
    private String rol;

    @Column(name = "habilitado", nullable = false)
    private Integer habilitado = 1;

    // ← Hibernate crea estas dos columnas automáticamente al arrancar
    @Column(name = "reset_token", length = 100)
    private String resetToken;

    @Column(name = "token_expiracion", columnDefinition = "TIMESTAMP")
    private LocalDateTime tokenExpiracion;
}