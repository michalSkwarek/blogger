package com.skwarek.blogger.repository;

import com.skwarek.blogger.EmbeddedDatabase;
import com.skwarek.blogger.domain.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class AccountRepositoryTests {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PostRepository postRepository;

    @Test
    void shouldFindAllAccounts() {
        Iterable<Account> accountsDb = accountRepository.findAll();

        assertThat(accountsDb).hasSize(3)
                .containsOnly(
                        EmbeddedDatabase.createAccountNo(1),
                        EmbeddedDatabase.createAccountNo(2),
                        EmbeddedDatabase.createAccountNo(3)
                );
    }

    @Test
    @Sql("/sql/cleanup_data.sql")
    void shouldFindNoAccounts() {
        Iterable<Account> accountsDb = accountRepository.findAll();

        assertThat(accountsDb).isEmpty();
    }

    @Test
    void shouldFindAccountById() {
        Long accountId = 1L;
        Optional<Account> accountDb = accountRepository.findById(accountId);

        assertThat(accountDb).isNotEmpty()
                .hasValue(EmbeddedDatabase.createAccountNo(1));
    }

    @Test
    void shouldNotFindAccountByIdWhenAccountDoesNotExist() {
        Long accountId = 0L;
        Optional<Account> accountDb = accountRepository.findById(accountId);

        assertThat(accountDb).isEmpty();
    }

    @Test
    void shouldCreateAccount() {
        Account newAccount = Account.builder()
                .email("newEmail@gmail.com")
                .password("newPassword")
                .build();
        Account savedAccount = accountRepository.save(newAccount);

        assertThat(accountRepository.findAll()).hasSize(4);
        assertThat(postRepository.findAll()).hasSize(4);
        assertThat(savedAccount).hasFieldOrPropertyWithValue("id", 4L);
        assertThat(savedAccount).hasFieldOrPropertyWithValue("email", "newEmail@gmail.com");
        assertThat(savedAccount).hasFieldOrPropertyWithValue("password", "newPassword");
        assertThat(savedAccount).hasFieldOrProperty("posts");
        assertThat(savedAccount.getPosts()).containsExactlyElementsOf(Collections.emptyList());
    }

    @Test
    void shouldUpdateAccount() {
        Account oldAccount = EmbeddedDatabase.createAccountNo(1);
        oldAccount.setEmail("updateda1@gmail.com");
        oldAccount.setPassword("updated111");
        Account savedAccount = accountRepository.save(oldAccount);

        assertThat(accountRepository.findAll()).hasSize(3);
        assertThat(postRepository.findAll()).hasSize(4);
        assertThat(savedAccount).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(savedAccount).hasFieldOrPropertyWithValue("email", "updateda1@gmail.com");
        assertThat(savedAccount).hasFieldOrPropertyWithValue("password", "updated111");
        assertThat(savedAccount).hasFieldOrProperty("posts");
        assertThat(savedAccount.getPosts()).containsExactlyElementsOf(oldAccount.getPosts());
    }

    @Test
    void shouldDeleteAccountByIdWithPosts() {
        Long accountId = 1L;
        accountRepository.deleteById(accountId);

        Iterable<Account> accountsDb = accountRepository.findAll();

        assertThat(postRepository.findAll()).hasSize(1);
        assertThat(accountsDb).hasSize(2)
                .doesNotContain(
                        EmbeddedDatabase.createAccountNo(1)
                )
                .containsOnly(
                        EmbeddedDatabase.createAccountNo(2),
                        EmbeddedDatabase.createAccountNo(3)
                );
    }

    @Test
    void shouldDeleteAccountByIdWithoutPosts() {
        Long accountId = 3L;
        accountRepository.deleteById(accountId);

        Iterable<Account> accountsDb = accountRepository.findAll();

        assertThat(postRepository.findAll()).hasSize(4);
        assertThat(accountsDb).hasSize(2)
                .doesNotContain(
                        EmbeddedDatabase.createAccountNo(3)
                )
                .containsOnly(
                        EmbeddedDatabase.createAccountNo(1),
                        EmbeddedDatabase.createAccountNo(2)
                );
    }

    @Test
    void shouldReturnTrueWhenAccountByEmailExists() {
        String email = "a1@gmail.com";
        boolean isAccountExist = accountRepository.existsByEmail(email);

        assertThat(isAccountExist).isTrue();
    }

    @Test
    void shouldReturnFalseWhenAccountByEmailDoesNotExist() {
        String email = "xxx@gmail.com";
        boolean isAccountExist = accountRepository.existsByEmail(email);

        assertThat(isAccountExist).isFalse();
    }

}
