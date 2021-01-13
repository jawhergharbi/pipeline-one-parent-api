package com.sawoo.pipeline.api.controller.person;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.controller.base.BaseControllerDelegator;
import com.sawoo.pipeline.api.dto.person.PersonDTO;
import com.sawoo.pipeline.api.service.person.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PersonControllerDelegator extends BaseControllerDelegator<PersonDTO, PersonService> {

    @Autowired
    public PersonControllerDelegator(PersonService service) {
        super(service, ControllerConstants.PERSON_CONTROLLER_API_BASE_URI);
    }

    @Override
    public String getComponentId(PersonDTO dto) {
        return dto.getId();
    }
}
