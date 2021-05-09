package com.sawoo.pipeline.api.mock;

import com.sawoo.pipeline.api.dto.common.LinkDTO;
import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
import com.sawoo.pipeline.api.dto.todo.TodoMessageDTO;
import com.sawoo.pipeline.api.model.todo.TodoStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class TodoAssigneeMockFactory extends TodoMockBaseFactory<TodoAssigneeDTO> {

    @Autowired
    public TodoAssigneeMockFactory(PersonMockFactory personMockFactory) {
        super(personMockFactory);
    }

    @Override
    public TodoAssigneeDTO newDTO(String id) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        TodoAssigneeDTO todo = TodoAssigneeDTO
                .builder()
                .id(id)
                .link(LinkDTO
                        .builder()
                        .url(getFAKER().internet().url())
                        .description(getFAKER().lebowski().quote())
                        .build())
                .status(TodoStatus.ON_GOING.getValue())
                .scheduled(now.plusDays(10).plusHours(10))
                .channel(0)
                .message(TodoMessageDTO
                        .builder()
                        .text(getFAKER().lorem().sentence(25))
                        .build())
                .build();
        todo.setCreated(now);
        todo.setUpdated(now);
        return todo;
    }

    @Override
    public TodoAssigneeDTO newDTO(String id, TodoAssigneeDTO dto) {
        TodoAssigneeDTO todo = dto.withAssignee(dto.getAssignee());
        todo.setId(id);
        return todo;
    }
}
