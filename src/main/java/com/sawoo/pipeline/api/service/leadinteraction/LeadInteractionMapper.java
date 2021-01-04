package com.sawoo.pipeline.api.service.leadinteraction;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.dto.lead.LeadInteractionDTO;
import com.sawoo.pipeline.api.model.lead.LeadInteraction;
import com.sawoo.pipeline.api.service.base.BaseMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@Component
public class LeadInteractionMapper implements BaseMapper<LeadInteractionDTO, LeadInteraction> {

    private final JMapper<LeadInteractionDTO, LeadInteraction> mapperOut = new JMapper<>(LeadInteractionDTO.class, LeadInteraction.class);
    private final JMapper<LeadInteraction, LeadInteractionDTO> mapperIn = new JMapper<>(LeadInteraction.class, LeadInteractionDTO.class);
}
