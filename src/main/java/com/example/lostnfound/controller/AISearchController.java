package com.example.lostnfound.controller;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import java.io.IOException;
import org.apache.http.HttpException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AISearchController {
        // you have to put your API here
        Client client = Client.builder().apiKey("put your gemini api here").build();

        @GetMapping("/basicAISearch")
        public String getResponse(@RequestParam("message") String query) throws HttpException, IOException {
            GenerateContentResponse response =
                    client.models.generateContent("gemini-2.0-flash-001", query, null);
            return response.text();
        }
}