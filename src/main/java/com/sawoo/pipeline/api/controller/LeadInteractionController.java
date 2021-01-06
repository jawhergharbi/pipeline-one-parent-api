package com.sawoo.pipeline.api.controller;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.prospect.LeadInteractionDTOOld;
import com.sawoo.pipeline.api.dto.prospect.LeadInteractionRequestDTO;
import com.sawoo.pipeline.api.service.LeadInteractionServiceOld;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/leads/old")
public class LeadInteractionController {

    private final LeadInteractionServiceOld service;

    @RequestMapping(
            value = "/{leadId}/interactions",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LeadInteractionDTOOld> create(
            @NotNull @PathVariable("leadId") Long leadId,
            @Valid @RequestBody LeadInteractionRequestDTO interaction) {
        LeadInteractionDTOOld newEntity = service.create(leadId, interaction);
        try {
            return ResponseEntity
                    .created(new URI("/api/leads/" + leadId + "/interactions/" + newEntity.getId()))
                    .body(newEntity);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(
            value = "/{leadId}/interactions",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<LeadInteractionDTOOld>> getAll(
            @NotNull @PathVariable("leadId") Long leadId) {
        return ResponseEntity.ok().body(service.findAll(leadId));
    }

    @RequestMapping(
            value = "/{leadId}/interactions/{id}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LeadInteractionDTOOld> get(
            @NotNull @PathVariable("leadId") Long leadId,
            @NotNull @PathVariable("id") Long id) {
        return ResponseEntity.ok().body(service.findById(leadId, id));
    }

    @RequestMapping(
            value = "/{leadId}/interactions/{id}",
            method = RequestMethod.DELETE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LeadInteractionDTOOld> delete(
            @NotNull @PathVariable("leadId") Long leadId,
            @NotNull @PathVariable("id") Long id) throws ResourceNotFoundException {
        return service
                .delete(leadId, id)
                .map((interaction) -> ResponseEntity.ok().body(interaction))
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_DELETE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{
                                        "LeadInteraction",
                                        String.format(
                                                "Lead id: [%d]. Interaction id: [%d]",
                                                leadId,
                                                id)}));
    }

    @RequestMapping(
            value = "/{leadId}/interactions/{id}",
            method = RequestMethod.PUT,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(
            @NotNull @PathVariable("leadId") Long leadId,
            @NotNull @PathVariable("id") Long id,
            @RequestBody LeadInteractionRequestDTO lead) throws ResourceNotFoundException {
        return service.update(leadId, id, lead)
                .map((updatedEntity) -> {
                    try {
                        return ResponseEntity
                                .ok()
                                .location(new URI("/api/leads/" + leadId + "/interactions/" + updatedEntity.getId() ))
                                .body(updatedEntity);
                    } catch (URISyntaxException exc) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                })
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_DELETE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{"Lead", String.valueOf(id)}));
    }

}
