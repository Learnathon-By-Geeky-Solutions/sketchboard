package com.example.lostnfound.service.BasicAISearch;

import com.example.lostnfound.model.Post;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Component
@Slf4j
public class QueryExecutorImpl implements QueryExecutor {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public ResponseEntity<List<Post>> executeAISearch(String response) {

        //Extract Query
        String sqlQueryPattern = "(?i)SELECT\\s+\\*\\s+FROM\\s+post\\s+WHERE\\s+.*";
        Pattern pattern = Pattern.compile(sqlQueryPattern, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(response);
        String searchQuery;
        if (matcher.find()) {
            searchQuery = matcher.group().replaceAll("`", "");
        } else {
            log.error("No valid SQL query found in AI response");
            return null;
        }
        System.out.println(searchQuery);
        List<Post> results = jdbcTemplate.query(searchQuery, new BeanPropertyRowMapper<>(Post.class));
        if (results==null || results.isEmpty()) {
            return null;
        }
        return ResponseEntity.ok(results);
    }
}