package com.example.lostnfound.service;

import com.example.lostnfound.exception.PostNotFoundException;
import com.example.lostnfound.exception.UserNotFoundException;
import com.example.lostnfound.model.Image;
import com.example.lostnfound.model.Post;
import com.example.lostnfound.model.User;
import com.example.lostnfound.repository.PostRepo;
import com.example.lostnfound.service.ai.embedding.EmbeddingService;
import com.example.lostnfound.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock private PostRepo postRepo;
    @Mock private EmbeddingService embeddingService;
    @Mock private UserService userService;
    @Mock private ImageService imageService;

    @InjectMocks
    private PostService postService;

    private User user;
    private Post post;
    private Image image;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setEmbedding(new float[]{0.0f, 0.0f, 0.0f});

        // Set up a post with its own embedding and an image
        post = new Post();
        post.setId(1L);
        post.setUserId(user.getUserId());
        post.setTitle("Lost Phone");
        post.setDescription("Lost my phone in the park");
        post.setEmbedding(new float[]{0.1f, 0.2f, 0.3f});
        image = new Image();
        image.setId(1L);
        post.setImage(image);
    }

    @Test
    void testSavePost() throws IOException, InterruptedException, UserNotFoundException {
        float[] embedding = new float[]{0.1f, 0.2f, 0.3f};
        when(embeddingService.getEmbedding(post.infoForEmbedding())).thenReturn(embedding);
        when(userService.findById(post.getUserId())).thenReturn(user);

        postService.savePost(post);

        verify(postRepo).save(post);
        assertArrayEquals(embedding, post.getEmbedding());
        verify(userService).findById(post.getUserId());
    }

    @Test
    void testGetPost_Success() throws UserNotFoundException, PostNotFoundException {
        when(postRepo.findById(1L)).thenReturn(Optional.of(post));
        when(userService.getCurrentUser()).thenReturn(user);

        Post result = postService.getPost(1L);

        assertSame(post, result);
        verify(postRepo).findById(1L);
    }

    @Test
    void testGetPost_NotFound() {
        when(postRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(PostNotFoundException.class, () -> postService.getPost(1L));
    }

    @Test
    void testGetPostsWithPagination() {
        Page<Post> page = mock(Page.class);
        PageRequest pr = PageRequest.of(0, 10).withSort(Sort.by("id"));
        when(postRepo.findAll(pr)).thenReturn(page);

        Page<Post> result = postService.getPostsWithPagination(0, 10);

        assertSame(page, result);
        verify(postRepo).findAll(pr);
    }

    @Test
    void testDeletePost_Success() throws PostNotFoundException {
        when(postRepo.findById(1L)).thenReturn(Optional.of(post));

        postService.deletePost(1L);

        verify(imageService).deleteImage(image.getId());
        verify(postRepo).deleteById(1L);
    }

    @Test
    void testDeletePost_NotFound() {
        when(postRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(PostNotFoundException.class, () -> postService.deletePost(1L));
    }

    @Test
    void testUpdatePost_Success() throws PostNotFoundException {
        Post updated = new Post();
        updated.setTitle("New Title");
        updated.setDescription("New Desc");
        when(postRepo.findById(1L)).thenReturn(Optional.of(post));

        postService.updatePost(1L, updated);

        assertEquals("New Title", post.getTitle());
        assertEquals("New Desc", post.getDescription());
    }

    @Test
    void testUpdatePost_NotFound() {
        when(postRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(PostNotFoundException.class, () -> postService.updatePost(1L, new Post()));
    }

    @Test
    void testGetCustomizedPosts() throws UserNotFoundException {
        when(userService.getCurrentUser()).thenReturn(user);
        when(postRepo.findTopKSimilarPosts(any(float[].class), eq(Long.MAX_VALUE)))
                .thenReturn(List.of(post));

        List<Post> result = postService.getCustomizedPosts();

        assertEquals(1, result.size());
        assertSame(post, result.get(0));
        verify(postRepo).findTopKSimilarPosts(any(float[].class), eq(Long.MAX_VALUE));
    }
}