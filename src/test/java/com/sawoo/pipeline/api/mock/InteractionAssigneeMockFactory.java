package com.sawoo.pipeline.api.mock;

import com.sawoo.pipeline.api.dto.interaction.InteractionAssigneeDTO;
import com.sawoo.pipeline.api.model.common.Note;
import com.sawoo.pipeline.api.model.common.UrlTitle;
import com.sawoo.pipeline.api.model.interaction.InteractionStatusList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class InteractionAssigneeMockFactory extends InteractionMockBaseFactory<InteractionAssigneeDTO> {

    @Autowired
    public InteractionAssigneeMockFactory(PersonMockFactory personMockFactory) {
        super(personMockFactory);
    }

    @Override
    public InteractionAssigneeDTO newDTO(String id) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        InteractionAssigneeDTO interaction = InteractionAssigneeDTO
                .builder()
                .id(id)
                .link(UrlTitle
                        .builder()
                        .url(getFAKER().internet().url())
                        .description(getFAKER().lebowski().quote())
                        .build())
                .status(InteractionStatusList.RESCHEDULED.getStatus())
                .scheduled(now.plusDays(10).plusHours(10))
                .type(0)
                .note(Note
                        .builder()
                        .text(getFAKER().lorem().sentence(25))
                        .updated(now)
                        .build())
                .build();
        interaction.setCreated(now);
        interaction.setUpdated(now);
        return interaction;
    }

    @Override
    public InteractionAssigneeDTO newDTO(String id, InteractionAssigneeDTO dto) {
        InteractionAssigneeDTO interaction = dto.withAssignee(dto.getAssignee());
        interaction.setId(id);
        return interaction;
    }
}
