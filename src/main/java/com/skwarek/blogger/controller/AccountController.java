package com.skwarek.blogger.controller;

import com.skwarek.blogger.domain.Account;
import com.skwarek.blogger.dto.AccountRequest;
import com.skwarek.blogger.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping(value = "/api")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping(value = "/accounts")
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.findAll();

        if (!accounts.isEmpty()) {
            return ResponseEntity.ok(accounts);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping(value = "/accounts/{accountId}")
    public ResponseEntity<Account> getAccountById(@PathVariable("accountId") Long accountId) {
        Account account = accountService.findById(accountId);

        return ResponseEntity.ok(account);
    }

    @PostMapping(value = "/accounts/create")
    public ResponseEntity<Account> createAccount(@RequestBody AccountRequest accountRequest) {
        Account createdAccount = accountService.create(accountRequest);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath("/api/accounts")
                .path("/{accountId}")
                .buildAndExpand(createdAccount.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdAccount);
    }

    @PutMapping(value = "/accounts/{accountId}")
    public ResponseEntity<Account> updateAccount(@PathVariable("accountId") Long accountId,
                                                 @RequestBody AccountRequest accountRequest) {
        Account updatedAccount = accountService.update(accountId, accountRequest);

        return ResponseEntity.ok(updatedAccount);
    }

    @DeleteMapping(value = "/accounts/{accountId}")
    public ResponseEntity<HttpStatus> deleteAccountById(@PathVariable("accountId") Long accountId) {
        accountService.deleteById(accountId);

        return ResponseEntity.noContent().build();
    }

}
