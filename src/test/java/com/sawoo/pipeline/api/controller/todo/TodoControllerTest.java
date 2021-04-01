package com.sawoo.pipeline.api.controller.todo;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerTest;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.mock.TodoMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.common.Note;
import com.sawoo.pipeline.api.model.todo.Todo;
import com.sawoo.pipeline.api.service.todo.TodoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@Tag(value = "controller")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
class TodoControllerTest extends BaseControllerTest<TodoDTO, Todo, TodoService, TodoMockFactory> {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService service;

    @Autowired
    public TodoControllerTest(TodoMockFactory mockFactory, TodoService service, MockMvc mockMvc) {
        super(mockFactory,
                ControllerConstants.TODO_CONTROLLER_API_BASE_URI,
                DBConstants.TODO_DOCUMENT,
                service,
                mockMvc);
    }

    @Override
    protected String getExistCheckProperty() {
        return "id";
    }

    @Override
    protected List<String> getResourceFieldsToBeChecked() {
        return Arrays.asList("scheduled", "created");
    }

    @Override
    protected Class<TodoDTO> getDTOClass() {
        return TodoDTO.class;
    }

    @Test
    @DisplayName("POST /api/todos: scheduled datetime not informed - Failure")
    void createWhenScheduledNotInformedReturnsFailure() throws Exception {
        // Setup the mocked entities
        TodoDTO postEntity = getMockFactory().newDTO(null);
        postEntity.setScheduled(null);

        // Execute the POST request
        mockMvc.perform(post(getResourceURI())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    @Test
    @DisplayName("PUT /api/todos/{id}: resource exists - Success")
    void updateWhenResourceFoundAndUpdatedReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String ENTITY_ID = getMockFactory().getComponentId();
        String TODO_MESSAGE_TEXT = getMockFactory().getFAKER().lorem().sentence(10);
        Note note = Note.builder()
                .text(TODO_MESSAGE_TEXT)
                .updated(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        TodoDTO postEntity = TodoDTO
                .builder()
                .note(note)
                .build();
        TodoDTO mockedDTOEntity = getMockFactory().newDTO(ENTITY_ID);
        mockedDTOEntity.setNote(note);


        // setup the mocked service
        doReturn(mockedDTOEntity).when(service).update(anyString(), any(TodoDTO.class));

        // Execute the PUT request
        mockMvc.perform(put(getResourceURI() + "/{id}", ENTITY_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, getResourceURI() + "/" + ENTITY_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(ENTITY_ID)))
                .andExpect(jsonPath("$.scheduled").exists())
                .andExpect(jsonPath("$.note.text", is(TODO_MESSAGE_TEXT)));
    }
}
