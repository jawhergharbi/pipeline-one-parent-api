package com.sawoo.pipeline.api.service.prospect;


import com.sawoo.pipeline.api.common.contants.MessageConstants;
import com.sawoo.pipeline.api.dto.UserCommon;
import com.sawoo.pipeline.api.dto.common.LinkDTO;
import com.sawoo.pipeline.api.dto.sequence.SequenceStepDTO;
import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
import com.sawoo.pipeline.api.dto.todo.TodoMessageDTO;
import com.sawoo.pipeline.api.model.common.LinkType;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.model.sequence.SequenceStepChannel;
import com.sawoo.pipeline.api.model.todo.TodoSource;
import com.sawoo.pipeline.api.model.todo.TodoSourceType;
import com.sawoo.pipeline.api.model.todo.TodoStatus;
import com.sawoo.pipeline.api.model.todo.TodoType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProspectSequenceTodoHelper {

    private final MessageSource messageSource;

    public TodoAssigneeDTO mapSequenceStepToTODO(SequenceStepDTO step, UserCommon assignee, Prospect prospect, String sequenceId, LocalDateTime startDate) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        return TodoAssigneeDTO.builder()
                .scheduled(startDate.plusDays(step.getTimespan()))
                .channel(step.getChannel())
                .type(TodoType.OUT_GOING_INTERACTION)
                .status(TodoStatus.PENDING.getValue())
                .link(createLink(step, prospect))
                .message(TodoMessageDTO.builder()
                        .text(step.getMessageTemplate().getText())
                        .build())
                .componentId(prospect.getId())
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

    private LinkDTO createLink(SequenceStepDTO step, Prospect prospect) {
        if (step.getAttachment() != null) {
            return LinkDTO.builder()
                    .description(step.getAttachment().getDescription())
                    .type(step.getAttachment().getType())
                    .url(step.getAttachment().getUrl())
                    .build();
        } else {
            Locale locale = LocaleContextHolder.getLocale();
            if (step.getChannel().equals(SequenceStepChannel.LINKED_IN.getValue())) {
                return LinkDTO.builder()
                        .description(messageSource.getMessage(MessageConstants.PROSPECT_SEQUENCE_STEP_LINKED_IN_CHAT_DEFAULT_LABEL, null, locale))
                        .type(LinkType.PLAIN_LINK)
                        .url(prospect.getLinkedInThread())
                        .build();
            }
            return null;
        }
    }
}
