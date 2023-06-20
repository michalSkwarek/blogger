package com.skwarek.blogger.service;

import com.skwarek.blogger.EmbeddedDatabase;
import com.skwarek.blogger.domain.Account;
import com.skwarek.blogger.dto.AccountRequest;
import com.skwarek.blogger.exception.DuplicateAccountException;
import com.skwarek.blogger.exception.NotFoundAccountException;
import com.skwarek.blogger.repository.AccountRepository;
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

@WebMvcTest(AccountService.class)
public class AccountServiceTests {

    @MockBean
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @Test
    void shouldFindAllAccounts() {
        List<Account> accountsDb = List.of(
                EmbeddedDatabase.createAccountNo(1),
                EmbeddedDatabase.createAccountNo(2),
                EmbeddedDatabase.createAccountNo(3)
        );

        when(accountRepository.findAll()).thenReturn(accountsDb);
        List<Account> accounts = accountService.findAll();

        assertThat(accounts).asList().hasSize(3)
                .containsOnly(
                        EmbeddedDatabase.createAccountNo(1),
                        EmbeddedDatabase.createAccountNo(2),
                        EmbeddedDatabase.createAccountNo(3)
                );
    }

    @Test
    void shouldFindNoAccounts() {
        List<Account> accountsDb = Collections.emptyList();

        when(accountRepository.findAll()).thenReturn(accountsDb);
        List<Account> accounts = accountService.findAll();

        assertThat(accounts).asList().isEmpty();
    }

    @Test
    void shouldFindAccountById() {
        Long accountId = 1L;
        Optional<Account> accountDb = Optional.of(EmbeddedDatabase.createAccountNo(1));

        when(accountRepository.findById(accountId)).thenReturn(accountDb);
        Account account = accountService.findById(accountId);

        assertThat(account).isNotNull()
                .isEqualTo(EmbeddedDatabase.createAccountNo(1));

    }

    @Test
    void shouldNotFindAccountByIdWhenAccountDoesNotExist() {
        Long accountId = 0L;
        String expectedMessage = "Not found account with id: " + accountId;

        Exception exception = assertThrows(NotFoundAccountException.class, () -> accountService.findById(accountId));
        assertThat(exception).hasMessage(expectedMessage);
    }

    @Test
    void shouldCreateAccount() {
        AccountRequest accountRequest = AccountRequest.builder()
                .email("newEmail@gmail.com")
                .password("newPassword")
                .build();

        when(accountRepository.existsByEmail(accountRequest.getEmail())).thenReturn(false);
        accountService.create(accountRequest);
        ArgumentCaptor<Account> accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountArgumentCaptor.capture());
        Account createdAccount = accountArgumentCaptor.getValue();

        assertThat(createdAccount).hasFieldOrPropertyWithValue("id", null);
        assertThat(createdAccount).hasFieldOrPropertyWithValue("email", "newEmail@gmail.com");
        assertThat(createdAccount).hasFieldOrPropertyWithValue("password", "newPassword");
        assertThat(createdAccount).hasFieldOrPropertyWithValue("posts", Collections.emptyList());
    }

    @Test
    void shouldNotCreateAccountWhenAccountAlreadyExists() {
        AccountRequest accountRequest = AccountRequest.builder()
                .email("newEmail@gmail.com")
                .password("newPassword")
                .build();
        String expectedMessage = "Duplicate account with email: " + accountRequest.getEmail();

        when(accountRepository.existsByEmail(accountRequest.getEmail())).thenReturn(true);

        Exception exception = assertThrows(DuplicateAccountException.class, () -> accountService.create(accountRequest));
        assertThat(exception).hasMessage(expectedMessage);
    }

    @Test
    void shouldUpdateAccount() {
        Long accountId = 1L;
        AccountRequest accountRequest = AccountRequest.builder()
                .email("updateda1@gmail.com")
                .password("updated111")
                .build();
        Optional<Account> oldAccount = Optional.of(EmbeddedDatabase.createAccountNo(1));

        when(accountRepository.findById(accountId)).thenReturn(oldAccount);
        when(accountRepository.existsByEmail(accountRequest.getEmail())).thenReturn(false);
        accountService.update(accountId, accountRequest);
        ArgumentCaptor<Account> accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountArgumentCaptor.capture());
        Account updatedAccount = accountArgumentCaptor.getValue();

        assertThat(updatedAccount).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(updatedAccount).hasFieldOrPropertyWithValue("email", "updateda1@gmail.com");
        assertThat(updatedAccount).hasFieldOrPropertyWithValue("password", "updated111");
        assertThat(updatedAccount).hasFieldOrPropertyWithValue("posts", oldAccount.get().getPosts());
    }

    @Test
    void shouldNotUpdateAccountWhenAccountDoesNotExist() {
        Long accountId = 0L;
        AccountRequest accountRequest = AccountRequest.builder()
                .email("updateda1@gmail.com")
                .password("updated111")
                .build();
        Optional<Account> oldAccount = Optional.empty();
        String expectedMessage = "Not found account with id: " + accountId;

        when(accountRepository.findById(accountId)).thenReturn(oldAccount);

        Exception exception = assertThrows(NotFoundAccountException.class, () -> accountService.update(accountId, accountRequest));
        assertThat(exception).hasMessage(expectedMessage);
    }

    @Test
    void shouldNotUpdateAccountWhenAccountAlreadyExists() {
        Long accountId = 1L;
        AccountRequest accountRequest = AccountRequest.builder()
                .email("b2@gmail.com")
                .password("updated111")
                .build();
        Optional<Account> oldAccount = Optional.of(EmbeddedDatabase.createAccountNo(1));
        String expectedMessage = "Duplicate account with name: " + accountRequest.getEmail();

        when(accountRepository.findById(accountId)).thenReturn(oldAccount);
        when(accountRepository.existsByEmail(accountRequest.getEmail())).thenReturn(true);

        Exception exception = assertThrows(DuplicateAccountException.class, () -> accountService.update(accountId, accountRequest));
        assertThat(exception).hasMessage(expectedMessage);
    }

    @Test
    void shouldDeleteAccountById() {
        Long accountId = 1L;
        Optional<Account> accountDb = Optional.of(EmbeddedDatabase.createAccountNo(1));

        when(accountRepository.findById(accountId)).thenReturn(accountDb);
        doNothing().when(accountRepository).deleteById(accountDb.get().getId());
        accountService.deleteById(accountId);

        assertThat(accountDb.get().getPosts()).isEmpty();
        verify(accountRepository, times(1)).deleteById(accountDb.get().getId());
    }

    @Test
    void shouldNotDeleteAccountByIdWhenAccountDoesNotExist() {
        Long accountId = 0L;
        Optional<Account> accountDb = Optional.empty();
        String expectedMessage = "Not found account with id: " + accountId;

        when(accountRepository.findById(accountId)).thenReturn(accountDb);

        Exception exception = assertThrows(NotFoundAccountException.class, () -> accountService.deleteById(accountId));
        assertThat(exception).hasMessage(expectedMessage);
    }

}
