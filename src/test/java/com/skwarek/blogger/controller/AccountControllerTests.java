package com.skwarek.blogger.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skwarek.blogger.EmbeddedDatabase;
import com.skwarek.blogger.domain.Account;
import com.skwarek.blogger.dto.AccountRequest;
import com.skwarek.blogger.exception.DuplicateAccountException;
import com.skwarek.blogger.exception.NotFoundAccountException;
import com.skwarek.blogger.service.AccountService;
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

@WebMvcTest(AccountController.class)
public class AccountControllerTests {

    private final static String MAIN_LOCATION_PATH = "http://localhost";

    @MockBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetAllAccounts() throws Exception {
        List<Account> accounts = List.of(
                EmbeddedDatabase.createAccountNo(1),
                EmbeddedDatabase.createAccountNo(2),
                EmbeddedDatabase.createAccountNo(3)
        );
        Account firstAccount = accounts.get(0);
        Account lastAccount = accounts.get(accounts.size() - 1);

        when(accountService.findAll()).thenReturn(accounts);

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].*", hasSize(4)))
                .andExpect(jsonPath("$[0].id").value(firstAccount.getId()))
                .andExpect(jsonPath("$[0].email").value(firstAccount.getEmail()))
                .andExpect(jsonPath("$[0].password").value(firstAccount.getPassword()))
                .andExpect(jsonPath("$[0].posts").isNotEmpty())
                .andExpect(jsonPath("$[2].*", hasSize(4)))
                .andExpect(jsonPath("$[2].id").value(lastAccount.getId()))
                .andExpect(jsonPath("$[2].email").value(lastAccount.getEmail()))
                .andExpect(jsonPath("$[2].password").value(lastAccount.getPassword()))
                .andExpect(jsonPath("$[2].posts").isEmpty());
    }

    @Test
    void shouldGetNoAccounts() throws Exception {
        List<Account> accounts = Collections.emptyList();

        when(accountService.findAll()).thenReturn(accounts);

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldGetAccountById() throws Exception {
        Long accountId = 1L;
        Account account = EmbeddedDatabase.createAccountNo(1);

        when(accountService.findById(accountId)).thenReturn(account);

        mockMvc.perform(get("/api/accounts/{accountId}", accountId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.id").value(account.getId()))
                .andExpect(jsonPath("$.email").value(account.getEmail()))
                .andExpect(jsonPath("$.password").value(account.getPassword()))
                .andExpect(jsonPath("$.posts").isNotEmpty());

    }

    @Test
    void shouldNotGetAccountByIdWhenAccountDoesNotExist() throws Exception {
        Long accountId = 0L;
        String expectedMessage = "This account doesn't exist.";

        when(accountService.findById(accountId)).thenThrow(NotFoundAccountException.class);

        mockMvc.perform(get("/api/accounts/{accountId}", accountId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedMessage));
    }

    @Test
    void shouldCreateAccount() throws Exception {
        AccountRequest accountRequest = AccountRequest.builder()
                .email("newEmail@gmail.com")
                .password("newPassword")
                .build();
        Account createdAccount = Account.builder()
                .id(1L)
                .email("newEmail@gmail.com")
                .password("newPassword")
                .posts(Collections.emptyList())
                .build();

        when(accountService.create(accountRequest)).thenReturn(createdAccount);

        mockMvc.perform(post("/api/accounts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string(HttpHeaders.LOCATION, MAIN_LOCATION_PATH + "/api/accounts/1"))
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.id").value(createdAccount.getId()))
                .andExpect(jsonPath("$.email").value(createdAccount.getEmail()))
                .andExpect(jsonPath("$.password").value(createdAccount.getPassword()))
                .andExpect(jsonPath("$.posts").isEmpty());
    }

    @Test
    void shouldNotCreateAccountWhenAccountAlreadyExists() throws Exception {
        AccountRequest accountRequest = AccountRequest.builder()
                .email("a1@gmail.com")
                .password("newPassword")
                .build();
        String expectedMessage = "This account already exists.";

        when(accountService.create(accountRequest)).thenThrow(DuplicateAccountException.class);

        mockMvc.perform(post("/api/accounts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedMessage));
    }

    @Test
    void shouldUpdateAccount() throws Exception {
        Long accountId = 1L;
        AccountRequest accountRequest = AccountRequest.builder()
                .email("updateda1@gmail.com")
                .password("updated111")
                .build();
        Account updatedAccount = Account.builder()
                .id(1L)
                .email("updateda1@gmail.com")
                .password("updated111")
                .posts(EmbeddedDatabase.createAccountNo(1).getPosts())
                .build();

        when(accountService.update(accountId, accountRequest)).thenReturn(updatedAccount);

        mockMvc.perform(put("/api/accounts/{accountId}", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.id").value(updatedAccount.getId()))
                .andExpect(jsonPath("$.email").value(updatedAccount.getEmail()))
                .andExpect(jsonPath("$.password").value(updatedAccount.getPassword()))
                .andExpect(jsonPath("$.posts").isNotEmpty());
    }

    @Test
    void shouldNotUpdateAccountWhenAccountDoesNotExist() throws Exception {
        Long accountId = 0L;
        AccountRequest accountRequest = AccountRequest.builder()
                .email("updateda1@gmail.com")
                .password("updated111")
                .build();
        String expectedMessage = "This account doesn't exist.";

        when(accountService.update(accountId, accountRequest)).thenThrow(NotFoundAccountException.class);

        mockMvc.perform(put("/api/accounts/{accountId}", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedMessage));
    }

    @Test
    void shouldNotUpdateAccountWhenAccountAlreadyExists() throws Exception {
        Long accountId = 1L;
        AccountRequest accountRequest = AccountRequest.builder()
                .email("b2@gmail.com")
                .password("updated111")
                .build();
        String expectedMessage = "This account already exists.";

        when(accountService.update(accountId, accountRequest)).thenThrow(DuplicateAccountException.class);
        mockMvc.perform(put("/api/accounts/{accountId}", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedMessage));
    }

    @Test
    void shouldDeleteAccountById() throws Exception {
        Long accountId = 1L;

        doNothing().when(accountService).deleteById(accountId);

        mockMvc.perform(delete("/api/accounts/{accountId}", accountId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotDeleteAccountByIdWhenAccountDoesNotExist() throws Exception {
        Long accountId = 0L;
        String expectedMessage = "This account doesn't exist.";

        doThrow(NotFoundAccountException.class).when(accountService).deleteById(accountId);

        mockMvc.perform(delete("/api/accounts/{accountId}", accountId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedMessage));
    }

}
