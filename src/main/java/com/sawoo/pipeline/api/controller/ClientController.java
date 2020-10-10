package com.sawoo.pipeline.api.controller;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.client.ClientBasicDTO;
import com.sawoo.pipeline.api.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@FunctionalInterface
interface UpdateClientFunction<T, S, R> {
    R apply(T clientId, S user);
}

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService service;

    @RequestMapping(
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ClientBasicDTO> create(@Valid @RequestBody ClientBasicDTO client) {
        ClientBasicDTO newEntity = service.create(client);
        try {
            return ResponseEntity
                    .created(new URI("/api/clients/" + newEntity.getId()))
                    .body(newEntity);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<ClientBasicDTO>> getAll() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @RequestMapping(
            value = "/main/{datetime}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<ClientBasicDTO>> getAllMain(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @NotBlank @PathVariable("datetime") LocalDateTime datetime) {
        List<ClientBasicDTO> lst = service.findAllMain(datetime);
        return ResponseEntity.ok().body(lst);
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ClientBasicDTO> get(@PathVariable Long id) throws ResourceNotFoundException {
        return service.findById(id)
                .map(ResponseEntity.ok()::body)
                .orElseThrow(() ->  newClientResourceNotFoundException(
                        ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION, id));
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.DELETE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ClientBasicDTO> delete(@PathVariable Long id) throws ResourceNotFoundException {
        return service
                .delete(id)
                .map(ResponseEntity.ok()::body)
                .orElseThrow(() -> newClientResourceNotFoundException(
                        ExceptionMessageConstants.COMMON_DELETE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION, id));
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.PUT,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(
            @RequestBody ClientBasicDTO client,
            @NotNull @PathVariable("id") Long id) throws ResourceNotFoundException {
        return updateResponse(getUpdate(), id, client);
    }

    @RequestMapping(
            value = "/{id}/csm/{userId}",
            method = RequestMethod.PUT,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updateCSM(
            @NotNull @PathVariable("id") Long id,
            @NotBlank @PathVariable("userId") String userId) throws ResourceNotFoundException {
        return updateResponse(getUpdateCSM(), id, userId);
    }

    @RequestMapping(
            value = "/{id}/sa/{userId}",
            method = RequestMethod.PUT,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updateSA(
            @NotNull @PathVariable("id") Long id,
            @NotBlank @PathVariable("userId") String userId) throws ResourceNotFoundException {
        return updateResponse(getUpdateSA(), id, userId);
    }

    private ResponseEntity<?> updateResponse(
            UpdateClientFunction<Long, String, Optional<ClientBasicDTO>> update,
            Long id,
            String userId) throws ResourceNotFoundException {
        return update.apply(id, userId)
                .map(this::buildUpdateResponse)
                .orElseThrow(() -> newClientResourceNotFoundException(
                        ExceptionMessageConstants.COMMON_UPDATE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION, id));
    }

    private ResponseEntity<?> updateResponse(
            UpdateClientFunction<Long, ClientBasicDTO, Optional<ClientBasicDTO>> update,
            Long id,
            ClientBasicDTO clientDTO) throws ResourceNotFoundException {
        return update.apply(id, clientDTO)
                .map(this::buildUpdateResponse)
                .orElseThrow(() -> newClientResourceNotFoundException(
                        ExceptionMessageConstants.COMMON_UPDATE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION, id));
    }

    private ResponseEntity<?> buildUpdateResponse(ClientBasicDTO updatedClient) {
        try {
            return ResponseEntity
                    .ok()
                    .location(new URI("/api/clients/" + updatedClient.getId()))
                    .body(updatedClient);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ResourceNotFoundException newClientResourceNotFoundException(String message, Long clientId) {
        return new ResourceNotFoundException(
                message,
                new String[]{"Client", String.valueOf(clientId)});
    }

    private UpdateClientFunction<Long, String, Optional<ClientBasicDTO>> getUpdateSA() {
        return service::updateSA;
    }

    private UpdateClientFunction<Long, String, Optional<ClientBasicDTO>> getUpdateCSM() {
        return service::updateCSM;
    }

    private UpdateClientFunction<Long, ClientBasicDTO, Optional<ClientBasicDTO>> getUpdate() {
        return service::update;
    }
}
