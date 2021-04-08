package com.sawoo.pipeline.api.service.todo;

import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.mock.AccountMockFactory;
import com.sawoo.pipeline.api.mock.TodoMockFactory;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.todo.Todo;
import com.sawoo.pipeline.api.model.todo.TodoStatus;
import com.sawoo.pipeline.api.model.user.User;
import com.sawoo.pipeline.api.model.user.UserRole;
import com.sawoo.pipeline.api.service.account.AccountServiceEventListener;
import com.sawoo.pipeline.api.service.base.event.BaseServiceBeforeInsertEvent;
import com.sawoo.pipeline.api.service.base.event.BaseServiceBeforeUpdateEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;
import java.util.HashSet;

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
    void onBeforeInsertWhenStatusNoInformedReturnsSuccess() {
        // Set up mocked entities
        TodoDTO postDTO = mockFactory.newDTO(null);
        postDTO.setStatus(null);
        Todo entity = mapper.getMapperIn().getDestination(postDTO);

        // Execute the service call
        listener.handleBeforeInsertEvent(new BaseServiceBeforeInsertEvent<>(postDTO, entity));

        Assertions.assertNotNull(entity.getStatus(), "Status must be informed");
        Assertions.assertEquals(
                TodoStatus.PENDING.getValue(),
                entity.getStatus(),
                String.format("Status must contain the default value: [%d]", TodoStatus.PENDING.getValue()));
    }
}
