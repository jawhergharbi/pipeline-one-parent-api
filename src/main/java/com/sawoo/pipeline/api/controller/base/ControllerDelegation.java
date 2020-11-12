package com.sawoo.pipeline.api.controller.base;

import org.springframework.http.ResponseEntity;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

public interface ControllerDelegation<D> {

    ResponseEntity<D> create(@Valid D dto);

    ResponseEntity<List<D>> findAll();

    ResponseEntity<D> findById(String id);

    ResponseEntity<D> deleteById(@NotBlank String id);

    ResponseEntity<?> update(String id, D dto);

    String getComponentId(D dto);
}
