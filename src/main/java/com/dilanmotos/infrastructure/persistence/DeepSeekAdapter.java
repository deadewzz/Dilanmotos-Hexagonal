package com.dilanmotos.infrastructure.persistence;

import com.dilanmotos.domain.model.ChatResponse;
import com.dilanmotos.domain.model.Producto;
import com.dilanmotos.domain.repository.ProductoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DeepSeekAdapter { // ← Nota: NO implementa ChatExternalPort

    @Value("${deepseek.api.key}")
    private String apiKey;

    private static final String MODEL_NAME = "deepseek-v4-flash";
    private static final String DEEPSEEK_URL = "https://api.deepseek.com/v1/chat/completions";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ProductoRepository productoRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DeepSeekAdapter(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Método para chat normal
    public ChatResponse getAiAnswer(String question, String nombreMarca, String modelo, double cilindraje) {
        List<Producto> productos = productoRepository.obtenerTodos();
        String catalogo = buildCatalogoTexto(productos);

        String marcaSafe = (nombreMarca != null && !nombreMarca.isBlank()) ? nombreMarca : "Suzuki";
        String modeloSafe = (modelo != null && !modelo.isBlank()) ? modelo : "Gixxer 250 SF";

        String systemPrompt = String.format("""
            Eres el mecánico jefe de Dilan Motos en Bogotá, Colombia.
            El cliente tiene una %s %s de %.0fcc.
            
            **INSTRUCCIONES CRÍTICAS:**
            1. NO uses etiquetas <think>, <thinking> o cualquier tipo de razonamiento visible.
            2. NO muestres tu proceso de pensamiento.
            3. Responde DIRECTAMENTE con la respuesta final.
            4. NO digas "basado en", "considerando" o frases similares.
            5. SOLO recomienda productos del inventario listado abajo.
            6. NO inventes productos ni precios.

            INVENTARIO ACTUAL:
            %s

            TONO: Mecánico colombiano, cercano y directo. Usa 'parcero' ocasionalmente.
            
            RESPUESTA: Da la recomendación directamente, sin explicar por qué llegaste a esa conclusión.
            """, marcaSafe, modeloSafe, cilindraje, catalogo);

        var body = Map.of(
            "model", MODEL_NAME,
            "messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", question)
            ),
            "temperature", 0.7,
            "max_tokens", 2048,
            "thinking", Map.of("type", "disabled")
        );

        return ejecutarConsulta(body, false);
    }

    // Método para recomendaciones en JSON
    public ChatResponse getRecomendaciones(String nombreMarca, String modelo, double cilindraje) {
        List<Producto> productos = productoRepository.obtenerTodos();
        
        if (productos == null || productos.isEmpty()) {
            return new ChatResponse("{\"recomendaciones\":[]}");
        }
        
        String catalogoSeguro = buildCatalogoParaJson(productos);
        String marcaSafe = (nombreMarca != null && !nombreMarca.isBlank()) ? nombreMarca : "Suzuki";
        String modeloSafe = (modelo != null && !modelo.isBlank()) ? modelo : "Gixxer 250 SF";

        String prompt = String.format("""
            Eres un asistente de recomendaciones para una tienda de motos Dilan Motos en Bogotá.
            
            **INSTRUCCIONES CRÍTICAS:**
            1. NO uses etiquetas <think>, <thinking> o razonamiento visible.
            2. SOLO responde con JSON puro.
            
            INVENTARIO DISPONIBLE (SOLO estos productos existen):
            %s
            
            MOTOCICLETA DEL CLIENTE: %s %s (%.0fcc)
            
            INSTRUCCIÓN: Selecciona hasta 3 productos del inventario que sean COMPATIBLES con esta motocicleta.
            ES OBLIGATORIO que los 3 productos sean de CATEGORÍAS DIFERENTES.
            
            RESPUESTA: Debes responder ÚNICAMENTE con un objeto JSON en este formato exacto:
            {"recomendaciones": [{"tipo": "categoria", "nombre": "nombre exacto del producto", "razon": "razón corta"}]}
            
            REGLAS ESTRICTAS:
            1. SOLO el objeto JSON, sin texto adicional
            2. Usa los nombres EXACTOS del inventario
            3. Cada producto debe ser de una categoría diferente
            4. Si no hay suficientes productos, devuelve {"recomendaciones": []}
            """, catalogoSeguro, marcaSafe, modeloSafe, cilindraje);

        var body = Map.of(
            "model", MODEL_NAME,
            "messages", List.of(
                Map.of("role", "system", "content", "Eres un asistente que genera JSON válido. NO uses <think>. Siempre respondes con JSON puro."),
                Map.of("role", "user", "content", prompt)
            ),
            "temperature", 0.1,
            "max_tokens", 600,
            "thinking", Map.of("type", "disabled"),
            "response_format", Map.of("type", "json_object")
        );

        ChatResponse response = ejecutarConsulta(body, true);
        
        System.out.println(">>> Recomendaciones generadas: " + response.content());
        
        if (response.content() == null || 
            response.content().isEmpty() || 
            response.content().equals("{\"recomendaciones\":[]}") ||
            !response.content().contains("recomendaciones")) {
            
            String fallback = generarRecomendacionesFallback(productos, marcaSafe, modeloSafe, cilindraje);
            System.out.println(">>> Usando fallback: " + fallback);
            return new ChatResponse(fallback);
        }
        
        return response;
    }

    // ========== MÉTODOS PRIVADOS ==========

    private ChatResponse ejecutarConsulta(Map<String, Object> body, boolean esJson) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            String jsonPayload = objectMapper.writeValueAsString(body);
            System.out.println(">>> Enviando a DeepSeek con modelo: " + MODEL_NAME);
            
            HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);
            var response = restTemplate.postForObject(DEEPSEEK_URL, entity, Map.class);
            
            var choices = (List<Map<String, Object>>) response.get("choices");
            var message = (Map<String, Object>) choices.get(0).get("message");
            String content = message.get("content").toString();
            
            System.out.println(">>> Respuesta de DeepSeek recibida, longitud: " + content.length());

            String respuestaLimpia = limpiarRespuesta(content);
            System.out.println(">>> Respuesta limpiada: " + respuestaLimpia);

            if (esJson) {
                String jsonSanitizado = sanitizeJson(respuestaLimpia);
                System.out.println(">>> JSON sanitizado: " + jsonSanitizado);
                return new ChatResponse(jsonSanitizado);
            }
            
            return new ChatResponse(respuestaLimpia);

        } catch (HttpStatusCodeException e) {
            System.err.println(">>> Error HTTP DeepSeek (" + e.getStatusCode() + "): " + e.getResponseBodyAsString());
            return new ChatResponse(esJson ? "{\"recomendaciones\":[]}" : "Error al conectar con la IA. Por favor, intenta de nuevo.");
        } catch (Exception e) {
            System.err.println(">>> Error inesperado en DeepSeekAdapter: " + e.getMessage());
            e.printStackTrace();
            return new ChatResponse(esJson ? "{\"recomendaciones\":[]}" : "Error al procesar la solicitud.");
        }
    }

    private String limpiarRespuesta(String content) {
        if (content == null) return "";
        
        String cleaned = content.replaceAll("(?s)<think>.*?</think>", "")
                                .replaceAll("(?s)<thinking>.*?</thinking>", "")
                                .replaceAll("```\\s*", "")
                                .trim();
        
        if (cleaned.toLowerCase().startsWith("thinking process") || 
            cleaned.toLowerCase().startsWith("process:")) {
            int firstNewLine = cleaned.indexOf('\n');
            if (firstNewLine != -1) {
                cleaned = cleaned.substring(firstNewLine + 1).trim();
            }
        }
        
        cleaned = cleaned.replaceAll("\\n\\s*\\n", "\n");
        
        if (cleaned.isEmpty()) {
            return "¡Hola, parcero! ¿En qué te puedo ayudar con tu moto?";
        }
        
        if (cleaned.contains("<") && cleaned.contains(">")) {
            cleaned = cleaned.replaceAll("<[^>]*>", " ").trim();
        }
        
        return cleaned;
    }

    private String sanitizeJson(String raw) {
        if (raw == null || raw.isBlank()) return "{\"recomendaciones\":[]}";
        
        String cleaned = raw.trim();
        
        int start = -1;
        int end = -1;
        int braceCount = 0;
        
        for (int i = 0; i < cleaned.length(); i++) {
            char c = cleaned.charAt(i);
            if (c == '{') {
                if (start == -1) start = i;
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0 && start != -1) {
                    end = i;
                    break;
                }
            }
        }
        
        if (start != -1 && end != -1 && end > start) {
            cleaned = cleaned.substring(start, end + 1);
        }
        
        cleaned = cleaned.replaceAll("```json\\s*", "")
                         .replaceAll("```\\s*", "")
                         .replaceAll("`", "")
                         .replaceAll("\\n", " ")
                         .replaceAll("\\r", " ")
                         .replaceAll("\\t", " ")
                         .replaceAll("\\s+", " ")
                         .trim();
        
        try {
            JsonNode jsonNode = objectMapper.readTree(cleaned);
            if (jsonNode.has("recomendaciones") && jsonNode.get("recomendaciones").isArray()) {
                return objectMapper.writeValueAsString(jsonNode);
            } else {
                ObjectNode root = objectMapper.createObjectNode();
                ArrayNode recommendations = objectMapper.createArrayNode();
                root.set("recomendaciones", recommendations);
                return objectMapper.writeValueAsString(root);
            }
        } catch (Exception e) {
            System.err.println(">>> Error validando JSON: " + e.getMessage());
            return "{\"recomendaciones\":[]}";
        }
    }

    private String generarRecomendacionesFallback(List<Producto> productos, String marca, String modelo, double cilindraje) {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            ArrayNode recomendaciones = objectMapper.createArrayNode();
            
            Map<String, List<Producto>> productosPorCategoria = productos.stream()
                .filter(p -> p.getNombreCategoria() != null && !p.getNombreCategoria().isEmpty())
                .collect(Collectors.groupingBy(Producto::getNombreCategoria));
            
            List<String> categorias = new ArrayList<>(productosPorCategoria.keySet());
            int categoriasSeleccionadas = Math.min(3, categorias.size());
            
            for (int i = 0; i < categoriasSeleccionadas; i++) {
                String categoria = categorias.get(i);
                List<Producto> productosCategoria = productosPorCategoria.get(categoria);
                if (!productosCategoria.isEmpty()) {
                    Producto p = productosCategoria.get(0);
                    ObjectNode rec = objectMapper.createObjectNode();
                    rec.put("tipo", categoria);
                    rec.put("nombre", p.getNombre() != null ? p.getNombre() : "Producto");
                    rec.put("razon", String.format("Recomendado para tu %s %s", marca, modelo));
                    recomendaciones.add(rec);
                }
            }
            
            if (recomendaciones.size() < 3) {
                List<String> categoriasSeleccionadasList = new ArrayList<>();
                for (int i = 0; i < recomendaciones.size(); i++) {
                    categoriasSeleccionadasList.add(recomendaciones.get(i).get("tipo").asText());
                }
                
                for (Producto p : productos) {
                    if (recomendaciones.size() >= 3) break;
                    String categoria = p.getNombreCategoria();
                    if (categoria != null && !categoriasSeleccionadasList.contains(categoria)) {
                        ObjectNode rec = objectMapper.createObjectNode();
                        rec.put("tipo", categoria);
                        rec.put("nombre", p.getNombre() != null ? p.getNombre() : "Producto");
                        rec.put("razon", String.format("Recomendado para tu %s %s", marca, modelo));
                        recomendaciones.add(rec);
                        categoriasSeleccionadasList.add(categoria);
                    }
                }
            }
            
            root.set("recomendaciones", recomendaciones);
            return objectMapper.writeValueAsString(root);
            
        } catch (Exception e) {
            System.err.println(">>> Error en fallback: " + e.getMessage());
            return "{\"recomendaciones\":[]}";
        }
    }

    private String buildCatalogoTexto(List<Producto> productos) {
        if (productos == null || productos.isEmpty()) return "Sin productos en inventario.";
        return productos.stream()
            .limit(20)
            .map(p -> {
                String cat = p.getNombreCategoria() != null ? p.getNombreCategoria() : "Repuestos";
                String nombre = p.getNombre() != null ? p.getNombre() : "Producto";
                nombre = nombre.replaceAll("[\\n\\r\\t\"]", " ").trim();
                cat = cat.replaceAll("[\\n\\r\\t\"]", " ").trim();
                return String.format("- %s (%s): $%,.0f COP", nombre, cat, p.getPrecio() != null ? p.getPrecio() : 0.0);
            })
            .collect(Collectors.joining("\n"));
    }

    private String buildCatalogoParaJson(List<Producto> productos) {
        if (productos == null || productos.isEmpty()) {
            return "Sin productos en inventario.";
        }
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (Producto p : productos) {
            if (count >= 30) break;
            String nombre = p.getNombre() != null ? p.getNombre() : "Producto";
            String categoria = p.getNombreCategoria() != null ? p.getNombreCategoria() : "General";
            nombre = nombre.replaceAll("[\\n\\r\\t\"]", " ").trim();
            categoria = categoria.replaceAll("[\\n\\r\\t\"]", " ").trim();
            sb.append(count + 1).append(". ").append(nombre)
              .append(" (").append(categoria).append(")\n");
            count++;
        }
        return sb.toString();
    }
}