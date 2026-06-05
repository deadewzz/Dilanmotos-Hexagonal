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

        if (request.getIdUsuario() == null) {
            return ResponseEntity.badRequest().build();
        }

        // ← ya no construyes el String "mensaje" aquí, solo pasas falla e idUsuario
        return ResponseEntity.ok(chatUseCase.execute(request.getFalla(), request.getIdUsuario()));
    }

    static class ConsultaRequest {
        private Integer idUsuario;
        private String falla;

        public Integer getIdUsuario() { return idUsuario; }
        public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
        public String getFalla() { return falla; }
        public void setFalla(String falla) { this.falla = falla; }
    }
}