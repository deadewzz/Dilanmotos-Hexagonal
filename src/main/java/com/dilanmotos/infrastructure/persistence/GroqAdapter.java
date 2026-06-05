package com.dilanmotos.infrastructure.persistence;

import com.dilanmotos.domain.model.ChatResponse;
import com.dilanmotos.domain.model.Producto;
import com.dilanmotos.domain.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GroqAdapter implements ChatExternalPort {

    @Value("${groq.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ProductoRepository productoRepository;

    public GroqAdapter(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public ChatResponse getAiAnswer(String question, String nombreMarca,
                                    String modelo, double cilindraje) {

        // 1. Traer todos los productos de la BD
        List<Producto> productos = productoRepository.obtenerTodos();

        // 2. Construir el catálogo como texto
        String catalogo = buildCatalogo(productos);

        // 3. Construir el prompt con moto + catálogo
        String systemPrompt = buildSystemPrompt(catalogo, nombreMarca, modelo, cilindraje);

        String url = "https://api.groq.com/openai/v1/chat/completions";

        var body = Map.of(
            "model", "llama-3.3-70b-versatile",
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

    private String buildCatalogo(List<Producto> productos) {
    if (productos.isEmpty()) {
        return "No hay productos disponibles en el inventario.";
    }

    Map<String, List<Producto>> porCategoria = productos.stream()
        .collect(Collectors.groupingBy(p ->
            p.getNombreCategoria() != null ? p.getNombreCategoria() : "General"
        ));

    StringBuilder sb = new StringBuilder();
    porCategoria.forEach((categoria, items) -> {
        sb.append("\n[").append(categoria.toUpperCase()).append("]\n");
        items.forEach(p -> sb.append(String.format(
            "  • %s | Precio: $%,.0f COP | %s\n",
            p.getNombre(),
            p.getPrecio(),
            p.getDescripcion() != null ? p.getDescripcion() : ""
        )));
    });

    return sb.toString();
}

    private String buildSystemPrompt(String catalogo, String marca,
                                     String modelo, double cilindraje) {
        return String.format("""
            Eres el mecánico jefe de Dilan Motos, un taller en Bogotá, Colombia.
            El cliente tiene una %s %s de %.0fcc.

            ╔══════════════════════════════════════════════╗
            REGLA ABSOLUTA: SOLO puedes recomendar productos
            que estén en el INVENTARIO que aparece abajo.
            NO inventes productos ni precios.
            Si no hay opciones en alguna categoría, dilo claramente.
            ╚══════════════════════════════════════════════╝

            INVENTARIO ACTUAL DE DILAN MOTOS:
            %s

            INSTRUCCIONES:
            - Recomienda máximo 3 productos por categoría, los más adecuados para la moto del cliente.
            - Usa SIEMPRE el precio exacto del inventario.
            - Usa tablas Markdown para comparar:
              | Producto |  Precio |
              | :--- | :--- | 
            - Si el cliente pregunta por algo que no está en el inventario,
              dile que no lo tienes disponible actualmente.

            TONO: Mecánico colombiano, cercano y directo. Usa 'parcero' ocasionalmente.
            Solo responde sobre motos, aceites, llantas y kits de arrastre.
            """, marca, modelo, cilindraje, catalogo);
    }
}