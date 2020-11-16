package com.sawoo.pipeline.api.mock;

import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.model.lead.Lead;
import org.springframework.stereotype.Component;

@Component
public class LeadMockFactory extends BaseMockFactory<LeadDTO, Lead> {

    @Override
    public String getComponentId() {
        return getFAKER().internet().uuid();
    }

    @Override
    public Lead newEntity(String id) {
        return null;
    }

    @Override
    public LeadDTO newDTO(String id) {
        return null;
    }

    @Override
    public LeadDTO newDTO(String id, LeadDTO dto) {
        LeadDTO newDTO = dto.toBuilder().build();
        return newDTO;
    }
}
