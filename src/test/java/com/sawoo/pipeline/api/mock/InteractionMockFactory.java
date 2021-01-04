package com.sawoo.pipeline.api.mock;

import com.sawoo.pipeline.api.dto.lead.LeadInteractionDTO;
import com.sawoo.pipeline.api.model.common.Note;
import com.sawoo.pipeline.api.model.common.UrlTitle;
import com.sawoo.pipeline.api.model.interaction.Interaction;
import com.sawoo.pipeline.api.model.interaction.InteractionStatusList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class InteractionMockFactory extends BaseMockFactory<LeadInteractionDTO, Interaction> {

    @Getter
    private final ProspectMockFactory prospectMockFactory;

    @Override
    public String getComponentId() {
        return getFAKER().internet().uuid();
    }

    @Override
    public Interaction newEntity(String id) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        return Interaction
                .builder()
                .id(id)
                .invite(UrlTitle
                        .builder()
                        .url(getFAKER().internet().url())
                        .description(getFAKER().lebowski().quote())
                        .build())
                .status(0)
                .scheduled(now.plusDays(10).plusHours(10))
                .type(InteractionStatusList.DONE.getStatus())
                .note(Note
                        .builder()
                        .text(getFAKER().lorem().sentence(25))
                        .updated(now)
                        .build())
                .created(now)
                .updated(now)
                .build();
    }

    @Override
    public LeadInteractionDTO newDTO(String id) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        LeadInteractionDTO interaction = LeadInteractionDTO
                .builder()
                .id(id)
                .invite(UrlTitle
                        .builder()
                        .url(getFAKER().internet().url())
                        .description(getFAKER().lebowski().quote())
                        .build())
                .status(0)
                .scheduled(now.plusDays(10).plusHours(10))
                .type(InteractionStatusList.DONE.getStatus())
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
    public LeadInteractionDTO newDTO(String id, LeadInteractionDTO dto) {
        return dto.toBuilder().id(id).build();
    }
}
