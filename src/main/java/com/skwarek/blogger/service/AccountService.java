package com.skwarek.blogger.service;

import com.skwarek.blogger.domain.Account;
import com.skwarek.blogger.dto.AccountRequest;

import java.util.List;

public interface AccountService {

    List<Account> findAll();

    Account findById(Long accountId);

    Account create(AccountRequest accountRequest);

    Account update(Long accountId, AccountRequest accountRequest);

    void deleteById(Long accountId);

}
