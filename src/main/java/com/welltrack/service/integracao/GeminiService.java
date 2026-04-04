package com.welltrack.service.integracao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.welltrack.dto.analisenutricional.DadosAnaliseNutricional;
import com.welltrack.dto.analisenutricional.ItemNutricional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GeminiService {

    @Value("${api.gemini.api-key}")
    private String apiKey;

    @Value("${api.gemini.limite-diario-por-usuario}")
    private int limiteDiario;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    // Rate limiting: mapa de userId -> mapa de data -> contador
    private final ConcurrentHashMap<UUID, ConcurrentHashMap<LocalDate, Integer>> contadorUsuarios = new ConcurrentHashMap<>();

    public DadosAnaliseNutricional analisarImagem(byte[] imagemBytes, String mimeType, UUID idUsuario) {
        verificarLimiteDiario(idUsuario);

        String base64Image = java.util.Base64.getEncoder().encodeToString(imagemBytes);
        String responseBody = chamarGeminiApi(base64Image, mimeType);

        incrementarContador(idUsuario);

        return parsearResposta(responseBody);
    }

    public int consultasRestantes(UUID idUsuario) {
        var contadorDatas = contadorUsuarios.get(idUsuario);
        if (contadorDatas == null) return limiteDiario;

        int usadas = contadorDatas.getOrDefault(LocalDate.now(), 0);
        return Math.max(0, limiteDiario - usadas);
    }

    private void verificarLimiteDiario(UUID idUsuario) {
        var contadorDatas = contadorUsuarios.computeIfAbsent(idUsuario, k -> new ConcurrentHashMap<>());
        int usadasHoje = contadorDatas.getOrDefault(LocalDate.now(), 0);

        if (usadasHoje >= limiteDiario) {
            throw new RuntimeException("Limite diário de " + limiteDiario + " análises atingido. Tente novamente amanhã.");
        }
    }

    private void incrementarContador(UUID idUsuario) {
        var contadorDatas = contadorUsuarios.computeIfAbsent(idUsuario, k -> new ConcurrentHashMap<>());
        contadorDatas.merge(LocalDate.now(), 1, Integer::sum);

        // Limpar datas antigas para não acumular memória
        contadorDatas.keySet().removeIf(data -> data.isBefore(LocalDate.now()));
    }

    private String chamarGeminiApi(String base64Image, String mimeType) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

        String prompt = """
                Analise esta foto de refeição e identifique todos os alimentos visíveis.
                Para cada alimento, estime a quantidade em gramas e os valores nutricionais.
                
                Responda APENAS com um JSON válido no seguinte formato, sem texto adicional, sem markdown:
                {
                  "nomeRefeicao": "descrição curta da refeição",
                  "caloriasTotal": 0.00,
                  "pesoTotal": 0.00,
                  "carboidratoTotal": 0.00,
                  "proteinaTotal": 0.00,
                  "gorduraTotal": 0.00,
                  "itens": [
                    {
                      "nome": "nome do alimento",
                      "quantidade": 0.00,
                      "calorias": 0.00,
                      "carboidrato": 0.00,
                      "proteina": 0.00,
                      "gordura": 0.00
                    }
                  ]
                }
                
                Regras:
                - Todos os valores numéricos devem ser números decimais (não strings)
                - Quantidade em gramas
                - Valores nutricionais por porção estimada
                - Os totais devem ser a soma dos itens
                - Se não conseguir identificar alimentos, retorne nomeRefeicao como "Não identificado" com valores zerados
                """;

        // Montar o JSON no formato exato da API Gemini
        String jsonPayload;
        try {
            var inlineData = objectMapper.createObjectNode();
            inlineData.put("mime_type", mimeType);
            inlineData.put("data", base64Image);

            var imagePart = objectMapper.createObjectNode();
            imagePart.set("inline_data", inlineData);

            var textPart = objectMapper.createObjectNode();
            textPart.put("text", prompt);

            var partsArray = objectMapper.createArrayNode();
            partsArray.add(imagePart);
            partsArray.add(textPart);

            var content = objectMapper.createObjectNode();
            content.set("parts", partsArray);

            var contentsArray = objectMapper.createArrayNode();
            contentsArray.add(content);

            var requestBody = objectMapper.createObjectNode();
            requestBody.set("contents", contentsArray);

            jsonPayload = objectMapper.writeValueAsString(requestBody);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao montar requisição para o Gemini.", e);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 429) {
                throw new RuntimeException("Limite de requisições da API Gemini atingido. Tente novamente mais tarde.");
            }

            if (response.statusCode() != 200) {
                throw new RuntimeException("Erro na API Gemini (status " + response.statusCode() + "): " + response.body());
            }

            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Erro ao se comunicar com a API Gemini.", e);
        }
    }

    private DadosAnaliseNutricional parsearResposta(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode textNode = root.path("candidates").get(0)
                    .path("content").path("parts").get(0).path("text");

            String jsonText = textNode.asText();

            // Remover possíveis marcadores de markdown (```json ... ```)
            jsonText = jsonText.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();

            JsonNode analise = objectMapper.readTree(jsonText);

            List<ItemNutricional> itens = new ArrayList<>();
            JsonNode itensNode = analise.path("itens");
            if (itensNode.isArray()) {
                for (JsonNode item : itensNode) {
                    itens.add(new ItemNutricional(
                            item.path("nome").asText(),
                            new BigDecimal(item.path("quantidade").asText("0")),
                            new BigDecimal(item.path("calorias").asText("0")),
                            new BigDecimal(item.path("carboidrato").asText("0")),
                            new BigDecimal(item.path("proteina").asText("0")),
                            new BigDecimal(item.path("gordura").asText("0"))
                    ));
                }
            }

            return new DadosAnaliseNutricional(
                    analise.path("nomeRefeicao").asText("Refeição"),
                    new BigDecimal(analise.path("caloriasTotal").asText("0")),
                    new BigDecimal(analise.path("pesoTotal").asText("0")),
                    new BigDecimal(analise.path("carboidratoTotal").asText("0")),
                    new BigDecimal(analise.path("proteinaTotal").asText("0")),
                    new BigDecimal(analise.path("gorduraTotal").asText("0")),
                    itens
            );
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar resposta do Gemini.", e);
        }
    }
}
