package com.sawoo.pipeline.api.mock;

import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.dto.account.AccountLeadDTO;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.account.AccountStatus;
import com.sawoo.pipeline.api.model.common.Status;
import com.sawoo.pipeline.api.model.company.Company;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class AccountMockFactory extends BaseMockFactory<AccountDTO, Account> {

    @Getter
    private final UserMockFactory userMockFactory;
    @Getter
    private final LeadMockFactory leadMockFactory;
    @Getter
    private final CompanyMockFactory companyMockFactory;

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
                        .value(AccountStatus.ON_BOARDING.getValue()).build());
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        entity.setUpdated(now);
        entity.setCreated(now);
        return entity;

    }

    @Override
    public AccountDTO newDTO(String id) {
        AccountDTO entity = new AccountDTO();
        LocalDateTime dateTime = LocalDateTime.now(ZoneOffset.UTC);
        Faker FAKER = getFAKER();

        entity.setId(id);
        entity.setFullName(FAKER.name().fullName());
        entity.setLinkedInUrl(FAKER.internet().url());
        entity.setPhoneNumber(FAKER.phoneNumber().phoneNumber());
        entity.setEmail(FAKER.internet().emailAddress());
        entity.setPosition(FAKER.company().profession());
        CompanyDTO company = CompanyDTO
                .builder()
                .id(FAKER.internet().uuid())
                .name(FAKER.company().name())
                .url(FAKER.company().url())
                .build();
        company.setCreated(LocalDateTime.of(2019, 12, 1, 10, 0));
        company.setUpdated(LocalDateTime.of(2020, 1, 2, 9, 50));
        entity.setCompany(company);
        entity.setStatus(
                Status
                        .builder()
                        .value(AccountStatus.ON_BOARDING.getValue()).build());
        entity.setUpdated(dateTime);
        entity.setCreated(dateTime);
        return entity;
    }

    @Override
    public AccountDTO newDTO(String id, AccountDTO dto) {
        AccountDTO newDTO = new AccountDTO();
        newDTO.setId(id);

        return dto.toBuilder().id(id).build();
    }


    public AccountLeadDTO newLeadDTO(String id) {
        Faker FAKER = getFAKER();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        AccountLeadDTO dto = AccountLeadDTO.
                builder()
                .id(id)
                .linkedInUrl(FAKER.internet().url())
                .fullName(FAKER.name().fullName())
                .position(FAKER.company().profession())
                .company(CompanyDTO
                        .builder()
                        .name(FAKER.company().name())
                        .url(FAKER.company().url())
                        .id(FAKER.internet().uuid())
                        .headcount(FAKER.number().numberBetween(10, 1000))
                        .build())
                .build();
        dto.setUpdated(now);
        dto.setCreated(now);
        return dto;
    }
}
