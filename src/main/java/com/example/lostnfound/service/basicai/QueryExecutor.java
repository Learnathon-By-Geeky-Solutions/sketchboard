package com.example.lostnfound.service.basicai;

import com.example.lostnfound.model.Post;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface QueryExecutor {

    ResponseEntity<List<Post>> executeAISearch(String response);
}
