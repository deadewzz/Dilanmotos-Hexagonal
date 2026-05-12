package com.dilanmotos.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "mecanico")
@Getter
@Setter
public class MecanicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMecanico;

    private String nombre;

    private String especialidad;

    private String telefono;
}