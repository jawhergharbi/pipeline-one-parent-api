package com.sawoo.pipeline.api.common;


import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.dto.client.ClientBaseDTO;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.dto.prospect.LeadDTOOld;
import com.sawoo.pipeline.api.dto.prospect.LeadMainDTO;
import com.sawoo.pipeline.api.model.CompanyOld;
import com.sawoo.pipeline.api.model.account.AccountStatus;
import com.sawoo.pipeline.api.model.client.Client;
import com.sawoo.pipeline.api.model.common.Status;
import com.sawoo.pipeline.api.model.company.Company;
import com.sawoo.pipeline.api.model.prospect.LeadOld;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class MockFactory {

    final Faker FAKER = Faker.instance();

    public LeadDTOOld newLeadDTO(String fullName, String linkedInUrl, String linkedInThread, boolean addCompany) {
        LeadDTOOld mockEntityDTO = new LeadDTOOld();
        mockEntityDTO.setFullName(fullName);
        mockEntityDTO.setLinkedInUrl(linkedInUrl);
        mockEntityDTO.setLinkedInThread(linkedInThread);
        mockEntityDTO.setPhoneNumber(FAKER.phoneNumber().phoneNumber());
        mockEntityDTO.setEmail(FAKER.internet().emailAddress());
        mockEntityDTO.setPosition(FAKER.company().profession());
        if (addCompany) {
            mockEntityDTO.setCompany(
                    CompanyDTO.builder()
                            .name(FAKER.company().name())
                            .url(FAKER.company().url())
                            .build());
        }
        return mockEntityDTO;
    }

    public LeadDTOOld newLeadDTO(String firstName, String lastName, String linkedInUrl, String linkedInThread, boolean addCompany) {
        LeadDTOOld mockEntityDTO = new LeadDTOOld();
        mockEntityDTO.setFirstName(firstName);
        mockEntityDTO.setLastName(lastName);
        mockEntityDTO.setFullName(String.join(" ", firstName, lastName));
        mockEntityDTO.setLinkedInUrl(linkedInUrl);
        mockEntityDTO.setLinkedInThread(linkedInThread);
        mockEntityDTO.setPhoneNumber(FAKER.phoneNumber().phoneNumber());
        mockEntityDTO.setEmail(FAKER.internet().emailAddress());
        mockEntityDTO.setPosition(FAKER.company().profession());
        if (addCompany) {
            mockEntityDTO.setCompany(
                    CompanyDTO.builder()
                            .name(FAKER.company().name())
                            .url(FAKER.company().url())
                            .build());
        }
        return mockEntityDTO;
    }

    public LeadDTOOld newLeadDTO(Long leadId, String fullName, String linkedInUrl, String linkedInThread, boolean addCompany) {
        LeadDTOOld mockEntityDTO = new LeadDTOOld();
        mockEntityDTO.setId(leadId);
        mockEntityDTO.setFullName(fullName);
        mockEntityDTO.setLinkedInUrl(linkedInUrl);
        mockEntityDTO.setLinkedInThread(linkedInThread);
        mockEntityDTO.setPhoneNumber(FAKER.phoneNumber().phoneNumber());
        mockEntityDTO.setEmail(FAKER.internet().emailAddress());
        mockEntityDTO.setPosition(FAKER.company().profession());
        if (addCompany) {
            mockEntityDTO.setCompany(
                    CompanyDTO.builder()
                            .name(FAKER.company().name())
                            .url(FAKER.company().url())
                            .build());
        }
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        mockEntityDTO.setCreated(now);
        mockEntityDTO.setUpdated(now);
        return mockEntityDTO;
    }

    public LeadDTOOld newLeadDTO(Long leadId, boolean addCompany) {
        LeadDTOOld mockEntityDTO = new LeadDTOOld();
        mockEntityDTO.setId(leadId);
        mockEntityDTO.setFullName(FAKER.name().fullName());
        mockEntityDTO.setLinkedInUrl(FAKER.internet().url());
        mockEntityDTO.setLinkedInThread(FAKER.internet().url());
        mockEntityDTO.setPhoneNumber(FAKER.phoneNumber().phoneNumber());
        mockEntityDTO.setEmail(FAKER.internet().emailAddress());
        mockEntityDTO.setPosition(FAKER.company().profession());
        if (addCompany) {
            mockEntityDTO.setCompany(
                    CompanyDTO.builder()
                            .name(FAKER.company().name())
                            .url(FAKER.company().url())
                            .build());
        }
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        mockEntityDTO.setCreated(now);
        mockEntityDTO.setUpdated(now);
        return mockEntityDTO;
    }

    public LeadMainDTO newLeadMainDTO(Long leadId, boolean addCompany, ClientBaseDTO client) {
        LeadMainDTO mockEntityDTO = new LeadMainDTO();
        mockEntityDTO.setId(leadId);
        mockEntityDTO.setFullName(FAKER.name().fullName());
        mockEntityDTO.setLinkedInUrl(FAKER.internet().url());
        mockEntityDTO.setLinkedInThread(FAKER.internet().url());
        mockEntityDTO.setPhoneNumber(FAKER.phoneNumber().phoneNumber());
        mockEntityDTO.setEmail(FAKER.internet().emailAddress());
        mockEntityDTO.setPosition(FAKER.company().profession());
        if (addCompany) {
            mockEntityDTO.setCompany(
                    CompanyDTO.builder()
                            .name(FAKER.company().name())
                            .url(FAKER.company().url())
                            .build());
        }
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        mockEntityDTO.setCreated(now);
        mockEntityDTO.setUpdated(now);
        mockEntityDTO.setClient(client);
        return mockEntityDTO;
    }

    public LeadDTOOld newLeadDTO(Long leadId, String fullName, String linkedInUrl, String linkedInThread, CompanyDTO company) {
        LeadDTOOld mockEntityDTO = new LeadDTOOld();
        mockEntityDTO.setId(leadId);
        mockEntityDTO.setFullName(fullName);
        mockEntityDTO.setLinkedInUrl(linkedInUrl);
        mockEntityDTO.setLinkedInThread(linkedInThread);
        mockEntityDTO.setPhoneNumber(FAKER.phoneNumber().phoneNumber());
        mockEntityDTO.setEmail(FAKER.internet().emailAddress());
        mockEntityDTO.setPosition(FAKER.company().profession());
        mockEntityDTO.setCompany(company);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        mockEntityDTO.setCreated(now);
        mockEntityDTO.setUpdated(now);
        return mockEntityDTO;
    }

    public LeadOld newLeadEntity(Long id, String firstName, String lastName, String linkedInUrl, String linkedInThread, boolean addCompany) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        LeadOld mockedEntity = new LeadOld();
        mockedEntity.setId(id);
        mockedEntity.setFirstName(firstName);
        mockedEntity.setLastName(lastName);
        mockedEntity.setLinkedInUrl(linkedInUrl);
        mockedEntity.setLinkedInThread(linkedInThread);
        mockedEntity.setEmail(FAKER.internet().emailAddress());
        mockedEntity.setPhoneNumber(FAKER.phoneNumber().phoneNumber());
        mockedEntity.setPosition(FAKER.company().profession());
        if (addCompany) {
            mockedEntity.setCompany(Company
                    .builder()
                    .id(FAKER.internet().uuid())
                    .name(FAKER.company().name())
                    .url(FAKER.company().url())
                    .build());
        }
        mockedEntity.setCreated(now);
        mockedEntity.setUpdated(now);
        return mockedEntity;
    }

    public LeadOld newLeadEntity(Long id, boolean addCompany) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        LeadOld mockedEntity = new LeadOld();
        mockedEntity.setId(id);
        mockedEntity.setFirstName(FAKER.name().firstName());
        mockedEntity.setFirstName(FAKER.name().lastName());
        mockedEntity.setLinkedInUrl(FAKER.internet().url());
        mockedEntity.setLinkedInThread(FAKER.internet().url());
        mockedEntity.setEmail(FAKER.internet().emailAddress());
        mockedEntity.setPhoneNumber(FAKER.phoneNumber().phoneNumber());
        mockedEntity.setPosition(FAKER.company().profession());
        if (addCompany) {
            mockedEntity.setCompany(Company
                    .builder()
                    .id(FAKER.internet().uuid())
                    .name(FAKER.company().name())
                    .url(FAKER.company().url())
                    .build());
        }
        mockedEntity.setCreated(now);
        mockedEntity.setUpdated(now);
        return mockedEntity;
    }

    public CompanyOld newCompanyEntity(LocalDateTime dateTime) {
        return CompanyOld.builder()
                .id(FAKER.number().randomNumber())
                .name(FAKER.company().name())
                .url(FAKER.company().url())
                .updated(dateTime)
                .created(dateTime)
                .build();
    }

    public Company newCompanyEntity(String id, String name, String url, LocalDateTime dateTime) {
        return Company.builder()
                .id(id)
                .name(name)
                .url(url)
                .created(dateTime)
                .updated(dateTime)
                .build();
    }

    public Client newClientEntity(Long id) {
        Client client = new Client();
        client.setId(id);
        client.setFullName(FAKER.name().fullName());
        client.setLinkedInUrl(FAKER.internet().url());
        client.setEmail(FAKER.internet().emailAddress());
        client.setPhoneNumber(FAKER.phoneNumber().phoneNumber());
        client.setPosition(FAKER.company().profession());
        client.setCompany(
                CompanyOld
                        .builder()
                        .name(FAKER.company().name())
                        .url(FAKER.company().url())
                        .created(LocalDateTime.of(2019, 12, 1, 10, 0))
                        .updated(LocalDateTime.of(2020, 1, 2, 9, 50))
                        .build());
        client.setStatus(
                Status
                        .builder()
                        .value(AccountStatus.ON_BOARDING.getValue()).build());
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        client.setUpdated(now);
        client.setCreated(now);
        return client;
    }

    public Client newClientEntity(Long id, String fullName, String linkedInUrl, boolean addCompany) {
        Client mockedEntity = new Client();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        mockedEntity.setId(id);
        mockedEntity.setFullName(fullName);
        mockedEntity.setLinkedInUrl(linkedInUrl);
        mockedEntity.setEmail(FAKER.internet().emailAddress());
        mockedEntity.setPhoneNumber(FAKER.phoneNumber().phoneNumber());
        mockedEntity.setPosition(FAKER.company().profession());
        if (addCompany) {
            mockedEntity.setCompany(newCompanyEntity(now));
        }
        mockedEntity.setCreated(now);
        mockedEntity.setUpdated(now);
        return mockedEntity;
    }
}
