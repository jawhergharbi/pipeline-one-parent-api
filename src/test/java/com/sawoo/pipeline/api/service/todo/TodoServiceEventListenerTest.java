package com.sawoo.pipeline.api.service.todo;

import com.sawoo.pipeline.api.dto.common.LinkDTO;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.dto.todo.TodoMessageDTO;
import com.sawoo.pipeline.api.mock.TodoMockFactory;
import com.sawoo.pipeline.api.model.common.LinkType;
import com.sawoo.pipeline.api.model.common.TodoChannel;
import com.sawoo.pipeline.api.model.todo.Todo;
import com.sawoo.pipeline.api.model.todo.TodoMessage;
import com.sawoo.pipeline.api.model.todo.TodoStatus;
import com.sawoo.pipeline.api.model.todo.TodoType;
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
    @DisplayName("onBeforeInsert: type not informed - Success")
    void onBeforeInsertWhenTypeNotInformedReturnsSuccess() {
        // Set up mocked entities
        TodoDTO postDTO = mockFactory.newDTO(null);
        postDTO.setType(null);
        Todo entity = mapper.getMapperIn().getDestination(postDTO);

        // Execute the service call
        listener.handleBeforeInsertEvent(new BaseServiceBeforeInsertEvent<>(postDTO, entity));

        // Assertions
        Assertions.assertNotNull(entity.getType(), "Type must be informed");
        Assertions.assertEquals(
                TodoType.OUT_GOING_INTERACTION,
                entity.getType(),
                String.format("Type must contain the default value: [%s]", TodoType.OUT_GOING_INTERACTION));
    }

    @Test
    @DisplayName("onBeforeInsert: message informed and valid - Success")
    void onBeforeInsertWhenMessageInformedAndValidReturnsSuccess() {
        // Set up mocked entities
        TodoDTO postDTO = mockFactory.newDTO(null);
        postDTO.setMessage(TodoMessageDTO.builder()
                .text(mockFactory.getFAKER().chuckNorris().fact())
                .build());
        Todo entity = mapper.getMapperIn().getDestination(postDTO);

        // Execute the service call
        listener.handleBeforeInsertEvent(new BaseServiceBeforeInsertEvent<>(postDTO, entity));

        // Assertions
        Assertions.assertTrue(entity.getMessage().isValid(), "Message must be valid");
    }

    @Test
    @DisplayName("onBeforeInsert: message informed and invalid - Success")
    void onBeforeInsertWhenMessageInformedAndInvalidReturnsSuccess() {
        // Set up mocked entities
        TodoDTO postDTO = mockFactory.newDTO(null);
        postDTO.setMessage(TodoMessageDTO.builder()
                .text("This a message with variables. {{prospect_name}} So it should be an invalid message")
                .build());
        Todo entity = mapper.getMapperIn().getDestination(postDTO);

        // Execute the service call
        listener.handleBeforeInsertEvent(new BaseServiceBeforeInsertEvent<>(postDTO, entity));

        // Assertions
        Assertions.assertFalse(entity.getMessage().isValid(), "Message can not be valid");
    }

    @Test
    @DisplayName("onBeforeInsert: message informed and invalid but variables - Success")
    void onBeforeInsertWhenMessageInformedAndInvalidWithTemplateVariablesReturnsSuccess() {
        // Set up mocked entities
        TodoDTO postDTO = mockFactory.newDTO(null);
        int VARIABLE_SIZE = 1;
        postDTO.setMessage(TodoMessageDTO.builder()
                .text("This a message with variables. {{prospect_name}} So it should be an invalid message")
                .build());
        Todo entity = mapper.getMapperIn().getDestination(postDTO);

        // Execute the service call
        listener.handleBeforeInsertEvent(new BaseServiceBeforeInsertEvent<>(postDTO, entity));

        // Assertions
        Assertions.assertFalse(entity.getMessage().isValid(), "Message can not be valid");
        Assertions.assertNotNull(entity.getMessage().getTemplate().getVariables(), "Template variables can not be null");
        Assertions.assertEquals(
                VARIABLE_SIZE,
                entity.getMessage().getTemplate().getVariables().size(),
                String.format("Template variables size must be %d", VARIABLE_SIZE));
    }

    @Test
    @DisplayName("onBeforeInsert: not a message - Success")
    void onBeforeInsertWhenMessageValidWhenTypeNonMessageReturnsSuccess() {
        // Set up mocked entities
        TodoDTO postDTO = mockFactory.newDTO(null);
        postDTO.setMessage(TodoMessageDTO.builder()
                .text("This a message with variables. {{prospect_name}} So it should be an invalid message")
                .build());
        postDTO.setChannel(TodoChannel.PHONE.getValue());
        Todo entity = mapper.getMapperIn().getDestination(postDTO);

        // Execute the service call
        listener.handleBeforeInsertEvent(new BaseServiceBeforeInsertEvent<>(postDTO, entity));

        // Assertions
        Assertions.assertTrue(entity.getMessage().isValid(), "Message must be valid");
    }

    @Test
    @DisplayName("onBeforeInsert: link not informed - Success")
    void onBeforeInsertWhenLinkTypeNotInformedAndNoLinkDescriptionProvidedReturnsSuccess() {
        // Set up mocked entities
        TodoDTO postDTO = mockFactory.newDTO(null);
        LinkDTO link = postDTO.getLink();
        link.setType(null);
        link.setDescription(null);
        Todo entity = mapper.getMapperIn().getDestination(postDTO);

        // Execute the service call
        listener.handleBeforeInsertEvent(new BaseServiceBeforeInsertEvent<>(postDTO, entity));

        // Assertions
        Assertions.assertNotNull(entity.getStatus(), "Link type must be informed");
        Assertions.assertEquals(
                LinkType.PLAIN_LINK,
                entity.getLink().getType(),
                String.format("Link type must contain the default value: [%s]", LinkType.PLAIN_LINK));
    }

    @Test
    @DisplayName("onBeforeInsert: link not informed - Success")
    void onBeforeInsertWhenLinkTypeNotInformedReturnsSuccess() {
        // Set up mocked entities
        TodoDTO postDTO = mockFactory.newDTO(null);
        LinkDTO link = postDTO.getLink();
        link.setType(null);
        link.setDescription(mockFactory.getFAKER().lebowski().quote());
        Todo entity = mapper.getMapperIn().getDestination(postDTO);

        // Execute the service call
        listener.handleBeforeInsertEvent(new BaseServiceBeforeInsertEvent<>(postDTO, entity));

        // Assertions
        Assertions.assertNotNull(entity.getStatus(), "Link type must be informed");
        Assertions.assertEquals(
                LinkType.EMBEDDED_LINK,
                entity.getLink().getType(),
                String.format("Link type must contain the default value: [%s]", LinkType.EMBEDDED_LINK));
    }

    @Test
    @DisplayName("onBeforeSave: status set to completed - Success")
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
    @DisplayName("onBeforeSave: status set to completed - Success")
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

    @Test
    @DisplayName("onBeforeSave: message should be valid - Success")
    void onBeforeSaveWhenValidMessageReturnsSuccess() {
        // Set up mocked entities
        TodoDTO postDTO = mockFactory.newDTO(null);
        postDTO.setStatus(TodoStatus.ON_GOING.getValue());
        Todo entity = mapper.getMapperIn().getDestination(postDTO);
        TodoMessage message = TodoMessage.builder()
                .text("This a message without variables. So it should be a valid message")
                .build();
        entity.setMessage(message);

        // Execute the service call
        listener.handleBeforeSaveEvent(new BaseServiceBeforeSaveEvent<>(postDTO, entity));

        // Assertions
        Assertions.assertTrue(
                entity.getMessage().isValid(),
                String.format("Message [%s] must be valid", entity.getMessage().getText()));
    }

    @Test
    @DisplayName("onBeforeSave: message should be invalid - Success")
    void onBeforeSaveWhenInValidMessageReturnsSuccess() {
        // Set up mocked entities
        TodoDTO postDTO = mockFactory.newDTO(null);
        postDTO.setStatus(TodoStatus.ON_GOING.getValue());
        Todo entity = mapper.getMapperIn().getDestination(postDTO);
        int VARIABLES = 2;
        TodoMessage message = TodoMessage.builder()
                .text("This a message with variables. {{prospect_name}} So it should be an invalid message. Another variable {{company_name}}. Repeat variable {{prospect_name}}")
                .build();
        entity.setMessage(message);

        // Execute the service call
        listener.handleBeforeSaveEvent(new BaseServiceBeforeSaveEvent<>(postDTO, entity));
        TodoMessage messageEntity = entity.getMessage();

        // Assertions
        Assertions.assertAll(String.format("Validate Message: [%s]", message),
                () -> Assertions.assertFalse(
                        messageEntity.isValid(),
                        String.format("Message [%s] must be valid", entity.getMessage().getText())),
                () -> Assertions.assertNotNull(messageEntity.getTemplate(), "Template can not be null"),
                () -> Assertions.assertFalse(messageEntity.getTemplate().getVariables().isEmpty(), "Template variables map can not be null"),
                () -> Assertions.assertEquals(
                        VARIABLES,
                        messageEntity.getTemplate().getVariables().size(),
                        String.format("THere must be [%d] variables in the variables map", VARIABLES)));

        Assertions.assertNotNull(messageEntity.getTemplate(), "Template can not be null");

    }
}
