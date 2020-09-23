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
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<ClientBasicDTO>> getAll(
            @NotBlank @PathVariable("id") String id) {
        return ResponseEntity.ok().body(service.findAll(id));
    }
}
