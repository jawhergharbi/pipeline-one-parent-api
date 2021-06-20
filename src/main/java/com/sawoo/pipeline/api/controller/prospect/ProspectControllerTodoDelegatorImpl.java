package com.sawoo.pipeline.api.controller.prospect;

import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.prospect.ProspectTodoDTO;
import com.sawoo.pipeline.api.dto.todo.TodoAssigneeDTO;
import com.sawoo.pipeline.api.dto.todo.TodoDTO;
import com.sawoo.pipeline.api.model.todo.TodoSearch;
import com.sawoo.pipeline.api.service.prospect.ProspectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Qualifier("prospectControllerTODO")
public class ProspectControllerTodoDelegatorImpl implements ProspectControllerTodoDelegator {

    private final ProspectService service;

    @Autowired
    public ProspectControllerTodoDelegatorImpl(ProspectService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<List<TodoDTO>> addTODOs(String prospectId, List<TodoDTO> todos)
            throws ResourceNotFoundException, CommonServiceException {
        List<TodoDTO> createdTODOs = service.addTODOList(prospectId, todos);
        try {
            String ids = createdTODOs.stream().map(TodoDTO::getId).collect(Collectors.joining(","));
            return ResponseEntity
                    .created(new URI(ControllerConstants.PROSPECT_CONTROLLER_API_BASE_URI
                            + "/"
                            + prospectId
                            + "/"
                            + ControllerConstants.TODO_CONTROLLER_RESOURCE_NAME
                            + "/"
                            + ids))
                    .body(createdTODOs);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<TodoDTO> removeTODO(String prospectId, String todoId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(service.removeTODO(prospectId, todoId));
    }

    @Override
    public ResponseEntity<List<TodoAssigneeDTO>> getTODOs(String prospectId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(service.getTODOs(prospectId));
    }

    @Override
    public ResponseEntity<TodoAssigneeDTO> getTODO(String prospectId, String todoId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(service.getTODO(prospectId, todoId));
    }

    @Override
    public ResponseEntity<List<ProspectTodoDTO>> searchTODOs(TodoSearch searchCriteria) {
        return ResponseEntity.ok().body(service.searchBy(searchCriteria));
    }
}
