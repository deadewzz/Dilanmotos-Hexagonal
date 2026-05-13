package com.dilanmotos.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tiposervicio")
@Getter
@Setter
public class TipoServicioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTipoServicio;

    private String nombre;
    
    private String descripcion;
}