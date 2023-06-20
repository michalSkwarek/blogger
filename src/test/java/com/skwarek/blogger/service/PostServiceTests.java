package com.skwarek.blogger.service;

import com.skwarek.blogger.EmbeddedDatabase;
import com.skwarek.blogger.domain.Account;
import com.skwarek.blogger.domain.Post;
import com.skwarek.blogger.dto.PostRequest;
import com.skwarek.blogger.exception.NotFoundAccountException;
import com.skwarek.blogger.exception.NotFoundPostException;
import com.skwarek.blogger.repository.PostRepository;
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

@WebMvcTest(PostService.class)
public class PostServiceTests {

    @MockBean
    private PostRepository postRepository;
    @MockBean
    private AccountService accountService;

    @Autowired
    private PostService postService;

    @Test
    void shouldFindAllPostsByAccountId() {
        Long accountId = 1L;
        Account account = EmbeddedDatabase.createAccountNo(1);
        List<Post> postsDb = List.of(
                EmbeddedDatabase.createPostNo(1),
                EmbeddedDatabase.createPostNo(2),
                EmbeddedDatabase.createPostNo(3),
                EmbeddedDatabase.createPostNo(4)
        );

        when(accountService.findById(accountId)).thenReturn(account);
        when(postRepository.findByAccountId(account.getId())).thenReturn(postsDb);
        List<Post> posts = postService.findAllByAccountId(accountId);

        assertThat(posts).asList().hasSize(4)
                .containsOnly(
                        EmbeddedDatabase.createPostNo(1),
                        EmbeddedDatabase.createPostNo(2),
                        EmbeddedDatabase.createPostNo(3),
                        EmbeddedDatabase.createPostNo(4)
                );
    }

    @Test
    void shouldFindNoPostsByAccountId() {
        Long accountId = 3L;
        Account account = EmbeddedDatabase.createAccountNo(3);
        List<Post> postsDb = Collections.emptyList();

        when(accountService.findById(accountId)).thenReturn(account);
        when(postRepository.findByAccountId(account.getId())).thenReturn(postsDb);
        List<Post> posts = postService.findAllByAccountId(accountId);

        assertThat(posts).asList().isEmpty();
    }

    @Test
    void shouldNotFindPostsByAccountIdWhenAccountDoesNotExist() {
        Long accountId = 0L;
        String expectedMessage = "Not found account with id: " + accountId;

        when(accountService.findById(accountId)).thenThrow(new NotFoundAccountException(expectedMessage));

        Exception exception = assertThrows(NotFoundAccountException.class, () -> accountService.findById(accountId));
        assertThat(exception).hasMessage(expectedMessage);
    }

    @Test
    void shouldFindPostById() {
        Long postId = 1L;
        Optional<Post> postDb = Optional.of(EmbeddedDatabase.createPostNo(1));

        when(postRepository.findById(postId)).thenReturn(postDb);
        Post post = postService.findById(postId);

        assertThat(post).isNotNull()
                .isEqualTo(EmbeddedDatabase.createPostNo(1));

    }

    @Test
    void shouldNotFindPostByIdWhenPostDoesNotExist() {
        Long postId = 0L;
        String expectedMessage = "Not found post with id: " + postId;

        Exception exception = assertThrows(NotFoundPostException.class, () -> postService.findById(postId));
        assertThat(exception).hasMessage(expectedMessage);
    }

    @Test
    void shouldCreatePost2Account() {
        Long accountId = 1L;
        PostRequest postRequest = PostRequest.builder()
                .content("new post to account1")
                .build();
        Account account = EmbeddedDatabase.createAccountNo(1);

        when(accountService.findById(accountId)).thenReturn(account);
        postService.create2Account(accountId, postRequest);
        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postArgumentCaptor.capture());
        Post createdPost = postArgumentCaptor.getValue();

        assertThat(createdPost).hasFieldOrPropertyWithValue("id", null);
        assertThat(createdPost).hasFieldOrPropertyWithValue("content", "new post to account1");
        assertThat(createdPost).hasFieldOrPropertyWithValue("account", account);
        assertThat(createdPost).hasFieldOrPropertyWithValue("comments", Collections.emptyList());
    }

    @Test
    void shouldNotCreatePost2AccountWhenAccountDoesNotExist() {
        Long accountId = 0L;
        PostRequest postRequest = PostRequest.builder()
                .content("new post to account1")
                .build();
        String expectedMessage = "Not found account with id: " + accountId;

        when(accountService.findById(accountId)).thenThrow(new NotFoundAccountException(expectedMessage));

        Exception exception = assertThrows(NotFoundAccountException.class, () -> postService.create2Account(accountId, postRequest));
        assertThat(exception).hasMessage(expectedMessage);
    }

    @Test
    void shouldUpdatePost() {
        Long postId = 1L;
        PostRequest postRequest = PostRequest.builder()
                .content("updated post no 1 to account1")
                .build();
        Optional<Post> oldPost = Optional.of(EmbeddedDatabase.createPostNo(1));

        when(postRepository.findById(postId)).thenReturn(oldPost);
        postService.update(postId, postRequest);
        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postArgumentCaptor.capture());
        Post updatedPost = postArgumentCaptor.getValue();

        assertThat(updatedPost).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(updatedPost).hasFieldOrPropertyWithValue("content", "updated post no 1 to account1");
        assertThat(updatedPost).hasFieldOrPropertyWithValue("account", oldPost.get().getAccount());
        assertThat(updatedPost).hasFieldOrPropertyWithValue("comments", oldPost.get().getComments());
    }

    @Test
    void shouldNotUpdatePostWhenPostDoesNotExist() {
        Long postId = 0L;
        PostRequest postRequest = PostRequest.builder()
                .content("updated post no 1 to account1")
                .build();
        Optional<Post> oldPost = Optional.empty();
        String expectedMessage = "Not found post with id: " + postId;

        when(postRepository.findById(postId)).thenReturn(oldPost);

        Exception exception = assertThrows(NotFoundPostException.class, () -> postService.update(postId, postRequest));
        assertThat(exception).hasMessage(expectedMessage);
    }

    @Test
    void shouldDeletePostById() {
        Long postId = 1L;
        Optional<Post> postDb = Optional.of(EmbeddedDatabase.createPostNo(1));

        when(postRepository.findById(postId)).thenReturn(postDb);
        doNothing().when(postRepository).deleteById(postDb.get().getId());
        postService.deleteById(postId);

        assertThat(postDb.get().getComments()).isEmpty();
        verify(postRepository, times(1)).deleteById(postDb.get().getId());
    }

    @Test
    void shouldNotDeletePostByIdWhenPostDoesNotExist() {
        Long postId = 0L;
        Optional<Post> postDb = Optional.empty();
        String expectedMessage = "Not found post with id: " + postId;

        when(postRepository.findById(postId)).thenReturn(postDb);

        Exception exception = assertThrows(NotFoundPostException.class, () -> postService.deleteById(postId));
        assertThat(exception).hasMessage(expectedMessage);
    }

}
