package com.sawoo.pipeline.api.controller.interaction;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerTest;
import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.mock.InteractionMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.common.Note;
import com.sawoo.pipeline.api.model.interaction.Interaction;
import com.sawoo.pipeline.api.service.interaction.InteractionService;
import org.junit.jupiter.api.*;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@Tag(value = "controller")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class InteractionControllerTest extends BaseControllerTest<InteractionDTO, Interaction, InteractionService, InteractionMockFactory> {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InteractionService service;

    @Autowired
    public InteractionControllerTest(InteractionMockFactory mockFactory, InteractionService service, MockMvc mockMvc) {
        super(mockFactory,
                ControllerConstants.INTERACTION_CONTROLLER_API_BASE_URI,
                DBConstants.INTERACTION_DOCUMENT,
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
    protected Class<InteractionDTO> getDTOClass() {
        return InteractionDTO.class;
    }

    @Test
    @DisplayName("POST /api/interactions: scheduled datetime not informed - Failure")
    void createWhenScheduledNotInformedReturnsFailure() throws Exception {
        // Setup the mocked entities
        InteractionDTO postEntity = getMockFactory().newDTO(null);
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
    @DisplayName("PUT /api/interactions/{id}: resource exists - Success")
    void updateWhenResourceFoundAndUpdatedReturnsSuccess() throws Exception {
        // Setup the mocked entities
        String INTERACTION_ID = getMockFactory().getComponentId();
        String INTERACTION_NOTE_TEXT = getMockFactory().getFAKER().lorem().sentence(10);
        Note note = Note.builder()
                .text(INTERACTION_NOTE_TEXT)
                .updated(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        InteractionDTO postEntity = InteractionDTO
                .builder()
                .note(note)
                .build();
        InteractionDTO mockedDTOEntity = getMockFactory().newDTO(INTERACTION_ID);
        mockedDTOEntity.setNote(note);


        // setup the mocked service
        doReturn(mockedDTOEntity).when(service).update(anyString(), any(InteractionDTO.class));

        // Execute the PUT request
        mockMvc.perform(put(getResourceURI() + "/{id}", INTERACTION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postEntity)))

                // Validate the response code and the content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, getResourceURI() + "/" + INTERACTION_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(INTERACTION_ID)))
                .andExpect(jsonPath("$.scheduled").exists())
                .andExpect(jsonPath("$.note.text", is(INTERACTION_NOTE_TEXT)));
    }
}
