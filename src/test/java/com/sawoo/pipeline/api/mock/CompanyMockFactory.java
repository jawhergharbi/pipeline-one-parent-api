package com.sawoo.pipeline.api.mock;

import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.model.Company;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CompanyMockFactory extends BaseMockFactory<CompanyDTO, Company> {

    @Override
    public String getComponentId() {
        return getFAKER().internet().uuid();
    }

    @Override
    public Company newEntity(String id) {
        Faker FAKER = getFAKER();
        return Company.builder()
                .id(id)
                .name(FAKER.company().name())
                .url(FAKER.company().url())
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .build();
    }

    public Company newEntity(String name, String url) {
        Faker FAKER = getFAKER();
        return Company.builder()
                .name(name)
                .url(url)
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .build();
    }

    @Override
    public CompanyDTO newDTO(String id) {
        Faker FAKER = getFAKER();
        return CompanyDTO.builder()
                .id(id)
                .name(FAKER.company().name())
                .url(FAKER.company().url())
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .build();
    }
}
