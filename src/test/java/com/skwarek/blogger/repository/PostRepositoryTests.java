package com.skwarek.blogger.repository;

import com.skwarek.blogger.EmbeddedDatabase;
import com.skwarek.blogger.domain.Account;
import com.skwarek.blogger.domain.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
public class PostRepositoryTests {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    void shouldFindAllPostsByAccountId() {
        Long accountId = 1L;
        Iterable<Post> postsDb = postRepository.findByAccountId(accountId);

        assertThat(postsDb).hasSize(3)
                .containsOnly(
                        EmbeddedDatabase.createPostNo(1),
                        EmbeddedDatabase.createPostNo(2),
                        EmbeddedDatabase.createPostNo(3)
                );
    }

    @Test
    void shouldFindNoPostsByAccountId() {
        Long accountId = 3L;
        Iterable<Post> postsDb = postRepository.findByAccountId(accountId);

        assertThat(postsDb).isEmpty();
    }

    @Test
    void shouldFindPostById() {
        Long postId = 1L;
        Optional<Post> postDb = postRepository.findById(postId);

        assertThat(postDb).isNotEmpty()
                .hasValue(EmbeddedDatabase.createPostNo(1));
    }

    @Test
    void shouldNotFindPostByIdWhenPostDoesNotExist() {
        Long postId = 0L;
        Optional<Post> postDb = postRepository.findById(postId);

        assertThat(postDb).isEmpty();
    }

    @Test
    void shouldSavePost() {
        Account accountDb = EmbeddedDatabase.createAccountNo(1);
        Post newPost = Post.builder()
                .content("new post to account1")
                .build();
        accountDb.addPost(newPost);
        Post savedPost = postRepository.save(newPost);

        assertThat(postRepository.findAll()).hasSize(5);
        assertThat(accountRepository.findAll()).hasSize(3);
        assertThat(commentRepository.findAll()).hasSize(5);
        assertThat(savedPost).hasFieldOrPropertyWithValue("id", 5L);
        assertThat(savedPost).hasFieldOrPropertyWithValue("content", "new post to account1");
        assertThat(savedPost).hasFieldOrPropertyWithValue("account", accountDb);
        assertThat(savedPost).hasFieldOrProperty("comments");
        assertThat(savedPost.getComments()).containsExactlyElementsOf(Collections.emptyList());
    }

    @Test
    void shouldUpdatePost() {
        Post oldPost = EmbeddedDatabase.createPostNo(1);
        oldPost.setContent("updated post no 1 to account1");
        Post savedPost = postRepository.save(oldPost);

        assertThat(postRepository.findAll()).hasSize(4);
        assertThat(accountRepository.findAll()).hasSize(3);
        assertThat(commentRepository.findAll()).hasSize(5);
        assertThat(savedPost).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(savedPost).hasFieldOrPropertyWithValue("content", "updated post no 1 to account1");
        assertThat(savedPost).hasFieldOrPropertyWithValue("account", oldPost.getAccount());
        assertThat(savedPost).hasFieldOrProperty("comments");
        assertThat(savedPost.getComments()).containsExactlyElementsOf(oldPost.getComments());
    }

    @Test
    void shouldDeletePostByIdWithComments() {
        Long postId = 1L;
        postRepository.deleteById(postId);

        Iterable<Post> postsDb = postRepository.findAll();

        assertThat(accountRepository.findAll()).hasSize(3);
        assertThat(commentRepository.findAll()).hasSize(2);
        assertThat(postsDb).hasSize(3)
                .doesNotContain(
                        EmbeddedDatabase.createPostNo(1)
                )
                .containsOnly(
                        EmbeddedDatabase.createPostNo(2),
                        EmbeddedDatabase.createPostNo(3),
                        EmbeddedDatabase.createPostNo(4)
                );
    }

    @Test
    void shouldDeletePostByIdWithoutComments() {
        Long postId = 3L;
        postRepository.deleteById(postId);

        Iterable<Post> postsDb = postRepository.findAll();

        assertThat(accountRepository.findAll()).hasSize(3);
        assertThat(commentRepository.findAll()).hasSize(5);
        assertThat(postsDb).hasSize(3)
                .doesNotContain(
                        EmbeddedDatabase.createPostNo(3)
                )
                .containsOnly(
                        EmbeddedDatabase.createPostNo(1),
                        EmbeddedDatabase.createPostNo(2),
                        EmbeddedDatabase.createPostNo(4)
                );
    }

}
