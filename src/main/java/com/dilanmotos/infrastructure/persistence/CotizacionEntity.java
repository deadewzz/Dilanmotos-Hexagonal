package com.dilanmotos.infrastructure.persistence;

import java.util.Date;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cotizacion")
@Getter
@Setter
public class CotizacionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cotizacion")
    private Integer idCotizacion;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "producto")
    private String producto;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "precio_unitario")
    private double precioUnitario;

    @Column(name = "fecha")
    private Date fecha;
    
    @Column(name = "producto_agregado")
    private Boolean producto_agregado;
    
}
