package com.skwarek.blogger.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skwarek.blogger.EmbeddedDatabase;
import com.skwarek.blogger.domain.Post;
import com.skwarek.blogger.dto.PostRequest;
import com.skwarek.blogger.exception.NotFoundAccountException;
import com.skwarek.blogger.exception.NotFoundPostException;
import com.skwarek.blogger.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
public class PostControllerTests {

    private final static String MAIN_LOCATION_PATH = "http://localhost";

    @MockBean
    private PostService postService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetAllPostsByAccountId() throws Exception {
        Long accountId = 1L;
        List<Post> posts = List.of(
                EmbeddedDatabase.createPostNo(1),
                EmbeddedDatabase.createPostNo(2),
                EmbeddedDatabase.createPostNo(3)
        );
        Post firstPost = posts.get(0);
        Post lastPost = posts.get(posts.size() - 1);

        when(postService.findAllByAccountId(accountId)).thenReturn(posts);

        mockMvc.perform(get("/api/accounts/{accountId}/posts", accountId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].*", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(firstPost.getId()))
                .andExpect(jsonPath("$[0].content").value(firstPost.getContent()))
                .andExpect(jsonPath("$[0].comments").isNotEmpty())
                .andExpect(jsonPath("$[2].*", hasSize(3)))
                .andExpect(jsonPath("$[2].id").value(lastPost.getId()))
                .andExpect(jsonPath("$[2].content").value(lastPost.getContent()))
                .andExpect(jsonPath("$[2].comments").isEmpty());
    }

    @Test
    void shouldGetNoPostsByAccountId() throws Exception {
        Long accountId = 3L;
        List<Post> posts = Collections.emptyList();

        when(postService.findAllByAccountId(accountId)).thenReturn(posts);

        mockMvc.perform(get("/api/accounts/{accountId}/posts", accountId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotGetAllPostsByAccountIdWhenAccountDoesNotExist() throws Exception {
        Long accountId = 0L;
        String expectedMessage = "This account doesn't exist.";

        when(postService.findAllByAccountId(accountId)).thenThrow(NotFoundAccountException.class);
        mockMvc.perform(get("/api/accounts/{accountId}/posts", accountId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedMessage));
    }

    @Test
    void shouldGetPostById() throws Exception {
        Long postId = 1L;
        Post post = EmbeddedDatabase.createPostNo(1);

        when(postService.findById(postId)).thenReturn(post);

        mockMvc.perform(get("/api/posts/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.content").value(post.getContent()))
                .andExpect(jsonPath("$.comments").isNotEmpty());

    }

    @Test
    void shouldNotGetPostByIdWhenPostDoesNotExist() throws Exception {
        Long postId = 0L;
        String expectedMessage = "This post doesn't exist.";

        when(postService.findById(postId)).thenThrow(NotFoundPostException.class);

        mockMvc.perform(get("/api/posts/{postId}", postId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedMessage));
    }

    @Test
    void shouldCreatePost2Account() throws Exception {
        Long accountId = 1L;
        PostRequest postRequest = PostRequest.builder()
                .content("new post")
                .build();
        Post createdPost = Post.builder()
                .id(1L)
                .content("new post")
                .account(EmbeddedDatabase.createAccountNo(1))
                .comments(Collections.emptyList())
                .build();

        when(postService.create2Account(accountId, postRequest)).thenReturn(createdPost);

        mockMvc.perform(post("/api/accounts/{accountId}/posts/create", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string(HttpHeaders.LOCATION, MAIN_LOCATION_PATH + "/api/posts/1"))
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$.id").value(createdPost.getId()))
                .andExpect(jsonPath("$.content").value(createdPost.getContent()))
                .andExpect(jsonPath("$.comments").isEmpty());
    }

    @Test
    void shouldNotCreatePost2AccountWhenAccountDoesNotExist() throws Exception {
        Long accountId = 0L;
        PostRequest accountRequest = PostRequest.builder()
                .content("new post")
                .build();
        String expectedMessage = "This account doesn't exist.";

        when(postService.create2Account(accountId, accountRequest)).thenThrow(NotFoundAccountException.class);

        mockMvc.perform(post("/api/accounts/{accountId}/posts/create", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedMessage));
    }

    @Test
    void shouldUpdatePost() throws Exception {
        Long postId = 1L;
        PostRequest postRequest = PostRequest.builder()
                .content("updated post no 1 to account1")
                .build();
        Post updatedPost = Post.builder()
                .id(1L)
                .content("updated post no 1 to account1")
                .account(EmbeddedDatabase.createPostNo(1).getAccount())
                .comments(EmbeddedDatabase.createPostNo(1).getComments())
                .build();

        when(postService.update(postId, postRequest)).thenReturn(updatedPost);

        mockMvc.perform(put("/api/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$.id").value(updatedPost.getId()))
                .andExpect(jsonPath("$.content").value(updatedPost.getContent()))
                .andExpect(jsonPath("$.comments").isNotEmpty());
    }

    @Test
    void shouldNotUpdatePostWhenPostDoesNotExist() throws Exception {
        Long postId = 0L;
        PostRequest postRequest = PostRequest.builder()
                .content("updated post no 1 to account1")
                .build();
        String expectedMessage = "This post doesn't exist.";

        when(postService.update(postId, postRequest)).thenThrow(NotFoundPostException.class);

        mockMvc.perform(put("/api/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedMessage));
    }

    @Test
    void shouldDeletePostById() throws Exception {
        Long postId = 1L;

        doNothing().when(postService).deleteById(postId);

        mockMvc.perform(delete("/api/posts/{postId}", postId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotDeletePostByIdWhenPostDoesNotExist() throws Exception {
        Long postId = 0L;
        String expectedMessage = "This post doesn't exist.";

        doThrow(NotFoundPostException.class).when(postService).deleteById(postId);

        mockMvc.perform(delete("/api/posts/{postId}", postId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedMessage));
    }

}
