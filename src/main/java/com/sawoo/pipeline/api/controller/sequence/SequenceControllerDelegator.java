package com.sawoo.pipeline.api.controller.sequence;

import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerDelegator;
import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
import com.sawoo.pipeline.api.service.sequence.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Primary
public class SequenceControllerDelegator extends BaseControllerDelegator<SequenceDTO, SequenceService> implements SequenceControllerUserDelegator {

    private final SequenceControllerUserDelegator userDelegator;

    @Autowired
    public SequenceControllerDelegator(
            SequenceService service,
                @Qualifier("sequenceUserController") SequenceControllerUserDelegator userDelegator) {
        super(service, ControllerConstants.SEQUENCE_CONTROLLER_API_BASE_URI);
        this.userDelegator = userDelegator;
    }

    @Override
    public String getComponentId(SequenceDTO dto) {
        return dto.getId();
    }

    @Override
    public ResponseEntity<SequenceDTO> deleteUser(String id, String userId) throws ResourceNotFoundException, CommonServiceException {
        return userDelegator.deleteUser(id, userId);
    }
}
