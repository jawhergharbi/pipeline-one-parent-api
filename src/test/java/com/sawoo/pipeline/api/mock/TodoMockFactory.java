package com.sawoo.pipeline.api.mock;

import com.sawoo.pipeline.api.dto.common.LinkDTO;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.dto.todo.TodoMessageDTO;
import com.sawoo.pipeline.api.model.todo.TodoSource;
import com.sawoo.pipeline.api.model.todo.TodoSourceType;
import com.sawoo.pipeline.api.model.todo.TodoStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class TodoMockFactory extends TodoMockBaseFactory<TodoDTO> {

    @Autowired
    public TodoMockFactory(PersonMockFactory personMockFactory) {
        super(personMockFactory);
    }

    @Override
    public TodoDTO newDTO(String id) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        TodoDTO todo = TodoDTO
                .builder()
                .id(id)
                .link(LinkDTO
                        .builder()
                        .url(getFAKER().internet().url())
                        .description(getFAKER().lebowski().quote())
                        .build())
                .status(TodoStatus.PENDING.getValue())
                .scheduled(now.plusDays(10).plusHours(10))
                .channel(0)
                .assigneeId(getFAKER().internet().uuid())
                .message(TodoMessageDTO
                        .builder()
                        .text(getFAKER().lorem().sentence(25))
                        .build())
                .source(TodoSource.builder()
                        .type(TodoSourceType.MANUAL)
                        .sourceId(getFAKER().internet().uuid())
                        .build())
                .build();
        todo.setCreated(now);
        todo.setUpdated(now);
        return todo;
    }

    @Override
    public TodoDTO newDTO(String id, TodoDTO dto) {
        return dto.withId(id);
    }
}
