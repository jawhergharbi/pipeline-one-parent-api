package com.sawoo.pipeline.api.controller;

import com.sawoo.pipeline.api.dto.lead.LeadBasicDTO;
import com.sawoo.pipeline.api.dto.lead.LeadMainDTO;
import com.sawoo.pipeline.api.service.ClientLeadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clients")
public class ClientLeadController {

    private final ClientLeadService service;

    @RequestMapping(
            value = "/{id}/leads",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> create(
            @NotNull @PathVariable("id") Long clientId,
            @Valid @RequestBody LeadBasicDTO lead) {
        LeadBasicDTO newEntity = service.create(clientId, lead);
        try {
            return ResponseEntity
                    .created(new URI("/api/clients/" + clientId + "/leads/" + newEntity.getId()))
                    .body(newEntity);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(
            value = "/{id}/leads/{leadId}",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> add(
            @NotNull @PathVariable("id") Long clientId,
            @NotNull @PathVariable("leadId") Long leadId) {
        LeadBasicDTO newEntity = service.add(clientId, leadId);
        try {
            return ResponseEntity
                    .created(new URI("/api/clients/" + clientId + "/leads/" + newEntity.getId()))
                    .body(newEntity);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(
            value = "/{id}/leads/{leadId}",
            method = RequestMethod.DELETE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LeadBasicDTO> delete(
            @NotNull @PathVariable("id") Long clientId,
            @NotNull @PathVariable("leadId") Long leadId) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.remove(clientId, leadId));
    }

    @RequestMapping(
            value = "/{id}/leads",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<LeadBasicDTO>> getClientAll(
            @NotNull @PathVariable("id") Long clientId) {
        return ResponseEntity.ok().body(service.findAll(clientId));
    }

    @RequestMapping(
            value = "/all/leads/main/{datetime}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<LeadMainDTO>> getAll(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @NotBlank @PathVariable("datetime") LocalDateTime datetime) {
        return ResponseEntity.ok().body(service.findAllMain(datetime));
    }

    @RequestMapping(
            value = "/{ids}/leads/main/{datetime}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<LeadMainDTO>> getClientsAll2(
            @NotNull
            @PathVariable("ids") List<Long> ids,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @NotBlank @PathVariable("datetime") LocalDateTime datetime) {
        return ResponseEntity.ok().body(service.findClientsMain(ids, datetime));
    }
}
