package com.sawoo.pipeline.api.service.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.UserCommon;
import com.sawoo.pipeline.api.dto.common.LinkDTO;
import com.sawoo.pipeline.api.dto.sequence.SequenceStepDTO;
import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
import com.sawoo.pipeline.api.dto.todo.TodoMessageDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.common.Personality;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.model.todo.TodoSource;
import com.sawoo.pipeline.api.model.todo.TodoSourceType;
import com.sawoo.pipeline.api.model.todo.TodoStatus;
import com.sawoo.pipeline.api.service.sequence.SequenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Component
@Validated
public class LeadSequenceTodoServiceDecorator implements LeadSequenceTodoService {

    private final LeadService leadService;
    private final SequenceService sequenceService;
    private final LeadServiceDecoratorHelper helper;

    @Autowired
    public LeadSequenceTodoServiceDecorator(@Lazy LeadService leadService, SequenceService sequenceService, LeadServiceDecoratorHelper helper) {
        this.leadService = leadService;
        this.sequenceService = sequenceService;
        this.helper = helper;
    }

    @Override
    public List<TodoAssigneeDTO> evalTODOs(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String sequenceId,
            String assigneeId)
            throws ResourceNotFoundException, CommonServiceException {
        log.debug("Evaluating todos to be created based on sequence id: [{}] and lead id [{}]", leadId, sequenceId);
        Lead lead = findLeadById(leadId);

        // Check prospect personality
        Personality personality = lead.getPerson().getPersonality();
        if (personality == null || personality.getType() == null) {
                throw new CommonServiceException(
                        ExceptionMessageConstants.LEAD_SEQUENCE_TODO_PERSONALITY_NOT_ASSIGNED_EXCEPTION,
                        new String[]{sequenceId, leadId});
        }

        // Add up timespan for each of the steps
        List<SequenceStepDTO> steps = sequenceService.getStepsByPersonality(sequenceId, personality.getType());
        AtomicInteger lastStepTimespan = new AtomicInteger();
        steps.forEach(s -> {
            lastStepTimespan.addAndGet(s.getTimespan());
            s.setTimespan(lastStepTimespan.get());
        });

        // Map steps into tasks
        LocalDateTime startDate = LocalDateTime.now(ZoneOffset.UTC).plusDays(1);
        UserCommon assignee = helper.getAssignee(leadId, assigneeId);
        return steps
                .stream()
                .map(s -> mapSequenceStepToTODO(s, assignee, leadId, sequenceId, startDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<TodoAssigneeDTO> createTODOs(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String sequenceId, String assigneeId)
            throws ResourceNotFoundException, CommonServiceException {
        log.debug("Create TODOs based on sequence id: [{}] and for lead id [{}]", leadId, sequenceId);
        List<TodoAssigneeDTO> todos = evalTODOs(leadId, sequenceId, assigneeId);
        leadService.addTODOList(leadId, todos);
        return todos;
    }

    private Lead findLeadById(String leadId) throws ResourceNotFoundException {
        return leadService
                .getRepository()
                .findById(leadId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ DBConstants.LEAD_DOCUMENT, leadId }));
    }

    private TodoAssigneeDTO mapSequenceStepToTODO(SequenceStepDTO step, UserCommon assignee, String leadId, String sequenceId, LocalDateTime startDate) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        return TodoAssigneeDTO.builder()
                .scheduled(startDate.plusDays(step.getTimespan()))
                .type(step.getChannel())
                .status(TodoStatus.SCHEDULED.getValue())
                .link(step.getAttachment() == null ? null : LinkDTO.builder()
                        .description(step.getAttachment().getDescription())
                        .type(step.getAttachment().getType())
                        .url(step.getAttachment().getUrl())
                        .build())
                .message(TodoMessageDTO.builder()
                        .text(step.getMessageTemplate().getText())
                        .build())
                .componentId(leadId)
                .assignee(assignee)
                .assigneeId(assignee.getId())
                .source(TodoSource.builder()
                        .type(TodoSourceType.AUTOMATIC)
                        .sourceId(sequenceId)
                        .build())
                .updated(now)
                .created(now)
                .build();
    }
}
