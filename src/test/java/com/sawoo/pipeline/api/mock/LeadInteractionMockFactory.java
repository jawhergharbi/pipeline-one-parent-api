package com.sawoo.pipeline.api.mock;

import com.sawoo.pipeline.api.dto.lead.LeadInteractionDTO;
import com.sawoo.pipeline.api.model.lead.LeadInteraction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LeadInteractionMockFactory extends BaseMockFactory<LeadInteractionDTO, LeadInteraction> {

    @Getter
    private final ProspectMockFactory prospectMockFactory;

    @Override
    public String getComponentId() {
        return getFAKER().internet().uuid();
    }

    @Override
    public LeadInteraction newEntity(String id) {
        return null;
    }

    @Override
    public LeadInteractionDTO newDTO(String id) {
        return null;
    }

    @Override
    public LeadInteractionDTO newDTO(String id, LeadInteractionDTO dto) {
        return null;
    }
}
