package com.sawoo.pipeline.api.mock;

import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.common.contants.DomainConstants;
import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.model.Company;
import com.sawoo.pipeline.api.model.CompanyOld;
import com.sawoo.pipeline.api.model.Status;
import com.sawoo.pipeline.api.model.User;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.client.Client;
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
        Account entity = new Account();
        Faker FAKER = getFAKER();
        entity.setId(id);
        entity.setFullName(FAKER.name().fullName());
        entity.setLinkedInUrl(FAKER.internet().url());
        entity.setEmail(FAKER.internet().emailAddress());
        entity.setPhoneNumber(FAKER.phoneNumber().phoneNumber());
        entity.setPosition(FAKER.company().profession());
        entity.setCompany(
                Company
                        .builder()
                        .id(FAKER.internet().uuid())
                        .name(FAKER.company().name())
                        .url(FAKER.company().url())
                        .created(LocalDateTime.of(2019, 12, 1, 10, 0))
                        .updated(LocalDateTime.of(2020, 1, 2, 9, 50))
                        .build());
        entity.setStatus(
                Status
                        .builder()
                        .value(DomainConstants.ClientStatus.ON_BOARDING.ordinal()).build());
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        entity.setUpdated(now);
        entity.setCreated(now);
        return entity;

    }

    @Override
    public AccountDTO newDTO(String id) {
        return null;
    }
}
