package com.dilanmotos.infrastructure.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PqrsRequestDTO {
    @NotNull
    private Integer idUsuario;
    
    @NotBlank
    private String tipo;
    
    @NotBlank
    private String asunto;
    
    @NotBlank
    private String descripcion;
    
    private String estado;
    
    private String respuesta_admin;
}