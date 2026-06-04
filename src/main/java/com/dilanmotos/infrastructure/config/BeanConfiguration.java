package com.dilanmotos.infrastructure.config;

import com.dilanmotos.application.UseCases.ChatUseCase;
import com.dilanmotos.domain.repository.MarcaRepository;
import com.dilanmotos.domain.repository.MotoRepository;
import com.dilanmotos.infrastructure.persistence.ChatExternalPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public ChatUseCase chatUseCase(ChatExternalPort chatExternalPort,
                                   MotoRepository motoRepository,
                                   MarcaRepository marcaRepository) {
        return new ChatUseCase(chatExternalPort, motoRepository, marcaRepository);
    }
}