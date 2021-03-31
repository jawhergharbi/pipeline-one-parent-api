package com.sawoo.pipeline.api.service.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.UserCommon;
import com.sawoo.pipeline.api.dto.interaction.InteractionAssigneeDTO;
import com.sawoo.pipeline.api.dto.sequence.SequenceStepDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.common.Note;
import com.sawoo.pipeline.api.model.common.Personality;
import com.sawoo.pipeline.api.model.interaction.InteractionStatusList;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.repository.lead.LeadRepository;
import com.sawoo.pipeline.api.service.sequence.SequenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeadSequenceInteractionServiceDecorator implements LeadSequenceInteractionService {

    private final LeadRepository leadRepository;
    private final SequenceService sequenceService;
    private final LeadServiceDecoratorHelper helper;

    @Override
    public List<InteractionAssigneeDTO> evalInteractions(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String leadId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String sequenceId,
            String assigneeId)
            throws ResourceNotFoundException, CommonServiceException {
        log.debug("Evaluating interactions to be created based on sequence id: [{}] and lead id [{}]", leadId, sequenceId);
        Lead lead = findLeadById(leadId);

        // Check prospect personality
        Personality personality = lead.getPerson().getPersonality();
        if (personality == null || personality.getType() == null) {
                throw new CommonServiceException(
                        ExceptionMessageConstants.LEAD_SEQUENCE_INTERACTION_PERSONALITY_NOT_ASSIGNED_EXCEPTION,
                        new String[]{sequenceId, leadId});
        }

        // Add up timespan for each of the steps
        List<SequenceStepDTO> steps = sequenceService.getStepsByPersonality(sequenceId, personality.getType());
        AtomicInteger lastStepTimespan = new AtomicInteger();
        steps.forEach(s -> {
            lastStepTimespan.addAndGet(s.getTimespan());
            s.setTimespan(lastStepTimespan.get());
        });

        // Map steps into interactions
        LocalDateTime startDate = LocalDateTime.now(ZoneOffset.UTC).plusDays(1);
        UserCommon assignee = helper.getAssignee(leadId, assigneeId);
        return steps
                .stream()
                .map(s -> mapSequenceStepToInteraction(s, assignee, leadId, startDate))
                .collect(Collectors.toList());
    }

    private Lead findLeadById(String leadId) throws ResourceNotFoundException {
        return leadRepository
                .findById(leadId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ DBConstants.LEAD_DOCUMENT, leadId }));
    }

    private InteractionAssigneeDTO mapSequenceStepToInteraction(SequenceStepDTO step, UserCommon assignee, String leadId, LocalDateTime startDate) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        return InteractionAssigneeDTO.builder()
                .scheduled(startDate.plusDays(step.getTimespan()))
                .type(step.getChannel())
                .status(InteractionStatusList.SCHEDULED.getStatus())
                .link(step.getAttachment())
                .note(Note.builder()
                        .text(step.getMessage())
                        .updated(now)
                        .build())
                .componentId(leadId)
                .assignee(assignee)
                .assigneeId(assignee.getId())
                .updated(now)
                .created(now)
                .build();
    }
}
