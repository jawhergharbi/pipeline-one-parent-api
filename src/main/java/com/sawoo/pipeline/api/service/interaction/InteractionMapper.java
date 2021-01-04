package com.sawoo.pipeline.api.service.interaction;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.dto.lead.LeadInteractionDTO;
import com.sawoo.pipeline.api.model.interaction.Interaction;
import com.sawoo.pipeline.api.service.base.BaseMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@Component
public class InteractionMapper implements BaseMapper<LeadInteractionDTO, Interaction> {

    private final JMapper<LeadInteractionDTO, Interaction> mapperOut = new JMapper<>(LeadInteractionDTO.class, Interaction.class);
    private final JMapper<Interaction, LeadInteractionDTO> mapperIn = new JMapper<>(Interaction.class, LeadInteractionDTO.class);
}
