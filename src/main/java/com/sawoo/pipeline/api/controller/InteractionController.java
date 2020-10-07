package com.sawoo.pipeline.api.controller;

import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.service.InteractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/leads/interactions")
public class InteractionController {

    private final InteractionService service;

    @RequestMapping(
            value = "/types/{types}/clients/{clients}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<InteractionDTO>> getByTypes(
            @PathVariable("types") Integer[] types,
            @PathVariable("clients") Long[] clients) {
        List<InteractionDTO> lst = service.getByType(types, clients);
        return ResponseEntity.ok().body(lst);
    }
}
