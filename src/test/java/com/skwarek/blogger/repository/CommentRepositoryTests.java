package com.skwarek.blogger.repository;

import com.skwarek.blogger.EmbeddedDatabase;
import com.skwarek.blogger.domain.Comment;
import com.skwarek.blogger.domain.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CommentRepositoryTests {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;

    @Test
    void shouldFindAllComments() {
        Iterable<Comment> commentsDb = commentRepository.findAll();

        assertThat(commentsDb).hasSize(5)
                .containsOnly(
                        EmbeddedDatabase.createCommentNo(1),
                        EmbeddedDatabase.createCommentNo(2),
                        EmbeddedDatabase.createCommentNo(3),
                        EmbeddedDatabase.createCommentNo(4),
                        EmbeddedDatabase.createCommentNo(5)
                );
    }

    @Test
    @Sql("/sql/cleanup_data.sql")
    void shouldFindNoComments() {
        Iterable<Comment> commentsDb = commentRepository.findAll();

        assertThat(commentsDb).isEmpty();
    }

    @Test
    void shouldFindCommentById() {
        Long commentId = 1L;
        Optional<Comment> commentDb = commentRepository.findById(commentId);

        assertThat(commentDb).isNotEmpty()
                .hasValue(EmbeddedDatabase.createCommentNo(1));
    }

    @Test
    void shouldNotFindCommentByIdWhenCommentDoesNotExist() {
        Long commentId = 0L;
        Optional<Comment> commentDb = commentRepository.findById(commentId);

        assertThat(commentDb).isEmpty();
    }

    @Test
    void shouldReturnTrueWhenCommentByIdExists() {
        Long commentId = 1L;
        boolean isCommentExist = commentRepository.existsById(commentId);

        assertThat(isCommentExist).isTrue();
    }

    @Test
    void shouldReturnFalseWhenCommentByIdDoesNotExist() {
        Long commentId = 0L;
        boolean isCommentExist = commentRepository.existsById(commentId);

        assertThat(isCommentExist).isFalse();
    }

    @Test
    void shouldSaveComment() {
        Post postDb = EmbeddedDatabase.createPostNo(1);
        Comment newComment = Comment.builder()
                .content("new comment to post1")
                .build();
        postDb.addComment(newComment);
        Comment savedComment = commentRepository.save(newComment);

        assertThat(commentRepository.findAll()).hasSize(6);
        assertThat(postRepository.findAll()).hasSize(4);
        assertThat(savedComment).hasFieldOrPropertyWithValue("id", 6L);
        assertThat(savedComment).hasFieldOrPropertyWithValue("content", "new comment to post1");
        assertThat(savedComment).hasFieldOrPropertyWithValue("post", postDb);
    }

    @Test
    void shouldUpdateComment() {
        Comment oldComment = EmbeddedDatabase.createCommentNo(1);
        oldComment.setContent("updated comment no 1 to post1");
        Comment savedComment = commentRepository.save(oldComment);

        assertThat(commentRepository.findAll()).hasSize(5);
        assertThat(postRepository.findAll()).hasSize(4);
        assertThat(savedComment).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(savedComment).hasFieldOrPropertyWithValue("content", "updated comment no 1 to post1");
        assertThat(savedComment).hasFieldOrPropertyWithValue("post", oldComment.getPost());
    }

    @Test
    void shouldDeleteCommentById() {
        Long commentId = 1L;
        commentRepository.deleteById(commentId);

        Iterable<Comment> commentsDb = commentRepository.findAll();

        assertThat(postRepository.findAll()).hasSize(4);
        assertThat(commentsDb).hasSize(4)
                .doesNotContain(
                        EmbeddedDatabase.createCommentNo(1)
                )
                .containsOnly(
                        EmbeddedDatabase.createCommentNo(2),
                        EmbeddedDatabase.createCommentNo(3),
                        EmbeddedDatabase.createCommentNo(4),
                        EmbeddedDatabase.createCommentNo(5)
                );
    }

    @Test
    void shouldFindAllCommentsByPostId() {
        Long postId = 1L;
        Iterable<Comment> commentsDb = commentRepository.findByPostId(postId);

        assertThat(commentsDb).hasSize(3)
                .containsOnly(
                        EmbeddedDatabase.createCommentNo(1),
                        EmbeddedDatabase.createCommentNo(2),
                        EmbeddedDatabase.createCommentNo(3)
                );
    }

    @Test
    void shouldFindNoCommentsByPostId() {
        Long postId = 3L;
        Iterable<Comment> commentsDb = commentRepository.findByPostId(postId);

        assertThat(commentsDb).isEmpty();
    }

}
