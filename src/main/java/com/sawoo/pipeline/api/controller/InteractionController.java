package com.sawoo.pipeline.api.controller;

import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.service.InteractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interactions")
public class InteractionController {

    private final InteractionService service;

    @RequestMapping(
            value = "",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<InteractionDTO>> findBy(
            @RequestParam(value = "types", required = false) Integer[] types,
            @RequestParam(value = "status", required = false) Integer[] status,
            @RequestParam(value = "clients", required = false) Long[] clients) {
        List<InteractionDTO> lst = service.findBy(types, status, clients);
        return ResponseEntity.ok().body(lst);
    }
}
