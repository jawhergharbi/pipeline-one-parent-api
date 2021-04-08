package com.sawoo.pipeline.api.mock;

import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.model.common.Link;
import com.sawoo.pipeline.api.model.todo.Todo;
import com.sawoo.pipeline.api.model.todo.TodoMessage;
import com.sawoo.pipeline.api.model.todo.TodoStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RequiredArgsConstructor
public abstract class TodoMockBaseFactory<D extends TodoDTO> extends BaseMockFactory<D , Todo> {

    @Getter
    private final PersonMockFactory personMockFactory;

    @Override
    public String getComponentId() {
        return getFAKER().internet().uuid();
    }

    @Override
    public Todo newEntity(String id) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        return Todo
                .builder()
                .id(id)
                .link(Link
                        .builder()
                        .url(getFAKER().internet().url())
                        .description(getFAKER().lebowski().quote())
                        .build())
                .status(TodoStatus.ON_GOING.getValue())
                .scheduled(now.plusDays(10).plusHours(10))
                .type(2)
                .message(TodoMessage
                        .builder()
                        .text(getFAKER().lorem().sentence(25))
                        .build())
                .created(now)
                .updated(now)
                .build();
    }

    @Override
    public abstract D newDTO(String id);

    @Override
    public abstract D newDTO(String id, D dto);
}
