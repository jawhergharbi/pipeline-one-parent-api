package com.sawoo.pipeline.api.mock;

import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.model.common.Note;
import com.sawoo.pipeline.api.model.common.UrlTitle;
import com.sawoo.pipeline.api.model.interaction.Interaction;
import com.sawoo.pipeline.api.model.interaction.InteractionStatusList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RequiredArgsConstructor
public abstract class InteractionMockBaseFactory<D extends InteractionDTO> extends BaseMockFactory<D , Interaction> {

    @Getter
    private final PersonMockFactory personMockFactory;

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
                .link(UrlTitle
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
    public abstract D newDTO(String id);

    @Override
    public abstract D newDTO(String id, D dto);
}
