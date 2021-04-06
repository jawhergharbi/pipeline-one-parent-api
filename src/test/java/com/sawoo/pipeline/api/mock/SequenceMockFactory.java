package com.sawoo.pipeline.api.mock;

import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
import com.sawoo.pipeline.api.dto.sequence.SequenceUserDTO;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.model.sequence.SequenceStatus;
import com.sawoo.pipeline.api.model.sequence.SequenceUserType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class SequenceMockFactory extends BaseMockFactory<SequenceDTO, Sequence> {

    @Getter
    private final SequenceStepMockFactory sequenceStepMockFactory;

    @Getter
    private final AccountMockFactory accountMockFactory;

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
                .status(SequenceStatus.IN_PROGRESS)
                .componentId(FAKER.internet().uuid())
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
                .status(SequenceStatus.IN_PROGRESS.getValue())
                .componentId(FAKER.internet().uuid())
                .description(FAKER.lebowski().quote())
                .users(new HashSet<>(Collections.singleton(
                        SequenceUserDTO.builder()
                                .userId(FAKER.internet().uuid())
                                .type(SequenceUserType.OWNER)
                                .build())))
                .build();
        dto.setCreated(LocalDateTime.now(ZoneOffset.UTC));
        dto.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
        return dto;
    }

    @Override
    public SequenceDTO newDTO(String id, SequenceDTO dto) {
        return SequenceDTO.builder()
                .id(id)
                .componentId(dto.getComponentId())
                .name(dto.getName())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .users(dto.getUsers())
                .account(dto.getAccount())
                .created(dto.getCreated())
                .updated(dto.getUpdated())
                .build();
    }
}
