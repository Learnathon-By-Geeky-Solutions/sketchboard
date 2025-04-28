package com.example.lostnfound.service.ai.GeminiChat;

import com.example.lostnfound.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Component
@Slf4j
public class QueryExecutor {
    private final JdbcTemplate jdbcTemplate;
    
    private static final Logger logger = LoggerFactory.getLogger(QueryExecutor.class);
    QueryExecutor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ResponseEntity<List<Post>> executeAISearch(String response) {

        //Extract Query
        String sqlQueryPattern = "(?i)SELECT\\s+\\*\\s+FROM\\s+post\\s+WHERE\\s+.*";
        Pattern pattern = Pattern.compile(sqlQueryPattern, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(response);
        String searchQuery;
        if (matcher.find()) {
            searchQuery = matcher.group().replace("`", "");
        } else {
            log.error("No valid SQL query found in AI response");
            return null;
        }
        logger.info("Performed PostgreSQL Query:");
        logger.info(searchQuery);
        List<Post> results = jdbcTemplate.query(searchQuery, new BeanPropertyRowMapper<>(Post.class));
        return ResponseEntity.ok(results);
    }
}