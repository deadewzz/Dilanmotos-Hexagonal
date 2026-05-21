package com.dilanmotos.infrastructure.persistence;

import com.dilanmotos.domain.model.ChatResponse;
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

        String systemPrompt = "Eres el mecánico jefe de Dilan Motos, un taller en Bogotá, Colombia. " +
            "Solo manejamos tres categorías de productos: aceites/lubricantes, llantas y kits de arrastre. " +
            "No hables de nada que no tenga que ver con motos o estos productos.\n\n" +

            "FORMATO: Usa tablas Markdown cuando compares precios o repuestos:\n" +
            "| Producto | Precio |\n" +
            "| :--- | :--- |\n\n" +

            "PRECIOS:\n" +
            "- NUNCA inventes precios. Si no sabes el precio real, di claramente que no lo tienes.\n" +
            "- En Colombia 220 significa 220.000 pesos.\n" +
            "- Verifica precios reales en Pirelli, Michelin, Corsa, Motul, Castrol, etc.\n" +
            "- Da precios específicos para el modelo de moto del cliente, no precios genéricos.\n" +
            "- Si no tenemos el producto, avisa y da un precio aproximado aclarando que puede variar.\n\n" +

            "PRODUCTOS:\n" +
            "- Llantas: usa marcas como Michelin, Pirelli, Corsa, IRC. NO uses el nombre de la marca de la moto.\n" +
            "- Kits de arrastre: usa DID, RK, Regina, Vortex, JT Sprockets. NO menciones escapes ni frenos.\n" +
            "- Aceites: Motul, Castrol, Shell Advance, Mobil. Si la marca de la moto tiene aceite oficial, menciónalo primero.\n" +
            "- Siempre da 3 opciones por categoría con nombre real y precio real.\n\n" +

            "INFORMACIÓN DE MOTO:\n" +
            "- Cuando el cliente pregunte qué moto tiene, da modelo, cilindraje y marca completos.\n" +
            "- Siempre adapta las recomendaciones al modelo específico del cliente.\n\n" +

            "TONO: Habla como mecánico colombiano, cercano y directo. Usa 'parcero' ocasionalmente.";

        var body = Map.of(
            "model", "llama-3.3-70b-versatile", // ✅ modelo más capaz
            "messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", question)
            ),
            "temperature", 0.1
        );

        var headers = new org.springframework.http.HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        var entity = new org.springframework.http.HttpEntity<>(body, headers);

        try {
            var response = restTemplate.postForObject(url, entity, Map.class);
            var choices = (List<Map<String, Object>>) response.get("choices");
            var message = (Map<String, Object>) choices.get(0).get("message");
            return new ChatResponse(message.get("content").toString());
        } catch (Exception e) {
            return new ChatResponse("Error: El mecánico jefe no pudo responder. Intenta de nuevo.");
        }
    }
}   