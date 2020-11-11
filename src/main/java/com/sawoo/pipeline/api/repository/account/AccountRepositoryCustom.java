package com.sawoo.pipeline.api.repository.account;

import com.sawoo.pipeline.api.model.account.Account;

import java.util.List;

public interface AccountRepositoryCustom {

    List<Account> searchByFullName(String fullName);
}
