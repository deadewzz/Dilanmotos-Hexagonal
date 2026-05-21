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

    @PostMapping("/consultar")                          // ✅ era /ask
    public ResponseEntity<ChatResponse> consultar(@RequestBody ConsultaRequest request) { // ✅ era @RequestParam
        if (request == null || request.getFalla() == null || request.getFalla().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // Armamos el mensaje combinando moto + falla
        String mensaje = "Moto: " + request.getMotor() + ". Consulta: " + request.getFalla();
        ChatResponse response = chatUseCase.execute(mensaje);
        return ResponseEntity.ok(response);
    }

    // Clase interna para mapear el JSON del frontend
    static class ConsultaRequest {
        private String motor;
        private String falla;

        public String getMotor() { return motor; }
        public void setMotor(String motor) { this.motor = motor; }
        public String getFalla() { return falla; }
        public void setFalla(String falla) { this.falla = falla; }
    }
}