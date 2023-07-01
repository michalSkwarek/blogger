package com.skwarek.blogger.service;

import com.skwarek.blogger.EmbeddedDatabase;
import com.skwarek.blogger.domain.Comment;
import com.skwarek.blogger.domain.Post;
import com.skwarek.blogger.dto.CommentRequest;
import com.skwarek.blogger.exception.NotFoundCommentException;
import com.skwarek.blogger.exception.NotFoundPostException;
import com.skwarek.blogger.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@WebMvcTest(CommentService.class)
public class CommentServiceTests {

    @MockBean
    private CommentRepository commentRepository;
    @MockBean
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Test
    void shouldFindAllCommentsByPostId() {
        Long postId = 1L;
        Post post = EmbeddedDatabase.createPostNo(1);
        List<Comment> commentsDb = List.of(
                EmbeddedDatabase.createCommentNo(1),
                EmbeddedDatabase.createCommentNo(2),
                EmbeddedDatabase.createCommentNo(3)
        );

        when(postService.findById(postId)).thenReturn(post);
        when(commentRepository.findByPostId(post.getId())).thenReturn(commentsDb);
        List<Comment> comments = commentService.findAllByPostId(postId);

        assertThat(comments).hasSize(3)
                .containsOnly(
                        EmbeddedDatabase.createCommentNo(1),
                        EmbeddedDatabase.createCommentNo(2),
                        EmbeddedDatabase.createCommentNo(3)
                );
    }

    @Test
    void shouldFindNoCommentsByPostId() {
        Long postId = 3L;
        Post post = EmbeddedDatabase.createPostNo(3);
        List<Comment> commentsDb = Collections.emptyList();

        when(postService.findById(postId)).thenReturn(post);
        when(commentRepository.findByPostId(post.getId())).thenReturn(commentsDb);
        List<Comment> comments = commentService.findAllByPostId(postId);

        assertThat(comments).isEmpty();
    }

    @Test
    void shouldNotFindCommentsByPostIdWhenPostDoesNotExist() {
        Long postId = 0L;
        String expectedMessage = "Not found post with id: " + postId;

        when(postService.findById(postId)).thenThrow(new NotFoundPostException(expectedMessage));

        Exception exception = assertThrows(NotFoundPostException.class, () -> postService.findById(postId));
        assertThat(exception).hasMessage(expectedMessage);
    }

    @Test
    void shouldFindCommentById() {
        Long commentId = 1L;
        Optional<Comment> commentDb = Optional.of(EmbeddedDatabase.createCommentNo(1));

        when(commentRepository.findById(commentId)).thenReturn(commentDb);
        Comment comment = commentService.findById(commentId);

        assertThat(comment).isNotNull()
                .isEqualTo(EmbeddedDatabase.createCommentNo(1));

    }

    @Test
    void shouldNotFindCommentByIdWhenCommentDoesNotExist() {
        Long commentId = 0L;
        String expectedMessage = "Not found comment with id: " + commentId;

        Exception exception = assertThrows(NotFoundCommentException.class, () -> commentService.findById(commentId));
        assertThat(exception).hasMessage(expectedMessage);
    }

    @Test
    void shouldCreateComment2Post() {
        Long postId = 1L;
        CommentRequest commentRequest = CommentRequest.builder()
                .content("new comment")
                .build();
        Post post = EmbeddedDatabase.createPostNo(1);

        when(postService.findById(postId)).thenReturn(post);
        commentService.create2Post(postId, commentRequest);
        ArgumentCaptor<Comment> commentArgumentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(commentArgumentCaptor.capture());
        Comment createdComment = commentArgumentCaptor.getValue();

        assertThat(createdComment).hasFieldOrPropertyWithValue("id", null);
        assertThat(createdComment).hasFieldOrPropertyWithValue("content", "new comment");
        assertThat(createdComment).hasFieldOrPropertyWithValue("post", post);
    }

    @Test
    void shouldNotCreateComment2PostWhenPostDoesNotExist() {
        Long postId = 0L;
        CommentRequest commentRequest = CommentRequest.builder()
                .content("new comment")
                .build();
        String expectedMessage = "Not found post with id: " + postId;

        when(postService.findById(postId)).thenThrow(new NotFoundPostException(expectedMessage));

        Exception exception = assertThrows(NotFoundPostException.class, () -> commentService.create2Post(postId, commentRequest));
        assertThat(exception).hasMessage(expectedMessage);
    }

    @Test
    void shouldUpdateComment() {
        Long commentId = 1L;
        CommentRequest commentRequest = CommentRequest.builder()
                .content("updated comment no 1 to post1")
                .build();
        Optional<Comment> oldComment = Optional.of(EmbeddedDatabase.createCommentNo(1));

        when(commentRepository.findById(commentId)).thenReturn(oldComment);
        commentService.update(commentId, commentRequest);
        ArgumentCaptor<Comment> commentArgumentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(commentArgumentCaptor.capture());
        Comment updatedComment = commentArgumentCaptor.getValue();

        assertThat(updatedComment).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(updatedComment).hasFieldOrPropertyWithValue("content", "updated comment no 1 to post1");
        assertThat(updatedComment).hasFieldOrPropertyWithValue("post", oldComment.get().getPost());
    }

    @Test
    void shouldNotUpdateCommentWhenCommentDoesNotExist() {
        Long commentId = 0L;
        CommentRequest commentRequest = CommentRequest.builder()
                .content("updated comment no 1 to post1")
                .build();
        Optional<Comment> oldComment = Optional.empty();
        String expectedMessage = "Not found comment with id: " + commentId;

        when(commentRepository.findById(commentId)).thenReturn(oldComment);

        Exception exception = assertThrows(NotFoundCommentException.class, () -> commentService.update(commentId, commentRequest));
        assertThat(exception).hasMessage(expectedMessage);
    }

    @Test
    void shouldDeleteCommentById() {
        Long commentId = 1L;
        Optional<Comment> commentDb = Optional.of(EmbeddedDatabase.createCommentNo(1));

        when(commentRepository.findById(commentId)).thenReturn(commentDb);
        doNothing().when(commentRepository).deleteById(commentDb.get().getId());
        commentService.deleteById(commentId);

        verify(commentRepository, times(1)).deleteById(commentDb.get().getId());
    }

    @Test
    void shouldNotDeleteCommentByIdWhenCommentDoesNotExist() {
        Long commentId = 0L;
        Optional<Comment> commentDb = Optional.empty();
        String expectedMessage = "Not found comment with id: " + commentId;

        when(commentRepository.findById(commentId)).thenReturn(commentDb);

        Exception exception = assertThrows(NotFoundCommentException.class, () -> commentService.deleteById(commentId));
        assertThat(exception).hasMessage(expectedMessage);
    }

}
