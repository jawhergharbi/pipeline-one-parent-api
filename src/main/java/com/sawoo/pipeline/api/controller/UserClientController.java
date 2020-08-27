package com.sawoo.pipeline.api.controller;

import com.sawoo.pipeline.api.dto.client.ClientBasicDTO;
import com.sawoo.pipeline.api.service.UserClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/")
public class UserClientController {

    private final UserClientService service;

    @RequestMapping(
            value = "/{id}/clients",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ClientBasicDTO> create(
            @NotBlank @PathVariable("id") String id,
            @Valid @RequestBody ClientBasicDTO client) {
        ClientBasicDTO newEntity = service.create(id, client);
        try {
            return ResponseEntity
                    .created(new URI("/api/users/" + id + "/clients/" + newEntity.getId()))
                    .body(newEntity);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(
            value = "/{id}/clients/{clientId}",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> add(
            @NotBlank @PathVariable("id") String id,
            @NotBlank @PathVariable("clientId") Long clientId) {
        ClientBasicDTO newEntity = service.add(id, clientId);
        try {
            return ResponseEntity
                    .created(new URI("/api/users/" + id + "/clients/" + newEntity.getId()))
                    .body(newEntity);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(
            value = "/{id}/clients",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<ClientBasicDTO>> getAll(
            @NotBlank @PathVariable("id") String id) {
        return ResponseEntity.ok().body(service.findAll(id));
    }

    @RequestMapping(
            value = "/{id}/clients/{clientId}",
            method = RequestMethod.DELETE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ClientBasicDTO> delete(
            @NotNull @PathVariable("id") String id,
            @NotNull @PathVariable("clientId") Long clientId) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.remove(id, clientId));
    }
}
