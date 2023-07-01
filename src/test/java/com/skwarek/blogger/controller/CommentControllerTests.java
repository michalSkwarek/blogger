package com.skwarek.blogger.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skwarek.blogger.EmbeddedDatabase;
import com.skwarek.blogger.domain.Comment;
import com.skwarek.blogger.dto.CommentRequest;
import com.skwarek.blogger.exception.NotFoundCommentException;
import com.skwarek.blogger.exception.NotFoundPostException;
import com.skwarek.blogger.service.CommentService;
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

@WebMvcTest(CommentController.class)
public class CommentControllerTests {

    private final static String MAIN_LOCATION_PATH = "http://localhost";

    @MockBean
    private CommentService commentService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetAllCommentsByPostId() throws Exception {
        Long postId = 1L;
        List<Comment> comments = List.of(
                EmbeddedDatabase.createCommentNo(1),
                EmbeddedDatabase.createCommentNo(2),
                EmbeddedDatabase.createCommentNo(3)
        );
        Comment firstComment = comments.get(0);
        Comment lastComment = comments.get(comments.size() - 1);

        when(commentService.findAllByPostId(postId)).thenReturn(comments);

        mockMvc.perform(get("/api/posts/{postId}/comments", postId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].*", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(firstComment.getId()))
                .andExpect(jsonPath("$[0].content").value(firstComment.getContent()))
                .andExpect(jsonPath("$[2].*", hasSize(2)))
                .andExpect(jsonPath("$[2].id").value(lastComment.getId()))
                .andExpect(jsonPath("$[2].content").value(lastComment.getContent()));
    }

    @Test
    void shouldGetNoCommentsByPostId() throws Exception {
        Long postId = 3L;
        List<Comment> comments = Collections.emptyList();

        when(commentService.findAllByPostId(postId)).thenReturn(comments);

        mockMvc.perform(get("/api/posts/{postId}/comments", postId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotGetAllCommentsByPostIdWhenPostDoesNotExist() throws Exception {
        Long postId = 0L;
        String expectedMessage = "This post doesn't exist.";

        when(commentService.findAllByPostId(postId)).thenThrow(NotFoundPostException.class);
        mockMvc.perform(get("/api/posts/{postId}/comments", postId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedMessage));
    }

    @Test
    void shouldGetCommentById() throws Exception {
        Long commentId = 1L;
        Comment comment = EmbeddedDatabase.createCommentNo(1);

        when(commentService.findById(commentId)).thenReturn(comment);

        mockMvc.perform(get("/api/comments/{commentId}", commentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.id").value(comment.getId()))
                .andExpect(jsonPath("$.content").value(comment.getContent()));

    }

    @Test
    void shouldNotGetCommentByIdWhenCommentDoesNotExist() throws Exception {
        Long commentId = 0L;
        String expectedMessage = "This comment doesn't exist.";

        when(commentService.findById(commentId)).thenThrow(NotFoundCommentException.class);

        mockMvc.perform(get("/api/comments/{commentId}", commentId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedMessage));
    }

    @Test
    void shouldCreateComment2Post() throws Exception {
        Long postId = 1L;
        CommentRequest commentRequest = CommentRequest.builder()
                .content("new comment")
                .build();
        Comment createdComment = Comment.builder()
                .id(1L)
                .content("new comment")
                .post(EmbeddedDatabase.createPostNo(1))
                .build();

        when(commentService.create2Post(postId, commentRequest)).thenReturn(createdComment);

        mockMvc.perform(post("/api/posts/{postId}/comments/create", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string(HttpHeaders.LOCATION, MAIN_LOCATION_PATH + "/api/comments/1"))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.id").value(createdComment.getId()))
                .andExpect(jsonPath("$.content").value(createdComment.getContent()));
    }

    @Test
    void shouldNotCreateComment2PostWhenPostDoesNotExist() throws Exception {
        Long postId = 0L;
        CommentRequest commentRequest = CommentRequest.builder()
                .content("new comment")
                .build();
        String expectedMessage = "This post doesn't exist.";

        when(commentService.create2Post(postId, commentRequest)).thenThrow(NotFoundPostException.class);

        mockMvc.perform(post("/api/posts/{postId}/comments/create", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedMessage));
    }

    @Test
    void shouldUpdateComment() throws Exception {
        Long commentId = 1L;
        CommentRequest commentRequest = CommentRequest.builder()
                .content("updated comment no 1 to post1")
                .build();
        Comment updatedComment = Comment.builder()
                .id(1L)
                .content("updated comment no 1 to post1")
                .post(EmbeddedDatabase.createCommentNo(1).getPost())
                .build();

        when(commentService.update(commentId, commentRequest)).thenReturn(updatedComment);

        mockMvc.perform(put("/api/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.id").value(updatedComment.getId()))
                .andExpect(jsonPath("$.content").value(updatedComment.getContent()));
    }

    @Test
    void shouldNotUpdateCommentWhenCommentDoesNotExist() throws Exception {
        Long commentId = 0L;
        CommentRequest commentRequest = CommentRequest.builder()
                .content("updated comment no 1 to post1")
                .build();
        String expectedMessage = "This comment doesn't exist.";

        when(commentService.update(commentId, commentRequest)).thenThrow(NotFoundCommentException.class);

        mockMvc.perform(put("/api/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedMessage));
    }

    @Test
    void shouldDeleteCommentById() throws Exception {
        Long commentId = 1L;

        doNothing().when(commentService).deleteById(commentId);

        mockMvc.perform(delete("/api/comments/{commentId}", commentId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotDeleteCommentByIdWhenCommentDoesNotExist() throws Exception {
        Long commentId = 0L;
        String expectedMessage = "This comment doesn't exist.";

        doThrow(NotFoundCommentException.class).when(commentService).deleteById(commentId);

        mockMvc.perform(delete("/api/comments/{commentId}", commentId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedMessage));
    }

}
