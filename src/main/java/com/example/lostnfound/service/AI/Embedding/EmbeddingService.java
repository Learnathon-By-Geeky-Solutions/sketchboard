package com.example.lostnfound.service.AI.Embedding;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@Data
public class EmbeddingService {

    // Make http request
    @Value("${HF_TOKEN}")
    private String huggingfaceToken;
    String url = "https://api-inference.huggingface.co/pipeline/feature-extraction/sentence-transformers/all-MiniLM-L6-v2";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public float[] getEmbedding(String input) throws IOException, InterruptedException {
        System.out.println("Token: " + huggingfaceToken);
        //remove problematic characters
        input = input.replaceAll("[<>]", "")
                .replaceAll("(?i)script", "")
                .trim();
        //this regex removes all special characters except for spaces
        String reqBody = "{\"inputs\": \"" + input + "\"}";
        System.out.println("Request body: " + reqBody);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + huggingfaceToken)
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("(\"{\\\"inputs\\\": \\\"\"" + input + "\"\\\"}\")"))
                .build();

        // Get response
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        try {
            return objectMapper.readValue(response.body(), float[].class);
        } catch (Exception e) {
            System.out.println("Error + " + e.getMessage());
            System.out.println("Response: " + response.body());
        }
        return null;
    }

}
