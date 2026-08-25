package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.ChatUseCase;
import com.dilanmotos.domain.model.ChatResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ia")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatUseCase chatUseCase;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatController(ChatUseCase chatUseCase) {
        this.chatUseCase = chatUseCase;
    }

    @PostMapping("/consultar")
    public ResponseEntity<?> consultar(@RequestBody ConsultaRequest request) {
        if (request == null || request.getFalla() == null || request.getFalla().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        if (request.getIdUsuario() == null) {
            return ResponseEntity.badRequest().build();
        }

        ChatResponse response = chatUseCase.execute(request.getFalla(), request.getIdUsuario());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recomendaciones/{idUsuario}")
    public ResponseEntity<?> recomendaciones(@PathVariable Integer idUsuario) {
        if (idUsuario == null) {
            return ResponseEntity.badRequest().build();
        }

        ChatResponse response = chatUseCase.executeRecomendaciones(idUsuario);
        
        try {
            String content = response.content();
            if (content != null && !content.isEmpty()) {
                JsonNode jsonNode = objectMapper.readTree(content);
                
                if (jsonNode.has("recomendaciones") && jsonNode.get("recomendaciones").isArray()) {
                    return ResponseEntity.ok(jsonNode);
                }
            }
            
            ObjectNode fallback = objectMapper.createObjectNode();
            fallback.putArray("recomendaciones");
            return ResponseEntity.ok(fallback);
            
        } catch (Exception e) {
            ObjectNode fallback = objectMapper.createObjectNode();
            fallback.putArray("recomendaciones");
            return ResponseEntity.ok(fallback);
        }
    }

    static class ConsultaRequest {
        private Integer idUsuario;
        private String motor;
        private String falla;

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