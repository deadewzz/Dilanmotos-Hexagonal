package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.ChatUseCase;
import com.dilanmotos.domain.model.ChatResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ia")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatUseCase chatUseCase;

    public ChatController(ChatUseCase chatUseCase) {
        this.chatUseCase = chatUseCase;
    }

    @PostMapping("/consultar")
    public ResponseEntity<ChatResponse> consultar(@RequestBody ConsultaRequest request) {
        // 1. Validamos que el cuerpo o la falla no vengan vacíos
        if (request == null || request.getFalla() == null || request.getFalla().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // 2. Validamos que el idUsuario esté presente para cumplir con las reglas del UseCase
        if (request.getIdUsuario() == null) {
            return ResponseEntity.badRequest().build();
        }

        // 3. Ejecutamos el caso de uso pasando la falla y el idUsuario
        ChatResponse response = chatUseCase.execute(request.getFalla(), request.getIdUsuario());
        
        return ResponseEntity.ok(response);
    }

    // Clase estática (DTO) corregida para soportar el Payload completo de tu Frontend
    static class ConsultaRequest {
        private Integer idUsuario;
        private String motor; // Añadido para hacer match con el "motor" enviado en el JSON de React
        private String falla;

        // Getters y Setters
        public Integer getIdUsuario() { 
            return idUsuario; 
        }
        
        public void setIdUsuario(Integer idUsuario) { 
            this.idUsuario = idUsuario; 
        }

        public String getMotor() {
            return motor;
        }

        public void setMotor(String motor) {
            this.motor = motor;
        }

        public String getFalla() { 
            return falla; 
        }
        
        public void setFalla(String falla) { 
            this.falla = falla; 
        }
    }
}