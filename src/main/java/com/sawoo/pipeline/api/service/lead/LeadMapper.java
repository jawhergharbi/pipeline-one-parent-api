package com.sawoo.pipeline.api.service.lead;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.dto.lead.LeadTodoDTO;
import com.sawoo.pipeline.api.dto.lead.LeadInteractionLeadDTO;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.service.base.BaseMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@Component
public class LeadMapper implements BaseMapper<LeadDTO, Lead> {

    private final JMapper<LeadDTO, Lead> mapperOut = new JMapper<>(LeadDTO.class, Lead.class);
    private final JMapper<Lead, LeadDTO> mapperIn = new JMapper<>(Lead.class, LeadDTO.class);

    private final JMapper<LeadTodoDTO, TodoDTO> interactionMapperOut = new JMapper<>(LeadTodoDTO.class, TodoDTO.class);
    private final JMapper<LeadInteractionLeadDTO, Lead> leadInteractionMapperOut = new JMapper<>(LeadInteractionLeadDTO.class, Lead.class);
}
