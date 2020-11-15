package com.sawoo.pipeline.api.common;


import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.common.contants.DomainConstants;
import com.sawoo.pipeline.api.dto.client.ClientBaseDTO;
import com.sawoo.pipeline.api.dto.client.ClientBasicDTO;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.dto.prospect.LeadDTO;
import com.sawoo.pipeline.api.dto.prospect.LeadMainDTO;
import com.sawoo.pipeline.api.dto.user.UserDTOOld;
import com.sawoo.pipeline.api.model.Company;
import com.sawoo.pipeline.api.model.CompanyOld;
import com.sawoo.pipeline.api.model.Status;
import com.sawoo.pipeline.api.model.UserOld;
import com.sawoo.pipeline.api.model.client.Client;
import com.sawoo.pipeline.api.model.prospect.Lead;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashSet;

@Component
public class MockFactory {

    final Faker FAKER = Faker.instance();

    public LeadDTO newLeadDTO(String fullName, String linkedInUrl, String linkedInThread, boolean addCompany) {
        LeadDTO mockEntityDTO = new LeadDTO();
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

    public LeadDTO newLeadDTO(String firstName, String lastName, String linkedInUrl, String linkedInThread, boolean addCompany) {
        LeadDTO mockEntityDTO = new LeadDTO();
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

    public LeadDTO newLeadDTO(Long leadId, String fullName, String linkedInUrl, String linkedInThread, boolean addCompany) {
        LeadDTO mockEntityDTO = new LeadDTO();
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

    public LeadDTO newLeadDTO(Long leadId, boolean addCompany) {
        LeadDTO mockEntityDTO = new LeadDTO();
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

    public LeadDTO newLeadDTO(Long leadId, String fullName, String linkedInUrl, String linkedInThread, CompanyDTO company) {
        LeadDTO mockEntityDTO = new LeadDTO();
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

    public Lead newLeadEntity(Long id, String firstName, String lastName, String linkedInUrl, String linkedInThread, boolean addCompany) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        Lead mockedEntity = new Lead();
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

    public Lead newLeadEntity(Long id, boolean addCompany) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        Lead mockedEntity = new Lead();
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

    public CompanyDTO newCompanyDTO(LocalDateTime dateTime) {
        return newCompanyDTO(FAKER.internet().uuid(), FAKER.company().name(), FAKER.company().url(), dateTime);
    }

    public CompanyDTO newCompanyDTO(String id, String name, String url, LocalDateTime dateTime) {
        return CompanyDTO.builder()
                .id(id)
                .name(name)
                .url(url)
                .updated(dateTime)
                .created(dateTime)
                .build();
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
                        .value(DomainConstants.ClientStatus.ON_BOARDING.ordinal()).build());
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

    public ClientBasicDTO newClientDTO(Long id, String fullName, String linkedInUrl, boolean addCompany) {
        LocalDateTime dateTime = LocalDateTime.of(2020, 12, 1, 1, 30);
        ClientBasicDTO mockedDTO = new ClientBasicDTO();
        mockedDTO.setId(id);
        mockedDTO.setFullName(fullName);
        mockedDTO.setLinkedInUrl(linkedInUrl);
        mockedDTO.setEmail(FAKER.internet().emailAddress());
        mockedDTO.setPhoneNumber(FAKER.phoneNumber().phoneNumber());
        mockedDTO.setPosition(FAKER.company().profession());
        if (addCompany) {
            mockedDTO.setCompany(newCompanyDTO(dateTime));
        }
        mockedDTO.setCreated(dateTime);
        mockedDTO.setUpdated(dateTime);
        return mockedDTO;
    }

    public ClientBasicDTO newClientDTO(Long id, String fullName, String linkedInUrl, String companyName, String companyUrl) {
        ClientBasicDTO mockedDTO = new ClientBasicDTO();
        LocalDateTime dateTime = LocalDateTime.now(ZoneOffset.UTC);
        mockedDTO.setId(id);
        mockedDTO.setFullName(fullName);
        mockedDTO.setLinkedInUrl(linkedInUrl);
        mockedDTO.setPhoneNumber(FAKER.phoneNumber().phoneNumber());
        mockedDTO.setEmail(FAKER.internet().emailAddress());
        mockedDTO.setPosition(FAKER.company().profession());
        mockedDTO.setCompany(newCompanyDTO(FAKER.internet().uuid(), companyName, companyUrl, dateTime));
        mockedDTO.setUpdated(dateTime);
        mockedDTO.setCreated(dateTime);
        return mockedDTO;
    }

    public UserOld newUserEntity(String componentId, String fullName, String[] roles) {
        UserOld userOld = new UserOld();
        userOld.setId(componentId);
        userOld.setFullName(fullName);
        userOld.setRoles(new HashSet<>(Arrays.asList(roles)));
        userOld.setActive(true);
        return userOld;
    }

    public UserDTOOld newUserDTO(String componentId, String fullName, String[] roles) {
        UserDTOOld user = new UserDTOOld();
        user.setId(componentId);
        user.setFullName(fullName);
        user.setActive(true);
        user.setRoles(new HashSet<>(Arrays.asList(roles)));
        return user;
    }
}
