package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.ChatUseCase;
import com.dilanmotos.domain.model.ChatResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatUseCase chatUseCase;

    // Inyección por constructor (Spring se encarga gracias a tu BeanConfiguration)
    public ChatController(ChatUseCase chatUseCase) {
        this.chatUseCase = chatUseCase;
    }

    /**
     * Endpoint para consultar a la IA de Groq.
     * Acceso: POST http://localhost:8080/chat/ask?message=Tu consulta aquí
     */
    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> ask(@RequestParam(name = "message") String message) {
        
        // 1. Validamos que el mensaje no esté vacío
        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // 2. Llamamos al caso de uso (Capa de Aplicación)
        ChatResponse response = chatUseCase.execute(message);

        // 3. Devolvemos la respuesta con estado 200 OK
        return ResponseEntity.ok(response);
    }
}