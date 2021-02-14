package com.sawoo.pipeline.api.mock;

import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class SequenceMockFactory extends BaseMockFactory<SequenceDTO, Sequence> {

    @Getter
    private final SequenceStepMockFactory sequenceStepMockFactory;

    @Override
    public String getComponentId() {
        return getFAKER().internet().uuid();
    }

    @Override
    public Sequence newEntity(String id) {
        Faker FAKER = getFAKER();
        return Sequence
                .builder()
                .id(id)
                .name(FAKER.funnyName().name())
                .description(FAKER.lebowski().quote())
                .created(LocalDateTime.now(ZoneOffset.UTC))
                .updated(LocalDateTime.now(ZoneOffset.UTC))
                .build();
    }

    @Override
    public SequenceDTO newDTO(String id) {
        Faker FAKER = getFAKER();
        SequenceDTO dto = SequenceDTO
                .builder()
                .id(id)
                .name(FAKER.funnyName().name())
                .description(FAKER.lebowski().quote())
                .build();
        dto.setCreated(LocalDateTime.now(ZoneOffset.UTC));
        dto.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
        return dto;
    }

    @Override
    public SequenceDTO newDTO(String id, SequenceDTO dto) {
        return dto.toBuilder().id(id).build();
    }
}
