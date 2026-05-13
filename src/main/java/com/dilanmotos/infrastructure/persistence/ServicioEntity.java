package com.dilanmotos.infrastructure.persistence;
import java.sql.Date;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "servicio")
@Getter
@Setter
public class ServicioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio")
    private Integer idServicio;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "id_mecanico")
    private Integer idMecanico;

    @Column(name = "id_tipo_servicio")
    private Integer idTipoServicio;

    @Column(name = "fecha_servicio")
    private Date fechaServicio;

    @Column(name = "estado_servicio")
    private String estadoServicio;
    
    @Column(name = "comentario")
    private String comentario;

    @Column(name = "puntuacion")
    private Integer puntuacion;

    @Column(name = "visible_en_historial")
    private Boolean visibleEnHistorial;
}
