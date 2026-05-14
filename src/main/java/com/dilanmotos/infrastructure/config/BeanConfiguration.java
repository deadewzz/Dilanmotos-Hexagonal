package com.dilanmotos.infrastructure.config;

import com.dilanmotos.application.UseCases.ChatUseCase;
import com.dilanmotos.infrastructure.persistence.ChatExternalPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public ChatUseCase chatUseCase(ChatExternalPort chatExternalPort) {
        return new ChatUseCase(chatExternalPort);
    }
}