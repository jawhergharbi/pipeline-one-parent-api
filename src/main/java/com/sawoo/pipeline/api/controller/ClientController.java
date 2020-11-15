package com.sawoo.pipeline.api.controller;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.client.ClientBasicDTO;
import com.sawoo.pipeline.api.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
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
}
