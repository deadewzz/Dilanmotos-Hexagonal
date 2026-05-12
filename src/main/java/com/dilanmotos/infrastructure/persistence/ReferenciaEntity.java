package com.dilanmotos.infrastructure.persistence;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "referencias")
@Getter @Setter              
@NoArgsConstructor
@AllArgsConstructor
public class ReferenciaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_referencia")
    private Integer idReferencia;

    @Column(name = "nombre_referencia") 
        private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_marca")  
    @JsonIgnoreProperties("referencias")
    private MarcaEntity marca;       
}