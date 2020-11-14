package com.sawoo.pipeline.api.common;


import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.common.contants.DomainConstants;
import com.sawoo.pipeline.api.common.contants.Role;
import com.sawoo.pipeline.api.dto.client.ClientBaseDTO;
import com.sawoo.pipeline.api.dto.client.ClientBasicDTO;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.dto.prospect.LeadDTO;
import com.sawoo.pipeline.api.dto.prospect.LeadMainDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthDetails;
import com.sawoo.pipeline.api.dto.user.UserDTOOld;
import com.sawoo.pipeline.api.model.*;
import com.sawoo.pipeline.api.model.client.Client;
import com.sawoo.pipeline.api.model.prospect.Lead;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

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

    public ProspectDTO newProspectBaseDTO(String id, String firstName, String lastName) {
        ProspectDTO prospectBaseDTO = new ProspectDTO();
        prospectBaseDTO.setId(id);
        prospectBaseDTO.setFirstName(firstName);
        prospectBaseDTO.setLastName(lastName);
        prospectBaseDTO.setCompany(newCompanyDTO(LocalDateTime.now()));
        prospectBaseDTO.setLinkedInThread(FAKER.internet().url());
        prospectBaseDTO.setLinkedInUrl(FAKER.internet().url());
        prospectBaseDTO.setPosition(FAKER.company().profession());
        prospectBaseDTO.setProfilePicture(FAKER.internet().url());
        return prospectBaseDTO;
    }

    public ProspectDTO newProspectDTO(String id, String firstName, String lastName) {
        ProspectDTO prospectDTO = new ProspectDTO();
        prospectDTO.setId(id);
        prospectDTO.setFirstName(firstName);
        prospectDTO.setLastName(lastName);
        prospectDTO.setCompany(newCompanyDTO(LocalDateTime.now()));
        prospectDTO.setLinkedInThread(FAKER.internet().url());
        prospectDTO.setLinkedInUrl(FAKER.internet().url());
        prospectDTO.setPosition(FAKER.company().profession());
        prospectDTO.setProfilePicture(FAKER.internet().url());
        prospectDTO.setSalutation(0);
        prospectDTO.setCreated(LocalDateTime.now());
        prospectDTO.setUpdated(LocalDateTime.now());
        return prospectDTO;
    }

    public CompanyDTO newCompanyDTO(String id, String name, String url) {
        LocalDateTime created = LocalDateTime.of(2020, 1, 1, 1, 30);
        LocalDateTime updated = LocalDateTime.of(2020, 12, 1, 1, 30);
        CompanyDTO company = newCompanyDTO(id, name, url, created);
        company.setUpdated(updated);
        return company;
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

    public CompanyOld newCompanyEntity(Long id, String name, String url) {
        return CompanyOld.builder()
                .id(id)
                .name(name)
                .url(url)
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

    public ClientBasicDTO newClientDTO(String fullName, String linkedInUrl, String companyName, String companyUrl) {
        ClientBasicDTO mockedDTO = new ClientBasicDTO();
        mockedDTO.setFullName(fullName);
        mockedDTO.setLinkedInUrl(linkedInUrl);
        mockedDTO.setCompany(newCompanyDTO(null, companyName, companyUrl));
        mockedDTO.setPhoneNumber(FAKER.phoneNumber().phoneNumber());
        mockedDTO.setEmail(FAKER.internet().emailAddress());
        mockedDTO.setPosition(FAKER.company().profession());
        return mockedDTO;
    }

    public ClientBasicDTO newClientDTO(Long id) {
        ClientBasicDTO mockedDTO = new ClientBasicDTO();
        LocalDateTime dateTime = LocalDateTime.now(ZoneOffset.UTC);
        mockedDTO.setId(id);
        mockedDTO.setFullName(FAKER.name().fullName());
        mockedDTO.setLinkedInUrl(FAKER.internet().url());
        mockedDTO.setPhoneNumber(FAKER.phoneNumber().phoneNumber());
        mockedDTO.setEmail(FAKER.internet().emailAddress());
        mockedDTO.setPosition(FAKER.company().profession());
        mockedDTO.setCompany(newCompanyDTO(FAKER.internet().uuid(), FAKER.company().name(), FAKER.company().url(), dateTime));
        mockedDTO.setUpdated(dateTime);
        mockedDTO.setCreated(dateTime);
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

    public ClientBasicDTO newClientMainDTO(Long id, String fullName, String linkedInUrl, boolean addCompany) {
        ClientBasicDTO mockedDTO = new ClientBasicDTO();
        LocalDateTime dateTime = LocalDateTime.now(ZoneOffset.UTC);
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

    public UserAuthDTO newUserAuthRegister(String email, String password) {
        UserAuthDTO mockUserAuth = new UserAuthDTO();
        mockUserAuth.setEmail(email);
        mockUserAuth.setPassword(password);
        mockUserAuth.setConfirmPassword(password);
        mockUserAuth.setFullName(FAKER.name().fullName());
        return mockUserAuth;
    }

    public UserAuthDTO newUserAuthRegister(String email, String password, String confirmPassword, String fullName) {
        UserAuthDTO mockUserAuth = new UserAuthDTO();
        mockUserAuth.setEmail(email);
        mockUserAuth.setPassword(password);
        mockUserAuth.setConfirmPassword(confirmPassword);
        mockUserAuth.setFullName(fullName);
        return mockUserAuth;
    }

    public User newUserAuthEntity(String id, String email, String[] roles) {
        User mockUserAuth = new User();

        LocalDateTime SIGNED_UP_DATE_TIME = LocalDateTime.of(2020, Month.DECEMBER, 12, 12, 0);
        mockUserAuth.setId(id);
        mockUserAuth.setEmail(email);
        mockUserAuth.setCreated(SIGNED_UP_DATE_TIME);
        mockUserAuth.setPassword(FAKER.internet().password());
        mockUserAuth.setActive(true);
        if (roles != null) {
            mockUserAuth.setRoles(new HashSet<>(Arrays.asList(roles)));
        } else {
            mockUserAuth.setRoles(new HashSet<>(Collections.singletonList(Role.USER.name())));
        }
        mockUserAuth.setUpdated(LocalDateTime.now(ZoneOffset.UTC));

        return mockUserAuth;
    }

    public User newUserAuthEntity(String id, String email) {
        return newUserAuthEntity(id, email, null);
    }

    public UserAuthDTO newUserAuthDTO(String email, String role) {
        UserAuthDTO mockUserAuth = new UserAuthDTO();
        LocalDateTime now = LocalDateTime.now();
        mockUserAuth.setId(UUID.randomUUID().toString());
        mockUserAuth.setEmail(email);
        mockUserAuth.setActive(true);
        mockUserAuth.setRoles(new HashSet<>(Collections.singletonList(role)));
        mockUserAuth.setCreated(now);
        mockUserAuth.setUpdated(now);
        return mockUserAuth;
    }

    public UserAuthDTO newUserAuthDTO(String id, String email, String role) {
        UserAuthDTO mockUserAuth = new UserAuthDTO();
        LocalDateTime now = LocalDateTime.now();
        mockUserAuth.setId(id);
        mockUserAuth.setEmail(email);
        mockUserAuth.setActive(true);
        mockUserAuth.setRoles(new HashSet<>(Collections.singletonList(role)));
        mockUserAuth.setCreated(now);
        mockUserAuth.setUpdated(now);
        return mockUserAuth;
    }

    public UserAuthDetails newUserAuthDetails(String email, String password, String id, String role) {
        UserAuthDetails mockUserAuth = new UserAuthDetails();
        LocalDateTime now = LocalDateTime.now();
        mockUserAuth.setId(id);
        mockUserAuth.setEmail(email);
        mockUserAuth.setPassword(password);
        mockUserAuth.setActive(true);
        mockUserAuth.setRoles(new HashSet<>(Collections.singletonList(role)));
        mockUserAuth.setCreated(now);
        mockUserAuth.setUpdated(now);
        return mockUserAuth;
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
