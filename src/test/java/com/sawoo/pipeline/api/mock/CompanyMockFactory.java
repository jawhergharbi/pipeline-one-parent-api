package com.sawoo.pipeline.api.mock;

import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.model.CompanyMongoDB;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CompanyMockFactory extends BaseMockFactory<CompanyDTO, CompanyMongoDB> {

    @Override
    public String getComponentId() {
        return getFAKER().internet().uuid();
    }

    @Override
    public CompanyMongoDB newEntity(String id) {
        Faker FAKER = getFAKER();
        return CompanyMongoDB.builder()
                .id(id)
                .name(FAKER.company().name())
                .url(FAKER.company().url())
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
