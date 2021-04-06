package com.sawoo.pipeline.api.mock;

import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.dto.PersonalityDTO;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.dto.person.PersonDTO;
import com.sawoo.pipeline.api.model.common.Personality;
import com.sawoo.pipeline.api.model.company.Company;
import com.sawoo.pipeline.api.model.person.Person;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class PersonMockFactory extends BaseMockFactory<PersonDTO, Person> {

    @Getter
    private final CompanyMockFactory companyMockFactory;

    @Override
    public String getComponentId() {
        return getFAKER().internet().uuid();
    }

    @Override
    public Person newEntity(String id) {
        return newEntity(id, true);
    }

    public Person newEntity(String id, boolean addCompany) {
        Faker FAKER = getFAKER();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        Person entity = new Person();
        entity.setId(id);
        entity.setFirstName(FAKER.name().firstName());
        entity.setLastName(FAKER.name().lastName());
        entity.setLinkedInUrl(FAKER.internet().url());
        entity.setEmail(FAKER.internet().emailAddress());
        entity.setPhoneNumber(FAKER.phoneNumber().phoneNumber());
        entity.setPosition(FAKER.company().profession());
        entity.setPersonality(Personality
                .builder()
                .type(1)
                .build());
        if (addCompany) {
            entity.setCompany(Company.builder()
                    .name(FAKER.company().name())
                    .url(FAKER.company().url())
                    .build());
        }
        entity.setCreated(now);
        entity.setUpdated(now);
        return entity;
    }

    @Override
    public PersonDTO newDTO(String id) {
        return newDTO(id, getFAKER().name().firstName(), getFAKER().name().lastName());
    }

    public PersonDTO newDTO(String id, String firstName, String lastName) {
        PersonDTO dto = new PersonDTO();
        Faker FAKER = getFAKER();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        dto.setId(id);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setPhoneNumber(FAKER.phoneNumber().phoneNumber());
        dto.setEmail(FAKER.internet().emailAddress());
        dto.setCompany(CompanyDTO.builder()
                .name(FAKER.company().name())
                .url(FAKER.company().url())
                .build());
        dto.setLinkedInUrl(FAKER.internet().url());
        dto.setPersonality(PersonalityDTO
                .builder()
                .type(1)
                .build());
        dto.setPosition(FAKER.company().profession());
        dto.setProfilePicture(FAKER.internet().url());
        dto.setSalutation(0);
        dto.setCreated(now);
        dto.setUpdated(now);
        return dto;
    }

    @Override
    public PersonDTO newDTO(String id, PersonDTO dto) {
        PersonDTO newDTO = dto.toBuilder().build();
        newDTO.setId(id);
        newDTO.setFirstName(dto.getFirstName());
        newDTO.setLastName(dto.getLastName());
        newDTO.setLinkedInUrl(dto.getLinkedInUrl());
        newDTO.setProfilePicture(dto.getProfilePicture());
        newDTO.setPosition(dto.getPosition());
        newDTO.setCompany(dto.getCompany());
        newDTO.setPersonality(dto.getPersonality());
        return newDTO;
    }
}
