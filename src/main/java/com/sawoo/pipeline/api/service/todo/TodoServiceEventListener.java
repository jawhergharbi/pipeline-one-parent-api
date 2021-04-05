package com.sawoo.pipeline.api.service.todo;

import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthDetails;
import com.sawoo.pipeline.api.model.common.Link;
import com.sawoo.pipeline.api.model.common.LinkType;
import com.sawoo.pipeline.api.model.todo.Todo;
import com.sawoo.pipeline.api.model.todo.TodoSource;
import com.sawoo.pipeline.api.model.todo.TodoSourceType;
import com.sawoo.pipeline.api.service.base.event.BaseServiceBeforeInsertEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class TodoServiceEventListener {

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
    }

    private UserAuthDetails getUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? (UserAuthDetails) auth.getPrincipal() : null;
    }
}
