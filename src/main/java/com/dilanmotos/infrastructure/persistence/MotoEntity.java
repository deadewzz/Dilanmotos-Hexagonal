package com.dilanmotos.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "moto")
@Getter
@Setter
public class MotoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_moto")
    private Integer idMoto;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "id_marca")
    private Integer idMarca;

    @Column(name = "modelo")
    private String modelo;

    @Column(name = "cilindraje")
    private Integer cilindraje;
}
