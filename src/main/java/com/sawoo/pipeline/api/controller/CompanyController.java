package com.sawoo.pipeline.api.controller;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
public class CompanyController {

    protected final CompanyService service;

    @RequestMapping(
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<CompanyDTO>> getAll() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CompanyDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.DELETE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CompanyDTO> delete(@PathVariable Long id) throws ResourceNotFoundException {
        return service
                .delete(id)
                .map((company) -> ResponseEntity.ok().body(company))
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_DELETE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{"Company", String.valueOf(id)}));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CompanyDTO> save(@Valid @RequestBody CompanyDTO company) {
        CompanyDTO newEntity = service.create(company);
        try {
            return ResponseEntity
                    .created(new URI("/api/companies/" + newEntity.getId()))
                    .body(newEntity);
        } catch (URISyntaxException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.PUT,
            produces = { MediaType.APPLICATION_JSON_VALUE},
            consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> update(@RequestBody CompanyDTO company,
                                    @PathVariable Long id) {
        return service.update(id, company)
                .map( (updatedEntity) -> {
                    try {
                        return ResponseEntity
                                .ok()
                                .location( new URI("/api/companies/" + updatedEntity.getId()) )
                                .body(updatedEntity);
                    } catch (URISyntaxException exc) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                })
                .orElseThrow( () ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_DELETE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{"Company", String.valueOf(id)}));
    }
}
