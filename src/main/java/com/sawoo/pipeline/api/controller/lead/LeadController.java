package com.sawoo.pipeline.api.controller.lead;

import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.audit.VersionDTO;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

   @PostMapping(
            value = { "", "/{type}"},
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

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<LeadDTO>> getAll() {
        return delegator.findAll();
    }

    @GetMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LeadDTO> findById(@PathVariable String id) {
        return delegator.findById(id);
    }

    @GetMapping(
            value = "/{id}/versions",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<VersionDTO<LeadDTO>>> getVersions(@PathVariable String id) {
        return delegator.getVersions(id);
    }

    @DeleteMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LeadDTO> delete(@PathVariable String id) {
        return delegator.deleteById(id);
    }

    @DeleteMapping(
            value = "/{id}/summary",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LeadDTO> deleteLeadSummary(@PathVariable String id) {
        return delegator.deleteLeadSummary(id);
    }

    @DeleteMapping(
            value = "/{id}/company-summary",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LeadDTO> deleteLeadCompanyComments(@PathVariable String id) {
        return delegator.deleteLeadCompanyComments(id);
    }

    @DeleteMapping(
            value = "/{id}/qualification-notes",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LeadDTO> deleteLeadQualificationNotes(@PathVariable String id) {
        return delegator.deleteLeadQualificationComments(id);
    }

    @PutMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(
            @RequestBody LeadDTO dto,
            @PathVariable("id") String id) {
        return delegator.update(id, dto);
    }

    @GetMapping(
            value = "/{id}/report",
            produces = {MediaType.APPLICATION_PDF_VALUE})
    public ResponseEntity<InputStreamResource> getReport(
            @PathVariable("id") String id,
            @RequestParam(value = "template", required = false) String template,
            @RequestParam(value = "lan", required = false) String lan) {
        return delegator.getReport(id, template, lan);
    }

    @PostMapping(
            value = "/{id}/interactions",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<InteractionDTO> addInteraction(
            @PathVariable("id") String leadId,
            @NotNull @RequestBody InteractionDTO interaction) {
        return delegator.addInteraction(leadId, interaction);
    }

    @DeleteMapping(
            value = "/{id}/interactions/{interactionId}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<InteractionDTO> removeInteraction(
            @PathVariable("id") String leadId,
            @PathVariable("interactionId") String interactionId) throws ResourceNotFoundException {
        return delegator.removeInteraction(leadId, interactionId);
    }

    @GetMapping(
            value = "/{id}/interactions",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<InteractionAssigneeDTO>> getInteractions(
            @PathVariable("id") String leadId) {
        return delegator.getInteractions(leadId);
    }

    @GetMapping(
            value = "/{id}/interactions/{interactionId}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<InteractionAssigneeDTO> getInteraction(
            @PathVariable("id") String leadId,
            @PathVariable("interactionId") String interactionId) {
        return delegator.getInteraction(leadId, interactionId);
    }

    @GetMapping(
            value = "/{id}/sequences/{sequenceId}/interactions/eval",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<InteractionAssigneeDTO>> getInteractions(
            @PathVariable("id") String leadId,
            @PathVariable("sequenceId") String sequenceId) {
        return delegator.evalInteractions(leadId, sequenceId);
    }
}
