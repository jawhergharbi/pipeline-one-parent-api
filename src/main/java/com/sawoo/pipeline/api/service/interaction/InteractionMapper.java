package com.sawoo.pipeline.api.service.interaction;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.model.interaction.Interaction;
import com.sawoo.pipeline.api.service.base.BaseMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@Component
public class InteractionMapper implements BaseMapper<InteractionDTO, Interaction> {

    private final JMapper<InteractionDTO, Interaction> mapperOut = new JMapper<>(InteractionDTO.class, Interaction.class);
    private final JMapper<Interaction, InteractionDTO> mapperIn = new JMapper<>(Interaction.class, InteractionDTO.class);
}
