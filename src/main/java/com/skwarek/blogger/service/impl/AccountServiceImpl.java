package com.skwarek.blogger.service.impl;

import com.skwarek.blogger.domain.Account;
import com.skwarek.blogger.domain.Post;
import com.skwarek.blogger.dto.AccountRequest;
import com.skwarek.blogger.exception.DuplicateAccountException;
import com.skwarek.blogger.exception.NotFoundAccountException;
import com.skwarek.blogger.repository.AccountRepository;
import com.skwarek.blogger.service.AccountService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account findById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundAccountException("Not found account with id: " + accountId));
    }

    @Override
    public Account create(AccountRequest accountRequest) {
        boolean isAccountExist = accountRepository.existsByEmail(accountRequest.getEmail());

        if (!isAccountExist) {
            Account newAccount = Account.builder()
                    .email(accountRequest.getEmail())
                    .password(accountRequest.getPassword())
                    .build();

            return accountRepository.save(newAccount);
        } else {
            throw new DuplicateAccountException("Duplicate account with email: " + accountRequest.getEmail());
        }
    }

    @Override
    public Account update(Long accountId, AccountRequest accountRequest) {
        Account oldAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundAccountException("Not found account with id: " + accountId));

        boolean isAccountExist = accountRepository.existsByEmail(accountRequest.getEmail());

        if (oldAccount.getEmail().equals(accountRequest.getEmail()) || !isAccountExist) {
            oldAccount.setEmail(accountRequest.getEmail());
            oldAccount.setPassword(accountRequest.getPassword());

            return accountRepository.save(oldAccount);
        } else {
            throw new DuplicateAccountException("Duplicate account with email: " + accountRequest.getEmail());
        }
    }

    @Override
    public void deleteById(Long accountId) {
        Account accountDb = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundAccountException("Not found account with id: " + accountId));

        List<Post> posts = new ArrayList<>(accountDb.getPosts());
        posts.forEach(accountDb::removePost);

        accountRepository.deleteById(accountDb.getId());
    }

}
