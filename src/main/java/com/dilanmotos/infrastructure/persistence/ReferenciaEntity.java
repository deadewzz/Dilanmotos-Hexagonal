package com.dilanmotos.infrastructure.persistence;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "referencia_motos")
@Getter @Setter              
@NoArgsConstructor
@AllArgsConstructor
public class ReferenciaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_referencia")
    private Integer idReferencia;

    @Column(name = "nombre") 
        private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_marca")  
    @JsonIgnoreProperties("referencias")
    private MarcaEntity marca;       
}