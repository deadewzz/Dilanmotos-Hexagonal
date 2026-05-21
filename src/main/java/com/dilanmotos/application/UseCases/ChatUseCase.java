package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.ChatResponse;
import com.dilanmotos.infrastructure.persistence.ChatExternalPort;
import org.springframework.stereotype.Service;

@Service // ✅ esto faltaba
public class ChatUseCase {

    private final ChatExternalPort chatExternalPort;

    public ChatUseCase(ChatExternalPort chatExternalPort) {
        this.chatExternalPort = chatExternalPort;
    }

    public ChatResponse execute(String message) {
        return chatExternalPort.getAiAnswer(message);
    }
}