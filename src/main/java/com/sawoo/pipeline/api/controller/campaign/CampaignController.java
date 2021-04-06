package com.sawoo.pipeline.api.controller.campaign;

import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.audit.VersionDTO;
import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.dto.campaign.CampaignProspectDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignProspectAddDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignProspectBaseDTO;
import com.sawoo.pipeline.api.dto.campaign.request.CampaignProspectCreateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ControllerConstants.CAMPAIGN_CONTROLLER_API_BASE_URI)
public class CampaignController {

    private final CampaignControllerDelegator delegator;

    @PostMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CampaignDTO> create(@RequestBody CampaignDTO dto) {
        return delegator.create(dto);
    }

    @GetMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<CampaignDTO>> getAll() {
        return delegator.findAll();
    }

   @GetMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CampaignDTO> get(@PathVariable String id) {
        return delegator.findById(id);
    }

    @GetMapping(
            value = "/{id}/versions",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<VersionDTO<CampaignDTO>>> getVersions(@PathVariable String id) {
        return delegator.getVersions(id);
    }

    @DeleteMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CampaignDTO> delete(@PathVariable String id) {
        return delegator.deleteById(id);
    }

    @PutMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(
            @RequestBody CampaignDTO dto,
            @PathVariable("id") String id) {
        return delegator.update(id, dto);
    }

    @GetMapping(
            value = "/accounts/{accountIds}/main",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<CampaignDTO>> findByAccountIds(
            @PathVariable(value = "accountIds") Set<String> accountIds) {
        return delegator.findByAccounts(accountIds);
    }

    @PostMapping(
            value = "/{id}/"  + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CampaignProspectDTO> createProspect(
            @PathVariable("id") String id,
            @RequestBody CampaignProspectCreateDTO dto) {
        return delegator.createProspect(id, dto);
    }

    @PostMapping(
            value = "/{id}/"  + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME + "/{leadId}",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CampaignProspectDTO> addProspect(
            @PathVariable("id") String id,
            @PathVariable("leadId") String prospectId,
            @RequestBody CampaignProspectAddDTO dto) {
        dto.setProspectId(prospectId);
        return delegator.addProspect(id, dto);
    }

    @PutMapping(
            value = "/{id}/"  + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME + "/{leadId}",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CampaignProspectDTO> updateProspect(
            @PathVariable("id") String id,
            @PathVariable("leadId") String prospectId,
            @RequestBody CampaignProspectBaseDTO dto) {
        dto.setProspectId(prospectId);
        return delegator.updateProspect(id, prospectId, dto);
    }

    @DeleteMapping(
            value = "/{id}/"  + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME + "/{leadId}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CampaignProspectDTO> removeProspect(
            @PathVariable("id") String id,
            @PathVariable("leadId") String prospectId) {
        return delegator.removeProspect(id, prospectId);
    }

    @GetMapping(
            value = "/{id}/" + ControllerConstants.PROSPECT_CONTROLLER_RESOURCE_NAME,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<CampaignProspectDTO>> findAllProspects(
            @PathVariable(value = "id") String id) {
        return delegator.findAllProspects(id);
    }
}
