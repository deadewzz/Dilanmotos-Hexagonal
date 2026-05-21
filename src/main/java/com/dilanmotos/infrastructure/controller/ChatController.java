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
        if (request == null || request.getFalla() == null || request.getFalla().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        String mensaje = """
                El cliente tiene la siguiente moto: %s.
                Pregunta del cliente: %s
                """.formatted(
                    request.getMotor() != null ? request.getMotor() : "moto no especificada",
                    request.getFalla()
                );

        return ResponseEntity.ok(chatUseCase.execute(mensaje));
    }

    static class ConsultaRequest {
        private String motor;
        private String falla;

        public String getMotor() { return motor; }
        public void setMotor(String motor) { this.motor = motor; }
        public String getFalla() { return falla; }
        public void setFalla(String falla) { this.falla = falla; }
    }
}