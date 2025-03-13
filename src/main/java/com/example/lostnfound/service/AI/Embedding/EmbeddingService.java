package com.example.lostnfound.service.AI.Embedding;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;

@Service
@Data
public class EmbeddingService {

    public EmbeddingService() {

    }

    /*
            import requests

            API_URL = "https://router.huggingface.co/hf-inference/models/sentence-transformers/all-MiniLM-L6-v2"
            headers = {"Authorization": "Bearer hf_xxxxxxxxxxxxxxxxxxxxxxxx"}

            def query(payload):
                response = requests.post(API_URL, headers=headers, json=payload)
                return response.json()

            output = query({
                "inputs": {
                "source_sentence": "That is a happy person",
                "sentences": [
                    "That is a happy dog",
                    "That is a very happy person",
                    "Today is a sunny day"
                ]
            },
            })

         */
    // Make http request
    @Value("${gemini.api.key}")
    private String huggingfaceToken;
    String url = "https://router.huggingface.co/hf-inference/models/sentence-transformers/all-MiniLM-L6-v2";
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + huggingfaceToken)
            .build();

    // Get response


}
