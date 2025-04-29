package com.example.lostnfound.service.ai.embedding;
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
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    // Make http request
    @Value("${HF_TOKEN}")
    private String huggingfaceToken;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public float[] getEmbeddingHuggingFace(String input) throws IOException, InterruptedException {
        String url = "https://api-inference.huggingface.co/pipeline/feature-extraction/sentence-transformers/all-MiniLM-L6-v2";

        //this regex removes all special characters except for spaces
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + huggingfaceToken)
                .headers(CONTENT_TYPE, APPLICATION_JSON)
                .POST(HttpRequest.BodyPublishers.ofString("(\"{\\\"inputs\\\": \\\"\"" + input + "\"\\\"}\")"))
                .build();

        // Get response
	    try (HttpClient httpClient = HttpClient.newHttpClient()) {
		    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		    return objectMapper.readValue(response.body(), float[].class);
	    }
    }

    private Client client;
    @Value("${gemini.api.key}")
    private char[] geminiApiKey;
    private String modelId = "gemini-embedding-exp-03-07";


    public float[] getEmbeddingGemini(String input) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelId + ":embedContent?key=" + new String(geminiApiKey);
        String reqBody = "{\"model\": \"models/" + modelId + "\", \"content\": {\"parts\":[{\"text\": \"" + input + "\"}]}}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode value= root.path("embedding").path("values");
            return objectMapper.treeToValue(value, float[].class);
        } catch (IOException e) {
	        log.error("Error: {}", e.getMessage(), e);
            return new float[0];
        } catch (InterruptedException e) {
	        log.error("Error: {}", e.getMessage(), e);
            Thread.currentThread().interrupt(); // Re-interrupt the thread
            return new float[0];
        }
    }

    @Value("${ai.baseUrl}")
    String aiLabUrl;

    public float[] getEmbeddingClipLocal(String input, int isImage) throws IOException, InterruptedException {
        String url = aiLabUrl + "/encode";

        String reqBody;
        if(isImage == 1){
            reqBody = "[{\"img_uri\": \"" + input + "\"}]";
        }else{
            reqBody = "[{\"text\": \"" + input + "\"}]";
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                .build();

	    try (HttpClient httpClient = HttpClient.newHttpClient()) {
		    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		    return objectMapper.readValue(response.body(), float[][].class)[0];
	    }
    }

    public float[] getEmbedding(String input) throws IOException, InterruptedException {
        input = input.replaceAll("[<>]", "")
                .replaceAll("(?i)script", "")
                .trim();
        return getEmbeddingClipLocal(input, 0);
    }

    public float[] getEmbeddingImage(String input) throws IOException, InterruptedException {
        input = input.replaceAll("[<>]", "")
                .replaceAll("(?i)script", "")
                .trim();
        return getEmbeddingClipLocal(input, 1);
    }
}
