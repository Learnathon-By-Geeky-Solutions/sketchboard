package com.example.lostnfound.service.ai.geminichat;

import com.example.lostnfound.model.Post;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QueryExecutorTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private QueryExecutor queryExecutor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        queryExecutor = new QueryExecutor(jdbcTemplate);
    }

    @Test
    void testExecuteAISearch_ValidQuery() {
        // Given a valid SQL query in the AI response
        String validResponse = "SELECT * FROM post WHERE title = 'Test Post'";
        Post post = new Post(); // Create a mock post object
        post.setId(1L);
        post.setTitle("Test Post");

        // Mock the JdbcTemplate query to return a list of posts
        when(jdbcTemplate.query(eq("SELECT * FROM post WHERE title = 'Test Post'"), any(BeanPropertyRowMapper.class)))
                .thenReturn(List.of(post));

        // When executing the AI search
        ResponseEntity<List<Post>> response = queryExecutor.executeAISearch(validResponse);

        // Then verify the response and the behavior of the JdbcTemplate
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Post", response.getBody().get(0).getTitle());
        verify(jdbcTemplate, times(1)).query(eq("SELECT * FROM post WHERE title = 'Test Post'"), any(BeanPropertyRowMapper.class));
    }

    @Test
    void testExecuteAISearch_InvalidQuery() {
        // Given an invalid SQL query in the AI response
        String invalidResponse = "DROP TABLE post";

        // When executing the AI search
        ResponseEntity<List<Post>> response = queryExecutor.executeAISearch(invalidResponse);

        // Then verify that the response is null (or error handling behavior, depending on your implementation)
        assertNull(response);
        verify(jdbcTemplate, never()).query(anyString(), any(BeanPropertyRowMapper.class)); // JdbcTemplate should not be called
    }

    @Test
    void testExecuteAISearch_NoSQLQueryInResponse() {
        // Given a response that doesn't contain a valid SQL query
        String noQueryResponse = "This is a response without SQL query";

        // When executing the AI search
        ResponseEntity<List<Post>> response = queryExecutor.executeAISearch(noQueryResponse);

        // Then verify that the response is null (since no valid SQL query is found)
        assertNull(response);
        verify(jdbcTemplate, never()).query(anyString(), any(BeanPropertyRowMapper.class)); // JdbcTemplate should not be called
    }

    @Test
    void testExecuteAISearch_EmptyResponse() {
        // Given an empty response
        String emptyResponse = "";

        // When executing the AI search
        ResponseEntity<List<Post>> response = queryExecutor.executeAISearch(emptyResponse);

        // Then verify that the response is null
        assertNull(response);
        verify(jdbcTemplate, never()).query(anyString(), any(BeanPropertyRowMapper.class)); // JdbcTemplate should not be called
    }

    @Test
    void testExecuteAISearch_SQLInjectionQuery() {
        // Given a SQL injection query in the AI response
        String sqlInjectionResponse = "SELECT * FROM post WHERE title = '' OR 1=1";

        // When executing the AI search
        ResponseEntity<List<Post>> response = queryExecutor.executeAISearch(sqlInjectionResponse);

        // Then verify the behavior of the JdbcTemplate (if allowed) or handle it appropriately
        assertNotNull(response);
        verify(jdbcTemplate, times(1)).query(eq("SELECT * FROM post WHERE title = '' OR 1=1"), any(BeanPropertyRowMapper.class));
    }
}
