package com.dilanmotos.infrastructure.persistence;

import com.dilanmotos.domain.model.ChatResponse;

public interface ChatExternalPort {
    ChatResponse getAiAnswer(String question);
}