package com.sawoo.pipeline.api.mock;

import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.model.Company;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class ProspectMockFactory extends BaseMockFactory<ProspectDTO, Prospect> {

    @Override
    public String getComponentId() {
        return getFAKER().internet().uuid();
    }

    @Override
    public Prospect newEntity(String id) {
        return newEntity(id, true);
    }

    public Prospect newEntity(String id, boolean addCompany) {
        Faker FAKER = getFAKER();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        Prospect entity = new Prospect();
        entity.setId(id);
        entity.setFirstName(FAKER.name().firstName());
        entity.setLastName(FAKER.name().lastName());
        entity.setLinkedInUrl(FAKER.internet().url());
        entity.setLinkedInThread(FAKER.internet().url());
        entity.setEmail(FAKER.internet().emailAddress());
        entity.setPhoneNumber(FAKER.phoneNumber().phoneNumber());
        entity.setPosition(FAKER.company().profession());
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
    public ProspectDTO newDTO(String id) {
        ProspectDTO dto = new ProspectDTO();
        Faker FAKER = getFAKER();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        dto.setId(id);
        dto.setFirstName(FAKER.name().firstName());
        dto.setLastName(FAKER.name().lastName());
        dto.setCompany(CompanyDTO.builder()
                .name(FAKER.company().name())
                .url(FAKER.company().url())
                .build());
        dto.setLinkedInThread(FAKER.internet().url());
        dto.setLinkedInUrl(FAKER.internet().url());
        dto.setPosition(FAKER.company().profession());
        dto.setProfilePicture(FAKER.internet().url());
        dto.setSalutation(0);
        dto.setCreated(now);
        dto.setUpdated(now);
        return dto;
    }
}
