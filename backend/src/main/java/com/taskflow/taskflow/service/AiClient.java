package com.taskflow.taskflow.service;

import com.taskflow.taskflow.exception.AiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

@Component
public class AiClient {

    private static final Logger log = LoggerFactory.getLogger(AiClient.class);

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public AiClient(@Value("${app.ai.api-key:}") String apiKey,
                    @Value("${app.ai.model}") String model,
                    @Value("${app.ai.base-url}") String baseUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5_000);
        factory.setReadTimeout(30_000);
        this.restClient = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
        this.apiKey = apiKey;
        this.model = model;
    }

    public String complete(String system, String userMessage, int maxTokens) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new AiException(HttpStatus.SERVICE_UNAVAILABLE,
                    "O assistente de IA não está configurado.");
        }

        Map<String, Object> body = Map.of(
                "model", model,
                "max_tokens", maxTokens,
                "system", system,
                "messages", List.of(Map.of("role", "user", "content", userMessage)));

        try {
            Map<?, ?> response = restClient.post()
                    .uri("/v1/messages")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Map.class);
            return extractText(response);
        } catch (RestClientException e) {
            // Não logamos a requisição (pode conter dados do usuário) nem a chave
            log.warn("Falha ao chamar a API de IA: {}", e.getClass().getSimpleName());
            throw new AiException(HttpStatus.BAD_GATEWAY,
                    "O assistente de IA está indisponível no momento. Tente novamente.");
        }
    }

    private String extractText(Map<?, ?> response) {
        if (response != null && response.get("content") instanceof List<?> blocks) {
            StringBuilder text = new StringBuilder();
            for (Object block : blocks) {
                if (block instanceof Map<?, ?> b && "text".equals(b.get("type"))
                        && b.get("text") != null) {
                    text.append(b.get("text"));
                }
            }
            if (!text.isEmpty()) {
                return text.toString().trim();
            }
        }
        throw new AiException(HttpStatus.BAD_GATEWAY, "Resposta inesperada do assistente de IA.");
    }
}
