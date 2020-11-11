package com.sawoo.pipeline.api.controller.base;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ControllerDelegation<D> {

    ResponseEntity<D> create(D dto);

    ResponseEntity<List<D>> findAll();

    ResponseEntity<D> findById(String id);

    ResponseEntity<D> deleteById(String id);

    ResponseEntity<D> update(String id, D dto);

    String getComponentId(D dto);
}
