package com.sawoo.pipeline.api.controller;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.prospect.LeadDTO;
import com.sawoo.pipeline.api.dto.prospect.LeadMainDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectType;
import com.sawoo.pipeline.api.service.LeadServiceOld;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/leads2")
public class LeadControllerOld {

    private final LeadServiceOld service;

    @RequestMapping(
            value = {"/", "/{type}",},
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LeadDTO> save(
            @PathVariable(required = false) ProspectType type,
            @Valid @RequestBody LeadDTO lead) {
        LeadDTO newEntity = service.create(lead, type != null ? type.getType(): ProspectType.PROSPECT.getType());
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
    public ResponseEntity<List<LeadDTO>> getAll() {
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
    public ResponseEntity<LeadDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.DELETE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LeadDTO> delete(@PathVariable Long id) throws ResourceNotFoundException {
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
            @RequestBody LeadDTO lead,
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

    @RequestMapping(
            value = "/{id}/report",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_PDF_VALUE})
    public ResponseEntity<InputStreamResource> getReport(
            @NotNull @PathVariable("id") Long id,
            @RequestParam(value = "template", required = false) String template,
            @RequestParam(value = "lan", required = false) String lan) {
        byte[] pdfBytes = service.getReport(id, template, lan);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment; filename=" + template + "." + id + ".pdf").build());
        return ResponseEntity
                .ok()
                .contentLength(pdfBytes.length)
                .headers(headers)
                .body( new InputStreamResource(new ByteArrayInputStream(pdfBytes)) );
    }
}
