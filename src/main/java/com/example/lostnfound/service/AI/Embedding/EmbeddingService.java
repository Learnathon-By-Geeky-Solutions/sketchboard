package com.example.lostnfound.service.AI.Embedding;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.genai.Client;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class EmbeddingService {

    // Make http request
    @Value("${HF_TOKEN}")
    private String huggingfaceToken;
    String url = "https://api-inference.huggingface.co/pipeline/feature-extraction/sentence-transformers/all-MiniLM-L6-v2";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public float[] getEmbeddingHuggingFace(String input) throws IOException, InterruptedException {
        System.out.println("Token: " + huggingfaceToken);

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

    private Client client;
    @Value("${gemini.api.key}")
    private char[] geminiApiKey;
    @Value("gemini-embedding-exp-03-07")
    private String modelId;

    /*
        curl "https://generativelanguage.googleapis.com/v1beta/models/text-embedding-004:embedContent?key=$GEMINI_API_KEY" \
-H 'Content-Type: application/json' \
-d '{"model": "models/gemini-embedding-exp-03-07",
     "content": {
     "parts":[{
     "text": "What is the meaning of life?"}]}
    }'
     */

    public float[] getEmbeddingGemini(String input) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelId + ":embedContent?key=" + new String(geminiApiKey);
        String reqBody = "{\"model\": \"models/" + modelId + "\", \"content\": {\"parts\":[{\"text\": \"" + input + "\"}]}}";
        System.out.println("Request body: " + reqBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response json structure: " + response.body());
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode value= root.path("embedding").path("values");
            return objectMapper.treeToValue(value, float[].class);
        } catch (IOException e) {
            log.error("Error: " + e.getMessage(), e);
            return null;
        } catch (InterruptedException e) {
            log.error("Error: " + e.getMessage(), e);
            Thread.currentThread().interrupt(); // Re-interrupt the thread
            return null;
        }
    }

    public float[] getEmbedding(String input){
        input = input.replaceAll("[<>]", "")
                .replaceAll("(?i)script", "")
                .trim();
        return getEmbeddingGemini(input);
    }
}
