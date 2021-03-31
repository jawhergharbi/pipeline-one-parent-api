package com.sawoo.pipeline.api.controller.person;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.audit.VersionDTO;
import com.sawoo.pipeline.api.dto.person.PersonDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ControllerConstants.PERSON_CONTROLLER_API_BASE_URI)
public class PersonController {

    private final PersonControllerDelegator delegator;

    @PostMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<PersonDTO> create(@RequestBody PersonDTO dto) {
        return delegator.create(dto);
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<PersonDTO>> getAll() {
        return delegator.findAll();
    }

   @GetMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<PersonDTO> get(@PathVariable String id) {
        return delegator.findById(id);
    }

    @GetMapping(
            value = "/{id}/versions",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<VersionDTO<PersonDTO>>> getVersions(@PathVariable String id) {
        return delegator.getVersions(id);
    }

    @DeleteMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<PersonDTO> delete(@PathVariable String id) {
        return delegator.deleteById(id);
    }

    @PutMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(
            @RequestBody PersonDTO dto,
            @PathVariable("id") String id) {
        return delegator.update(id, dto);
    }
}
