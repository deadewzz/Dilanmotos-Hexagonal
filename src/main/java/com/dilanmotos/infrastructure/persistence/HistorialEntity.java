package com.dilanmotos.infrastructure.persistence;
import java.sql.Date;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "historial")
@Getter
@Setter
public class HistorialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Integer idHistorial;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "id_servicio")
    private Integer idServicio;

    @Column(name = "accion")
    private String accion;

    @Column(name = "fecha")
    private Date fecha;

    @Column(name = "detalle")
    private String detalle;
}
