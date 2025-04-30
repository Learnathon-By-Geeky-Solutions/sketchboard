package com.example.lostnfound.service.ai.embedding;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

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

        HttpClient httpClient = HttpClient.newHttpClient();
	    try {
		    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		    return objectMapper.readValue(response.body(), float[][].class)[0];
	    } catch (IOException e) {
            log.error("Error while sending request to AI Lab: {}", e.getMessage());
            throw e;
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
