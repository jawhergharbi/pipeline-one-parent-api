package com.sawoo.pipeline.api.controller.sequence;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerTest;
import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
import com.sawoo.pipeline.api.mock.SequenceMockFactory;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.service.sequence.SequenceService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@Tag(value = "controller")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
public class SequenceControllerTest extends BaseControllerTest<SequenceDTO, Sequence, SequenceService, SequenceMockFactory> {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SequenceService service;

    @Autowired
    public SequenceControllerTest(SequenceMockFactory mockFactory, SequenceService service, MockMvc mockMvc) {
        super(mockFactory,
                ControllerConstants.SEQUENCE_CONTROLLER_API_BASE_URI,
                DBConstants.SEQUENCE_DOCUMENT,
                service,
                mockMvc);
    }

    @Override
    protected String getExistCheckProperty() {
        return "id";
    }

    @Override
    protected List<String> getResourceFieldsToBeChecked() {
        return Arrays.asList("name", "description", "created");
    }

    @Override
    protected Class<SequenceDTO> getDTOClass() {
        return SequenceDTO.class;
    }
}
