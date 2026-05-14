package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.model.ChatResponse;
import com.dilanmotos.infrastructure.persistence.ChatExternalPort;

public class ChatUseCase {
    private final ChatExternalPort chatExternalPort;

    public ChatUseCase(ChatExternalPort chatExternalPort) {
        this.chatExternalPort = chatExternalPort;
    }

    public ChatResponse execute(String message) {
        // Aquí podrías validar si el mensaje tiene que ver con motos
        return chatExternalPort.getAiAnswer(message);
    }
}