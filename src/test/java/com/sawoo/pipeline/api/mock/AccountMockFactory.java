package com.sawoo.pipeline.api.mock;

import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.model.User;
import com.sawoo.pipeline.api.model.account.Account;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

@Component
public class AccountMockFactory extends BaseMockFactory<AccountDTO, Account> {

    @Override
    public String getComponentId() {
        return getFAKER().internet().uuid();
    }

    @Override
    public Account newEntity(String id) {
        return null;
    }

    @Override
    public AccountDTO newDTO(String id) {
        return null;
    }
}
