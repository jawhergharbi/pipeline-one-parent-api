package com.sawoo.pipeline.api.mock;

import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
import com.sawoo.pipeline.api.model.common.Note;
import com.sawoo.pipeline.api.model.common.UrlTitle;
import com.sawoo.pipeline.api.model.todo.TodoStatusList;
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
        TodoAssigneeDTO interaction = TodoAssigneeDTO
                .builder()
                .id(id)
                .link(UrlTitle
                        .builder()
                        .url(getFAKER().internet().url())
                        .description(getFAKER().lebowski().quote())
                        .build())
                .status(TodoStatusList.RESCHEDULED.getValue())
                .scheduled(now.plusDays(10).plusHours(10))
                .type(0)
                .note(Note
                        .builder()
                        .text(getFAKER().lorem().sentence(25))
                        .updated(now)
                        .build())
                .build();
        interaction.setCreated(now);
        interaction.setUpdated(now);
        return interaction;
    }

    @Override
    public TodoAssigneeDTO newDTO(String id, TodoAssigneeDTO dto) {
        TodoAssigneeDTO interaction = dto.withAssignee(dto.getAssignee());
        interaction.setId(id);
        return interaction;
    }
}
