package com.sawoo.pipeline.api.service.prospect;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.UserCommon;
import com.sawoo.pipeline.api.dto.sequence.SequenceStepDTO;
import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.common.Personality;
import com.sawoo.pipeline.api.model.prospect.Prospect;
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
public class ProspectSequenceTodoServiceDecorator implements ProspectSequenceTodoService {

    private final ProspectService prospectService;
    private final SequenceService sequenceService;
    private final ProspectServiceDecoratorHelper helper;
    private final ProspectSequenceTodoHelper sequenceTodoHelper;

    @Autowired
    public ProspectSequenceTodoServiceDecorator(@Lazy ProspectService prospectService,
                                                SequenceService sequenceService,
                                                ProspectServiceDecoratorHelper helper,
                                                ProspectSequenceTodoHelper sequenceTodoHelper) {
        this.prospectService = prospectService;
        this.sequenceService = sequenceService;
        this.helper = helper;
        this.sequenceTodoHelper = sequenceTodoHelper;
    }

    @Override
    public List<TodoAssigneeDTO> evalTODOs(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String prospectId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String sequenceId,
            String assigneeId)
            throws ResourceNotFoundException, CommonServiceException {
        log.debug("Evaluating TODOs to be created based on sequence id: [{}] and prospect id [{}]", prospectId, sequenceId);

        return createTODOsFromSequence(prospectId, sequenceId, assigneeId);
    }

    @Override
    public List<TodoAssigneeDTO> createTODOs(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String prospectId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String campaignId,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String sequenceId,
            String assigneeId)
            throws ResourceNotFoundException, CommonServiceException {
        log.debug("Create TODOs based on sequence id: [{}] and for prospect id [{}]", prospectId, sequenceId);
        List<TodoAssigneeDTO> todos = createTODOsFromSequence(prospectId, sequenceId, assigneeId);

        if (todos != null && !todos.isEmpty()) {
            JMapper<TodoDTO, TodoAssigneeDTO> mapper = prospectService.getMapper().getTodoAssigneeMapper();
            List<TodoDTO> todoDTOs = todos
                    .stream()
                    .peek(t -> t.setCampaignId(campaignId))
                    .map(mapper::getDestination)
                    .collect(Collectors.toList());
            prospectService.addTODOList(prospectId, todoDTOs);
        } else {
            log.info("No TODOs are going to be created for [campaignId: {}, sequenceId: {}, prospectId: {}]", campaignId, sequenceId, prospectId);
        }
        return todos;
    }

    private List<TodoAssigneeDTO> createTODOsFromSequence(String prospectId, String sequenceId, String assigneeId) {
        Prospect prospect = findProspectById(prospectId);

        // Check prospect personality
        Personality personality = prospect.getPerson().getPersonality();
        if (personality == null || personality.getType() == null) {
            throw new CommonServiceException(
                    ExceptionMessageConstants.PROSPECT_SEQUENCE_TODO_PERSONALITY_NOT_ASSIGNED_EXCEPTION,
                    new String[]{sequenceId, prospectId});
        }

        // Add up timespan for each of the steps
        List<SequenceStepDTO> steps = sequenceService.getStepsByPersonality(sequenceId, personality.getType());
        AtomicInteger lastStepTimespan = new AtomicInteger();
        steps.forEach(s -> {
            lastStepTimespan.addAndGet(s.getTimespan());
            s.setTimespan(lastStepTimespan.get());
        });

        // Map steps into tasks
        LocalDateTime startDate = LocalDateTime.now(ZoneOffset.UTC);
        UserCommon assignee = helper.getAssignee(prospectId, assigneeId);
        return steps
                .stream()
                .map(s -> sequenceTodoHelper.mapSequenceStepToTODO(s, assignee, prospect, sequenceId, startDate))
                .collect(Collectors.toList());
    }

    private Prospect findProspectById(String prospectId) throws ResourceNotFoundException {
        return prospectService
                .getRepository()
                .findById(prospectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{ DBConstants.PROSPECT_DOCUMENT, prospectId }));
    }
}
