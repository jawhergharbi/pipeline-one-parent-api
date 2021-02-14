package com.sawoo.pipeline.api.mock;

import com.github.javafaker.Faker;
import com.sawoo.pipeline.api.dto.sequence.SequenceStepDTO;
import com.sawoo.pipeline.api.model.sequence.SequenceStep;
import com.sawoo.pipeline.api.model.sequence.SequenceStepChannel;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class SequenceStepMockFactory extends BaseMockFactory<SequenceStepDTO, SequenceStep> {

    @Override
    public String getComponentId() {
        return getFAKER().internet().uuid();
    }

    @Override
    public SequenceStep newEntity(String id) {
        return newSequenceStepEntity(id, 0, SequenceStepChannel.LINKED);
    }

    public SequenceStep newSequenceStepEntity(String id, int position) {
        return newSequenceStepEntity(id, position, SequenceStepChannel.LINKED);
    }

    public SequenceStep newSequenceStepEntity(String id, int position, SequenceStepChannel channel) {
        Faker FAKER = getFAKER();
        return SequenceStep
                .builder()
                .id(id)
                .historyId(id)
                .position(position)
                .timespan(FAKER.random().nextInt(10))
                .message(FAKER.lebowski().quote())
                .attachment(FAKER.internet().url())
                .channel(channel)
                .created(LocalDateTime.now(ZoneOffset.UTC))
                .updated(LocalDateTime.now(ZoneOffset.UTC))
                .build();
    }

    @Override
    public SequenceStepDTO newDTO(String id) {
        Faker FAKER = getFAKER();
        SequenceStepDTO dto = SequenceStepDTO
                .builder()
                .id(id)
                .historyId(id)
                .position(0)
                .timespan(FAKER.random().nextInt(10))
                .message(FAKER.lebowski().quote())
                .attachment(FAKER.internet().url())
                .channel(SequenceStepChannel.LINKED)
                .build();
        dto.setCreated(LocalDateTime.now(ZoneOffset.UTC));
        dto.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
        return dto;
    }

    @Override
    public SequenceStepDTO newDTO(String id, SequenceStepDTO dto) {
        return dto.toBuilder().id(id).build();
    }
}
