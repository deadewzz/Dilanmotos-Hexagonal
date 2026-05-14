package com.dilanmotos.infrastructure.persistence;

import com.dilanmotos.domain.model.ChatResponse;
import com.dilanmotos.infrastructure.persistence.ChatExternalPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.List;

@Component
public class GroqAdapter implements ChatExternalPort {

    @Value("${groq.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public ChatResponse getAiAnswer(String question) {
        String url = "https://api.groq.com/openai/v1/chat/completions";

        var body = Map.of(
    "model", "llama-3.1-8b-instant", // <--- Cambia 'llama3-8b-8192' por este
    "messages", List.of(Map.of("role", "user", "content", question))
);

        var headers = new org.springframework.http.HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        var entity = new org.springframework.http.HttpEntity<>(body, headers);
        var response = restTemplate.postForObject(url, entity, Map.class);

        var choices = (List<Map<String, Object>>) response.get("choices");
        var message = (Map<String, Object>) choices.get(0).get("message");
        
        return new ChatResponse(message.get("content").toString());
    }
}