package com.sawoo.pipeline.api.service.todo;

import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.mock.TodoMockFactory;
import com.sawoo.pipeline.api.model.todo.Todo;
import com.sawoo.pipeline.api.model.todo.TodoStatus;
import com.sawoo.pipeline.api.service.base.event.BaseServiceBeforeInsertEvent;
import com.sawoo.pipeline.api.service.base.event.BaseServiceBeforeSaveEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
class TodoServiceEventListenerTest {

    @Autowired
    private TodoServiceEventListener listener;

    @Autowired
    private TodoMockFactory mockFactory;

    @Autowired
    private TodoMapper mapper;

    @Test
    @DisplayName("onBeforeInsert: status not informed - Success")
    void onBeforeInsertWhenStatusNotInformedReturnsSuccess() {
        // Set up mocked entities
        TodoDTO postDTO = mockFactory.newDTO(null);
        postDTO.setStatus(null);
        Todo entity = mapper.getMapperIn().getDestination(postDTO);

        // Execute the service call
        listener.handleBeforeInsertEvent(new BaseServiceBeforeInsertEvent<>(postDTO, entity));

        // Assertions
        Assertions.assertNotNull(entity.getStatus(), "Status must be informed");
        Assertions.assertEquals(
                TodoStatus.PENDING.getValue(),
                entity.getStatus(),
                String.format("Status must contain the default value: [%d]", TodoStatus.PENDING.getValue()));
    }

    @Test
    @DisplayName("onBeforeInsert: status not informed - Success")
    void onBeforeInsertWhenStatusInformedReturnsSuccess() {
        // Set up mocked entities
        TodoDTO postDTO = mockFactory.newDTO(null);
        int TODO_STATUS_ON_GOING = TodoStatus.ON_GOING.getValue();
        postDTO.setStatus(TODO_STATUS_ON_GOING);
        Todo entity = mapper.getMapperIn().getDestination(postDTO);

        // Execute the service call
        listener.handleBeforeInsertEvent(new BaseServiceBeforeInsertEvent<>(postDTO, entity));

        // Assertions
        Assertions.assertNotNull(entity.getStatus(), "Status must be informed");
        Assertions.assertEquals(
                TODO_STATUS_ON_GOING,
                entity.getStatus(),
                String.format("Status must contain the given value: [%d]", TODO_STATUS_ON_GOING));
    }

    @Test
    @DisplayName("onBeforeInsert: status set to completed - Success")
    void onBeforeSaveWhenStatusDoneReturnsSuccess() {
        // Set up mocked entities
        TodoDTO postDTO = mockFactory.newDTO(null);
        postDTO.setStatus(TodoStatus.COMPLETED.getValue());
        Todo entity = mapper.getMapperIn().getDestination(postDTO);

        // Execute the service call
        listener.handleBeforeSaveEvent(new BaseServiceBeforeSaveEvent<>(postDTO, entity));

        // Assertions
        Assertions.assertNotNull(entity.getCompletionDate(), "CompletionDate can not be null");
        Assertions.assertEquals(LocalDate.now(ZoneOffset.UTC), entity.getCompletionDate().toLocalDate(), "Completion Data must be today");
    }

    @Test
    @DisplayName("onBeforeInsert: status set to completed - Success")
    void onBeforeSaveWhenStatusIsNotDoneButItWasDoneBeforeReturnsSuccess() {
        // Set up mocked entities
        TodoDTO postDTO = mockFactory.newDTO(null);
        postDTO.setStatus(TodoStatus.ON_GOING.getValue());
        Todo entity = mapper.getMapperIn().getDestination(postDTO);
        entity.setCompletionDate(LocalDateTime.now(ZoneOffset.UTC));

        // Execute the service call
        listener.handleBeforeSaveEvent(new BaseServiceBeforeSaveEvent<>(postDTO, entity));

        // Assertions
        Assertions.assertNull(entity.getCompletionDate(), "CompletionDate must be null");
    }
}
