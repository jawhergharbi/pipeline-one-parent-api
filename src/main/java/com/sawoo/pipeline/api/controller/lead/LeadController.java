package com.sawoo.pipeline.api.controller.lead;

import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.interaction.InteractionAssigneeDTO;
import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.dto.lead.LeadTypeRequestParam;
import com.sawoo.pipeline.api.model.common.Status;
import com.sawoo.pipeline.api.model.lead.LeadStatusList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/leads")
public class LeadController {

    private final LeadControllerDelegator delegator;

   @RequestMapping(
            value = { "", "/{type}"},
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LeadDTO> save(
            @PathVariable(required = false) LeadTypeRequestParam type,
            @NotNull @RequestBody LeadDTO lead) {
        if (type != null && type.equals(LeadTypeRequestParam.LEAD)) {
            lead.setStatus(Status
                    .builder()
                    .value(LeadStatusList.HOT.getStatus())
                    .updated(LocalDateTime.now()).build());
        } else {
            lead.setStatus(Status
                    .builder()
                    .value(LeadStatusList.FUNNEL_ON_GOING.getStatus())
                    .updated(LocalDateTime.now())
                    .build());
        }
        return delegator.create(lead);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<LeadDTO>> getAll() {
        return delegator.findAll();
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LeadDTO> findById(@PathVariable String id) {
        return delegator.findById(id);
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.DELETE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LeadDTO> delete(@PathVariable String id) {
        return delegator.deleteById(id);
    }

    @RequestMapping(
            value = "/{id}/summary",
            method = RequestMethod.DELETE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LeadDTO> deleteLeadSummary(@PathVariable String id) {
        return delegator.deleteById(id);
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.PUT,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(
            @RequestBody LeadDTO dto,
            @PathVariable("id") String id) {
        return delegator.update(id, dto);
    }

    @RequestMapping(
            value = "/{id}/report",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_PDF_VALUE})
    public ResponseEntity<InputStreamResource> getReport(
            @PathVariable("id") String id,
            @RequestParam(value = "template", required = false) String template,
            @RequestParam(value = "lan", required = false) String lan) {
        return delegator.getReport(id, template, lan);
    }

    @RequestMapping(
            value = "/{id}/interactions",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<InteractionDTO> addInteraction(
            @PathVariable("id") String leadId,
            @NotNull @RequestBody InteractionDTO interaction) {
        return delegator.addInteraction(leadId, interaction);
    }

    @RequestMapping(
            value = "/{id}/interactions/{interactionId}",
            method = RequestMethod.DELETE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<InteractionDTO> removeInteraction(
            @PathVariable("id") String leadId,
            @PathVariable("interactionId") String interactionId) throws ResourceNotFoundException {
        return delegator.removeInteraction(leadId, interactionId);
    }

    @RequestMapping(
            value = "/{id}/interactions",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<InteractionAssigneeDTO>> getInteractions(
            @PathVariable("id") String leadId) {
        return delegator.getInteractions(leadId);
    }

    @RequestMapping(
            value = "/{id}/interactions/{interactionId}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<InteractionAssigneeDTO> getInteraction(
            @PathVariable("id") String leadId,
            @PathVariable("interactionId") String interactionId) {
        return delegator.getInteraction(leadId, interactionId);
    }
}
