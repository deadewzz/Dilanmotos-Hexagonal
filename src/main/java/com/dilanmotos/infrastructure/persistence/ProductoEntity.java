package com.dilanmotos.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "productos")
@Getter
@Setter
public class ProductoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(name = "id_categoria")
    private Integer idCategoria;

    @Column(name = "id_marca")
    private Integer idMarca;

    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private Double precio;

    @Column(name = "`imagenUrl`")
    private String imagenUrl;

    @Column(name = "stock", nullable = false, columnDefinition = "int default 0")
    private Integer stock = 0;

    @Column(name = "disponible", nullable = false, columnDefinition = "boolean default true")
    private Boolean disponible = true;

    // ← Relaciones para obtener nombre de marca y categoría
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_marca", insertable = false, updatable = false)
    private MarcaEntity marca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", insertable = false, updatable = false)
    private CategoriaEntity categoria;
}