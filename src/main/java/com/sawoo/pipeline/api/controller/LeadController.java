package com.sawoo.pipeline.api.controller;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.lead.LeadBasicDTO;
import com.sawoo.pipeline.api.dto.lead.LeadMainDTO;
import com.sawoo.pipeline.api.service.LeadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/leads")
public class LeadController {

    private final LeadService service;

    @RequestMapping(
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LeadBasicDTO> save(@Valid @RequestBody LeadBasicDTO lead) {
        LeadBasicDTO newEntity = service.create(lead);
        try {
            return ResponseEntity
                    .created(new URI("/api/leads/" + newEntity.getId()))
                    .body(newEntity);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<LeadBasicDTO>> getAll() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @RequestMapping(
            value = "/main/{datetime}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<LeadMainDTO>> getAllMain(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @NotBlank  @PathVariable("datetime")LocalDateTime datetime) {
        List<LeadMainDTO> lst = service.findAllMain(datetime);
        return ResponseEntity.ok().body(lst);
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LeadBasicDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.DELETE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LeadBasicDTO> delete(@PathVariable Long id) throws ResourceNotFoundException {
        return service
                .delete(id)
                .map((lead) -> ResponseEntity.ok().body(lead))
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_DELETE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{"Lead", String.valueOf(id)}));
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.PUT,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(
            @RequestBody LeadBasicDTO lead,
            @PathVariable("id") Long id) {
        return service.update(id, lead)
                .map((updatedEntity) -> {
                    try {
                        return ResponseEntity
                                .ok()
                                .location(new URI("/api/leads/" + updatedEntity.getId()))
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
