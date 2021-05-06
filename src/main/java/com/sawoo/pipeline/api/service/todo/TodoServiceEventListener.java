package com.sawoo.pipeline.api.service.todo;

import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthDetails;
import com.sawoo.pipeline.api.model.common.Link;
import com.sawoo.pipeline.api.model.common.LinkType;
import com.sawoo.pipeline.api.model.common.MessageTemplate;
import com.sawoo.pipeline.api.model.common.TodoType;
import com.sawoo.pipeline.api.model.todo.Todo;
import com.sawoo.pipeline.api.model.todo.TodoMessage;
import com.sawoo.pipeline.api.model.todo.TodoSource;
import com.sawoo.pipeline.api.model.todo.TodoSourceType;
import com.sawoo.pipeline.api.model.todo.TodoStatus;
import com.sawoo.pipeline.api.service.base.event.BaseServiceBeforeInsertEvent;
import com.sawoo.pipeline.api.service.base.event.BaseServiceBeforeSaveEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class TodoServiceEventListener {

    private static final Pattern MESSAGE_PATTERN = Pattern.compile(".*?\\{\\{(.*?)\\}\\}.*?", Pattern.DOTALL);

    @EventListener
    public void handleBeforeInsertEvent(BaseServiceBeforeInsertEvent<TodoDTO, Todo> event) {
        log.debug("Todo before insert listener");
        Todo entity = event.getModel();

        // Link init
        Link link = entity.getLink();
        if (link != null && link.getType() == null) {
            if (!StringUtils.isEmpty(link.getDescription())) {
                entity.getLink().setType(LinkType.EMBEDDED_LINK);
            } else {
                entity.getLink().setType(LinkType.PLAIN_LINK);
            }
        }

        // Status. Not informed. It would be PENDING
        if (entity.getStatus() == null) {
            entity.setStatus(TodoStatus.PENDING.getValue());
        }

        // Source. Not informed. It would be a manual task
        if (entity.getSource() == null) {

            entity.setSource(TodoSource.builder()
                    .type(TodoSourceType.MANUAL)
                    .build());
            UserAuthDetails user = getUserDetails();
            if (user != null) {
                entity.getSource().setSourceId(user.getId());
                entity.getSource().setSourceDescription(user.getFullName());
            }
        }

        // Valid message?
        if (entity.getMessage() != null) {
            updateTodoMessage(entity);
        }
    }

    @EventListener
    public void handleBeforeSaveEvent(BaseServiceBeforeSaveEvent<TodoDTO, Todo> event) {
        log.debug("Todo before save listener");
        Todo entity = event.getModel();

        // Status: DONE. Completion Date must be informed
        if (entity.getStatus().equals(TodoStatus.COMPLETED.getValue())) {
            if (entity.getCompletionDate() == null) {
                entity.setCompletionDate(LocalDateTime.now(ZoneOffset.UTC));
            }
        } else {
            entity.setCompletionDate(null);
        }

        // Valid message?
        if (entity.getMessage() != null) {
            updateTodoMessage(entity);
        }
    }

    private UserAuthDetails getUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? (UserAuthDetails) auth.getPrincipal() : null;
    }

    private void updateTodoMessage(Todo todo) {
        TodoMessage todoMessage = todo.getMessage();
        String message = todoMessage.getText();
        if (Strings.isNotBlank(message)) {
            Matcher matcher = MESSAGE_PATTERN.matcher(message);
            boolean template = matcher.matches() && isMessageType(todo);
            todoMessage.setValid(!template);
            if (template && todo.getSource().getType().equals(TodoSourceType.MANUAL)) {
                updateTemplate(todoMessage);
            }
        } else {
            todoMessage.setValid(false);
        }
    }

    private boolean isMessageType(Todo todo) {
        int type = todo.getType();
        return type == TodoType.LINKED_IN.getValue() ||
                type == TodoType.SMS.getValue() ||
                type == TodoType.WHATS_APP.getValue() ||
                type == TodoType.EMAIL.getValue();
    }

    private void updateTemplate(TodoMessage todoMessage) {
        MessageTemplate template = todoMessage.getTemplate() != null ?
                todoMessage.getTemplate() :
                MessageTemplate.builder().build();
        template.setText(todoMessage.getText());
        Matcher matcher = MESSAGE_PATTERN.matcher(todoMessage.getText());
        todoMessage.setTemplate(template);

        // Variables
        List<String> matches = new ArrayList<>();
        while(matcher.find()) {
            matches.add(matcher.group(1));
        }
        template.getVariables().clear();
        matches.forEach(s -> template.addVariable(s, null));
    }
}
