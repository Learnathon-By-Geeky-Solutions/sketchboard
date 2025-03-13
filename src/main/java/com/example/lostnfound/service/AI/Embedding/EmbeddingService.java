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

    /*
       import requests

hf_token = ""

API_URL = "https://api-inference.huggingface.co/pipeline/feature-extraction/sentence-transformers/all-MiniLM-L6-v2"
headers = {"Authorization": f"Bearer {hf_token}"}

def query(payload):
    response = requests.post(API_URL, headers=headers, json=payload)
    return response.json()

output = query({"inputs": "The big brown dog"})

print(output)


         */
    // Make http request
    @Value("${gemini.api.key}")
    private String huggingfaceToken;
    String url = "https://api-inference.huggingface.co/pipeline/feature-extraction/sentence-transformers/all-MiniLM-L6-v2";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public float[] getEmbedding(String input) throws IOException, InterruptedException {
        System.out.println("Getting embedding for: " + input);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + huggingfaceToken)
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("(\"{\\\"inputs\\\": \\\"\" + input + \"\\\"}\")"))
                .build();

        // Get response
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        return objectMapper.readValue(response.body(), float[][].class)[0];
    }


}
