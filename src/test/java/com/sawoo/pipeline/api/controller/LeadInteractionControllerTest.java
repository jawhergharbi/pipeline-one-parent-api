package com.sawoo.pipeline.api.controller;

import com.sawoo.pipeline.api.common.BaseControllerTest;
import com.sawoo.pipeline.api.dto.UrlTitleDTO;
import com.sawoo.pipeline.api.dto.lead.LeadInteractionDTO;
import com.sawoo.pipeline.api.dto.lead.LeadInteractionRequestDTO;
import com.sawoo.pipeline.api.service.LeadInteractionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class LeadInteractionControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeadInteractionService service;

    @Test
    @DisplayName("POST /api/leads/{leadId}/interactions: leadInteraction create - Success")
    void createWhenLeadInteractionDoesExistReturnsSuccess() throws Exception {
        // Setup the mocked entities
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        int INTERACTION_STATUS = FAKER.number().numberBetween(0, 4);
        int INTERACTION_CHANNEL = FAKER.number().numberBetween(0, 3);
        Long LEAD_INTERACTION_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        String URL_INVITE = FAKER.internet().url();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        LeadInteractionRequestDTO postDTO = newMockedPostDTO(INTERACTION_STATUS, INTERACTION_CHANNEL, URL_INVITE);
        postDTO.setScheduled(now);
        postDTO.setDateTime(now);

        LeadInteractionDTO mockedDTO = newMockedDTOEntity(LEAD_INTERACTION_ID, INTERACTION_STATUS, INTERACTION_CHANNEL, URL_INVITE);
        mockedDTO.setCreated(now);
        mockedDTO.setUpdated(now);

        // setup the mocked service
        doReturn(mockedDTO).when(service).create(LEAD_ID, postDTO);

        // Execute the POST request
        mockMvc.perform(post("/api/leads/{leadId}/interactions", LEAD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postDTO))
        )

                // Validate the response code and the content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/leads/" + LEAD_ID + "/interactions/" + LEAD_INTERACTION_ID))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(LEAD_INTERACTION_ID.intValue())))
                .andExpect(jsonPath("$.status", is(INTERACTION_STATUS)))
                .andExpect(jsonPath("$.type", is(INTERACTION_CHANNEL)))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.updated").exists())
                .andExpect(jsonPath("$.note").doesNotExist());
    }

    @Test
    @DisplayName("POST /api/leads/{leadId}/interactions: leadInteraction create - Failure")
    void createWhenLeadInteractionDoesExistReturnsFailure() throws Exception {
        // Setup the mocked entities
        Long LEAD_ID = FAKER.number().numberBetween(1, (long) Integer.MAX_VALUE);
        int INTERACTION_STATUS = FAKER.number().numberBetween(0, 4);
        int INTERACTION_CHANNEL = FAKER.number().numberBetween(0, 3);
        String URL_INVITE = FAKER.internet().url();
        LeadInteractionRequestDTO postDTO = newMockedPostDTO(INTERACTION_STATUS, INTERACTION_CHANNEL, URL_INVITE);

        // Execute the POST request
        mockMvc.perform(post("/api/leads/{leadId}/interactions", LEAD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postDTO)))

                // Validate the response code and the content type
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate the returned fields
                .andExpect(jsonPath("$.messages", hasSize(2)));
    }

    private LeadInteractionRequestDTO newMockedPostDTO(int status, int type, String urlInvite) {
        LeadInteractionRequestDTO mockedDTO = new LeadInteractionRequestDTO();
        mockedDTO.setStatus(status);
        mockedDTO.setType(type);
        mockedDTO.setInvite(UrlTitleDTO.builder().url(urlInvite).build());
        return mockedDTO;
    }

    private LeadInteractionDTO newMockedDTOEntity(Long id, int status, int type, String urlInvite) {
        LeadInteractionDTO mockedDTO = new LeadInteractionDTO();
        mockedDTO.setId(id);
        mockedDTO.setStatus(status);
        mockedDTO.setType(type);
        mockedDTO.setInvite(UrlTitleDTO.builder().url(urlInvite).build());
        return mockedDTO;
    }
}
